package com.kingsley.tetris

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import kotlin.jvm.Volatile
import com.kingsley.tetris.util.ActivityStackManager
import java.lang.ref.WeakReference

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        /**
         * 用来保存当前该Application的context
         */
        @SuppressLint("StaticFieldLeak")
        private var instance: Context? = null

        /**
         * 用来保存最新打开页面的context
         */
        @Volatile
        private var instanceRef: WeakReference<Context?>? = null

        /**
         * 该函数用来返回一个context，一般情况下为当前activity的context，如果为空，
         * 就会调用[ActivityStackManager.getActivity]方法去获取栈顶context,
         * 但是如果activity没有调用 [.setInstanceRef]方法去设置context,
         * 就会使用整个Application的context，相当于[.getApplicationContext],
         * 不推荐使用该方法，特别是耗时任务，因为会导致页面销毁时，任务无法回收，导致内存泄露和
         * 其他异常
         *
         * @return context上下文，如果返回Null检测manifest文件是否设置了application的name
         */
        fun getInstance(): Context? {
            if (instanceRef == null || instanceRef!!.get() == null) {
                synchronized(BaseApplication::class.java) {
                    if (instanceRef == null || instanceRef!!.get() == null) {
                        val context: Context? = ActivityStackManager.instance!!.activity
                        instanceRef = if (context != null) {
                            WeakReference(context)
                        } else {
                            WeakReference(instance)
                        }
                    }
                }
            }
            return instanceRef!!.get()
        }

        /**
         * 将[.instanceRef]设置为最新页面的context
         *
         * @param context 最新页面的context
         */
        fun setInstanceRef(context: Context?) {
            instanceRef = WeakReference(context)
        }
    }
}