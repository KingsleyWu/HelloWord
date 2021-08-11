package com.kingsley.helloword

import android.app.Application
import com.kingsley.base.L
import com.kingsley.crash.CrashUtils
import com.kingsley.network.NetworkUtils
import com.tencent.mmkv.MMKV

/**
 * @author Kingsley
 * Created on 2021/5/14.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        L.setDebug(true)
        val rootDir = MMKV.initialize(this)
        L.d("mmkv root: $rootDir")
        NetworkUtils.registerNetworkCallback(this)
        CrashUtils.init(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        L.d("level = onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        L.d("level = $level , onTrimMemory")
        if (level == TRIM_MEMORY_UI_HIDDEN) {
        }
    }
}