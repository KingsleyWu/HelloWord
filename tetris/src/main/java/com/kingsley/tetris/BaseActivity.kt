package com.kingsley.tetris

import com.kingsley.tetris.util.ActivityStackManager.Companion.instance
import android.app.Activity
import android.os.Bundle

open class BaseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApplication.setInstanceRef(this)
        instance?.addActivity(this)
    }

    override fun onResume() {
        super.onResume()
        //也要在onresume函数里面进行设置，保证弱引用一直引用当前的可见页面
        BaseApplication.setInstanceRef(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance?.removeActivity(this)
    }
}