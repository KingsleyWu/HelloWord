package com.kingsley.helloword.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingsley.base.BaseViewModel
import com.kingsley.base.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : BaseViewModel() {

    val onSettingClick = MutableLiveData<Boolean>()

    fun test() : String? {
        var test : String? = ""
        val job = launchOnUI {
            test = "$test\n父协程 --> 已启动"
            val a = async {
                test = "$test\nasync --> delay(2000)"
                delay(2000)
                test = "$test\nasync --> 执行完毕"
            }
            test = "$test\nasync --> 启动"
            a.await()
        }
        test = "$test\n主线程 --> sleep(500)"
        Thread.sleep(500)
        job.cancel()
        test = "$test\n父协程 --> 已取消"
        test = "$test\n主线程 --> 执行完毕"
        return test
    }
}