package com.kingsley.common.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

object AppLifeObserver : DefaultLifecycleObserver {

    var isForeground = MutableLiveData<Boolean>()

    /**
     * 在前台
     */
    override fun onStart(owner: LifecycleOwner) {
        isForeground.value = true
    }

    /**
     * 在后台
     */
    override fun onStop(owner: LifecycleOwner) {
        isForeground.value = false
    }

}