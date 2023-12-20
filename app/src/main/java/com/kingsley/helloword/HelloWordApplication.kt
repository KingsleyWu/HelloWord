package com.kingsley.helloword

import android.app.Application
import com.kingsley.common.EventBus
import com.kingsley.common.L
import com.kingsley.crash.CrashUtils
import com.kingsley.network.NetworkUtils
import com.tencent.mmkv.MMKV

/**
 * @author Kingsley
 * Created on 2021/5/14.
 */
class HelloWordApplication : Application() {

    companion object{
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        L.setDebug(true)
        val rootDir = MMKV.initialize(this)
        L.d("mmkv root: $rootDir")
        NetworkUtils.registerNetworkCallback(this)
        CrashUtils.init(this)
        EventBus.init(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        L.d("level = onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        L.d("level = $level , onTrimMemory")
        when(level) {
            // 当您的应用程序运行时：
            TRIM_MEMORY_RUNNING_MODERATE -> {
                // 5 设备内存开始不足。该进程不是可消耗的后台进程，应用程序正在运行且不可终止。
            }
            TRIM_MEMORY_RUNNING_LOW -> {
                // 10 该设备的运行内存變少。该进程不是可消耗的后台进程，应用程序正在运行且不可终止，但请释放未使用的资源以提高系统性能（这直接影响您的应用程序的性能）。
            }
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // 15 该设备的运行内存极低。该进程不是可消耗的后台进程，应用尚未被视为可终止进程，但如果应用不释放资源，系统将开始终止后台进程，因此您应该立即释放非关键资源以防止性能下降。
            }
            // 当您的应用程序的可见性发生变化时：
            TRIM_MEMORY_UI_HIDDEN -> {
                // 20 应用程序的用户界面不再可见，因此这是释放"仅供您的用户界面使用的大量资源"的好时机。
            }
            // 当你的应用进程驻留在后台 LRU 列表中时：
            TRIM_MEMORY_BACKGROUND -> {
                // 40 系统内存不足，进程接近后台 LRU 列表的开头。虽然你的应用进程被杀的风险不高，但系统可能已经在杀掉LRU列表中的进程，所以你应该释放容易回收的资源，让你的进程留在列表中，并在用户访问时快速恢复返回到您的应用程序。
            }
            TRIM_MEMORY_MODERATE -> {
                // 60 系统内存不足，进程接近后台 LRU 列表的中间。如果系统进一步受内存限制，则您的进程有可能被终止。
            }
            TRIM_MEMORY_COMPLETE -> {
                // 80 系统内存不足，进程接近后台 LRU 列表的末尾。如果系统现在不恢复内存，您的进程将是最先被杀死的进程之一。您应该绝对释放对恢复应用状态不重要的所有内容。
            }
            else -> {

            }
        }
    }
}