package com.kingsley.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.Delayed

/**
 * @author Kingsley
 * Created on 2021/5/18.
 * desc: 包含：加載，無內容，無網絡，吐司,出錯顯示
 *       不包含：顯示內容
 */
abstract class BaseViewModel : ViewModel() {

    /** 使用 private 以避免来自其他类对此状态的更新 */
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)

    /** UI 从此 StateFlow 收集以获取其状态更新 */
    val uiState: StateFlow<UiState> = _uiState

    fun showLoading() {
        _uiState.value = UiState.Loading
    }

    fun showNoNet() {
        _uiState.value = UiState.NoNet
    }

    fun showEmpty(msg: CharSequence? = null) {
        _uiState.value = UiState.Empty(msg)
    }

    fun <T> showContent(data: T) {
        _uiState.value = UiState.ShowContent(data)
    }

    fun showError(errorMsg: CharSequence? = null) {
        _uiState.value = UiState.Error(errorMsg)
    }

    /**
     * 在主线程中执行一个协程
     */
    protected fun launchOnUI(delayed: Long = 0, block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) {
            delay(delayed)
            block() }
    }

    /**
     * 在IO线程中执行一个协程
     */
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) { block() }
    }

    fun <T, R> launchWithIoMain(
        io: (suspend () -> T)? = null,
        main: ((T?) -> R)? = null,
        error: ((e: Exception?) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = io?.invoke()
                withContext(Dispatchers.Main) {
                    main?.invoke(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error?.invoke(e)
                }
            }
        }
    }
}

/**
 * 代表屏幕的不同状态
 */
sealed class UiState {
    object Loading : UiState()
    object NoNet : UiState()
    data class Empty(val msg: CharSequence?) : UiState()
    data class ShowContent<T>(val data: T?): UiState()
    data class Error(val errorMsg: CharSequence?) : UiState()
}