package com.kingsley.common.lifecycle

import androidx.lifecycle.*

object AppLifeObserver : DefaultLifecycleObserver{

    var isForeground = MutableLiveData<Boolean>()

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // 在前台
        isForeground.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // 在后台
        isForeground.value = false
    }

}