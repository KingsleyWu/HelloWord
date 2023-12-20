package com.kingsley.common

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.*

object EventBus {
    lateinit var app: Application

    /**
     * EventBus init
     */
    @JvmStatic
    fun init(app: Application) {
        EventBus.app = app
    }

    /**
     * 发送 Application 范围的事件
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    inline fun <reified T> post(event: T, isSticky: Boolean = false, timeMillis: Long = 0L) {
        AppScopeProvider.get().post(T::class.java.name, isSticky, event, timeMillis)
    }

    /**
     * 发送 Application 范围的事件
     * @param eventName 事件名稱
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    fun post(eventName: String, event: Any?, isSticky: Boolean = false, timeMillis: Long = 0L) {
        AppScopeProvider.get().post(eventName, isSticky, event, timeMillis)
    }

    /**
     * 发送 Activity 范围的事件
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    inline fun <reified T> ComponentActivity.post(event: T, isSticky: Boolean = false, timeMillis: Long = 0L) {
        ViewModelProvider(this)[EventBusViewModel::class.java].post(T::class.java.name, isSticky, event, timeMillis)
    }

    /**
     * 发送 Activity 范围的事件
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    fun ComponentActivity.post(eventName: String, event: Any?, isSticky: Boolean = false, timeMillis: Long = 0L) {
        ViewModelProvider(this)[EventBusViewModel::class.java].post(eventName, isSticky, event, timeMillis)
    }

    /**
     * 发送 Fragment 范围的事件
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    inline fun <reified T> Fragment.post(event: T, isSticky: Boolean = false, timeMillis: Long = 0L) {
        ViewModelProvider(this)[EventBusViewModel::class.java].post(T::class.java.name, isSticky, event, timeMillis)
    }

    /**
     * 发送 Fragment 范围的事件
     * @param event 事件
     * @param isSticky 是否是粘性事件
     * @param timeMillis 延时发送间隔
     */
    @JvmStatic
    @JvmOverloads
    fun Fragment.post(eventName: String, event: Any?, isSticky: Boolean = false, timeMillis: Long = 0L) {
        ViewModelProvider(this)[EventBusViewModel::class.java].post(eventName, isSticky, event, timeMillis)
    }

    /**
     * 移除 Application scope 粘性事件
     */
    @JvmStatic
    fun removeStick(eventName: String) {
        AppScopeProvider.get().removeStick(eventName)
    }

    /**
     * 移除 Fragment scope 粘性事件
     */
    @JvmStatic
    fun Fragment.removeStick(eventName: String) {
        ViewModelProvider(this)[EventBusViewModel::class.java].removeStick(eventName)
    }

    /**
     * 移除 ComponentActivity scope 粘性事件
     */
    @JvmStatic
    fun ComponentActivity.removeStick(eventName: String) {
        ViewModelProvider(this)[EventBusViewModel::class.java].removeStick(eventName)
    }
}

object AppScopeProvider : ViewModelStoreOwner {

    /**
     * EventBus Application ViewModelStore
     */
    private val eventViewModelStore: ViewModelStore = ViewModelStore()

    override val viewModelStore: ViewModelStore
        get() = eventViewModelStore

    /**
     * EventBus Application ViewModelProvider
     */
    private val mViewModelProvider = ViewModelProvider(AppScopeProvider, ViewModelProvider.AndroidViewModelFactory.getInstance(EventBus.app))

    /**
     * get application scope ViewModel
     */
    @JvmStatic
    fun <T : ViewModel> get(modelClass: Class<T>): T {
        return mViewModelProvider[modelClass]
    }

    /**
     * get application scope EventBusViewModel
     */
    @JvmStatic
    fun get() = mViewModelProvider[EventBusViewModel::class.java]

}

/**
 * 事件监听回调注册，Application Scope 范围的事件
 * @param dispatcher 协程调度器
 * @param minActiveState 生命週期 state
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
fun <T> LifecycleOwner.observeAppEvent(
    eventName: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    onReceived: (T?) -> Unit
) : Job {
    return AppScopeProvider.get().observeEvent(this, eventName, dispatcher, minActiveState, isSticky, onReceived)
}

/**
 * 事件监听回调注册，Application Scope 范围的事件
 * @param dispatcher 协程调度器
 * @param minActiveState 生命週期 state
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
inline fun <reified T> LifecycleOwner.observeAppEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    noinline onReceived: (T?) -> Unit
) : Job {
    return AppScopeProvider.get().observeEvent(this, T::class.java.name, dispatcher, minActiveState, isSticky, onReceived)
}

/**
 * 事件监听回调注册，Application Scope, Activity Scope, Fragment Scope 范围的事件
 * @param dispatcher 协程调度器
 * @param minActiveState 生命週期 state
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
inline fun <reified T> LifecycleOwner.observeEvent(
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    isGlobal: Boolean = false,
    noinline onReceived: (T?) -> Unit
) : Job {
    val bus = when {
        isGlobal -> AppScopeProvider.get()
        this is Fragment -> ViewModelProvider(this)[EventBusViewModel::class.java]
        this is ComponentActivity -> ViewModelProvider(this)[EventBusViewModel::class.java]
        else -> AppScopeProvider.get()
    }
    return bus.observeEvent(this, T::class.java.name, dispatcher, minActiveState, isSticky, onReceived)
}

/**
 * 事件监听回调注册，Application Scope, Activity Scope, Fragment Scope 范围的事件
 * @param dispatcher 协程调度器
 * @param minActiveState 生命週期 state
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
fun <T> LifecycleOwner.observeEvent(
    eventName: String,
    dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    isSticky: Boolean = false,
    isGlobal: Boolean = false,
    onReceived: (T?) -> Unit
) : Job {
    val bus = when {
        isGlobal -> AppScopeProvider.get()
        this is Fragment -> ViewModelProvider(this)[EventBusViewModel::class.java]
        this is ComponentActivity -> ViewModelProvider(this)[EventBusViewModel::class.java]
        else -> AppScopeProvider.get()
    }
    return bus.observeEvent(this, eventName, dispatcher, minActiveState, isSticky, onReceived)
}

/**
 * 事件监听回调注册，没有生命周期
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
inline fun <reified T> CoroutineScope.observeAppEvent(isSticky: Boolean = false, noinline onReceived: (T?) -> Unit) = launch {
    AppScopeProvider.get().observeWithoutLifecycle(T::class.java.name, isSticky, onReceived)
}

/**
 * 事件监听回调注册，没有生命周期
 * @param isSticky 是否是粘性事件
 * @param onReceived 事件回调
 */
@JvmOverloads
fun <T> CoroutineScope.observeAppEvent(eventName: String, isSticky: Boolean = false, onReceived: (T?) -> Unit) = launch {
    AppScopeProvider.get().observeWithoutLifecycle(eventName, isSticky, onReceived)
}