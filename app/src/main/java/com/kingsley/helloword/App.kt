package com.kingsley.helloword

import android.app.Application
import com.kingsley.base.L
import com.tencent.mmkv.MMKV




/**
 * @author Kingsley
 * Created on 2021/5/14.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        L.setDebug(true)
        val rootDir = MMKV.initialize(this)
        L.d("mmkv root: $rootDir")
    }
}