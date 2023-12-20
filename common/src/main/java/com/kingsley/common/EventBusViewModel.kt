package com.kingsley.common

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

class EventBusViewModel : ViewModel() {

    /** 普通事件 */
    private val normalEventFlows = mutableMapOf<String, MutableSharedFlow<Any?>>()

    /** 粘性事件 */
    private val stickyEventFlows = mutableMapOf<String, MutableSharedFlow<Any?>>()

    /**
     * 获取事件的 Flow
     */
    private fun getEventFlow(eventName: String, isSticky: Boolean): MutableSharedFlow<Any?> {
        return if (isSticky) {
            stickyEventFlows[eventName]
        } else {
            normalEventFlows[eventName]
        } ?: MutableSharedFlow<Any?>(if (isSticky) 1 else 0, Int.MAX_VALUE).also {
            if (isSticky) {
                stickyEventFlows[eventName] = it
            } else {
                normalEventFlows[eventName] = it
            }
        }
    }

    /**
     * 监听事件
     * @param lifecycleOwner
     * @param eventName
     * @param minState
     * @param dispatcher 协程调度器 如 #Dispatchers.Main.immediate，Dispatchers.Io
     * @param isSticky 是否是粘性事件
     * @param onReceived
     */
    @JvmOverloads
    fun <T : Any> observeEvent(
        lifecycleOwner: LifecycleOwner,
        eventName: String,
        dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
        minState: Lifecycle.State = Lifecycle.State.STARTED,
        isSticky: Boolean = false,
        onReceived: (T?) -> Unit
    ): Job {
        return lifecycleOwner.launchWhenStateAtLeast(minState) {
            lifecycleOwner.lifecycleScope.launch {
                getEventFlow(eventName, isSticky).collect { value ->
                    this.launch(dispatcher) {
                        invokeReceived(value, onReceived)
                    }
                }
            }
        }
    }

    @JvmOverloads
    suspend fun <T : Any> observeWithoutLifecycle(
        eventName: String,
        isSticky: Boolean = false,
        onReceived: (T?) -> Unit
    ) {
        getEventFlow(eventName, isSticky).collect { value ->
            invokeReceived(value, onReceived)
        }
    }

    @JvmOverloads
    fun post(eventName: String, value: Any? = null, timeMillis: Long = 0) {
        post(getEventFlow(eventName, false), value, timeMillis)
    }

    @JvmOverloads
    fun post(eventName: String, isSticky: Boolean, value: Any? = null, timeMillis: Long = 0) {
        post(getEventFlow(eventName, isSticky), value, timeMillis)
    }

    @JvmOverloads
    fun postSticky(eventName: String, value: Any? = null, timeMillis: Long = 0) {
        post(getEventFlow(eventName, true), value, timeMillis)
    }

    fun removeStick(eventName: String) {
        stickyEventFlows.remove(eventName)
    }

    private fun post(flow: MutableSharedFlow<Any?>, value: Any?, timeMillis: Long) {
        viewModelScope.launch {
            delay(timeMillis)
            flow.emit(value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> invokeReceived(value: Any?, onReceived: (T?) -> Unit) = onReceived(value as? T)

}

fun <T> LifecycleOwner.launchWhenStateAtLeast(
    minState: Lifecycle.State,
    block: () -> T
): Job {
    return lifecycleScope.launch {
        lifecycle.withStateAtLeast(minState, block)
    }
}