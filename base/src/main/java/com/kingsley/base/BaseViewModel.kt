package com.kingsley.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Kingsley
 * Created on 2021/5/18.
 * desc: 包含：加載，無內容，無網絡，吐司
 *       不包含：顯示內容及出錯顯示
 */
abstract class BaseViewModel : ViewModel() {

    /** 加載中 */
    private var _loadingLiveData = MutableLiveData<Boolean>()
    var loadingLiveData : LiveData<Boolean> = _loadingLiveData

    /** 無內容 */
    private var _emptyLiveData = MutableLiveData<Boolean>()
    var emptyLiveData : LiveData<Boolean> = _emptyLiveData

    /** 無網絡 */
    private var _noNetLiveData = MutableLiveData<Boolean>()
    var noNetLiveData : LiveData<Boolean> = _noNetLiveData

    /** 顯示吐司 */
    private var _toastLiveData = MutableLiveData<String>()
    var toastLiveData : LiveData<String> = _toastLiveData

    /**
     * 在主线程中执行一个协程
     */
    protected fun launchOnUI(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.Main) { block() }
    }

    /**
     * 在IO线程中执行一个协程
     */
    protected fun launchOnIO(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(Dispatchers.IO) { block() }
    }

    fun showLoading(isLoading: Boolean) {
        _loadingLiveData.postValue(isLoading)
    }

    fun showEmpty(isEmpty: Boolean) {
        _emptyLiveData.postValue(isEmpty)
    }

    fun showNoNet(isNoNet: Boolean) {
        _noNetLiveData.postValue(isNoNet)
    }

    fun showToast(msg: String) {
        _toastLiveData.postValue(msg)
    }

}