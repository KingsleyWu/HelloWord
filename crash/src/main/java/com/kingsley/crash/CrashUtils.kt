package com.kingsley.crash

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import com.kingsley.extend.DefaultActivityLifecycleCallbacks
import java.io.File
import java.util.*

object CrashUtils : Thread.UncaughtExceptionHandler {
    private const val TAG = "CrashUtils"
    private const val CREATED = "Created"
    private const val RESUMED = "Resumed"

    private const val PAUSED = "Paused"
    private const val STOPPED = "Stopped"

    private val mDefaultCrashHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()
    private var mInitialized = false
    var packageName: String = ""
    var versionName: String = ""
    var versionCode: Long = 0
    lateinit var mCrashDir: String
    private var mCurActivityName: String = ""
    private lateinit var mApp: Application
    private var mCrashListener: CrashListener? = null
    private val mActivityNameTask = Stack<String>()
    private val mActivityLifecycleState = mutableMapOf<String, String>()
    /** 用於寫入錯誤文件的開始位置 */
    var mCrashExtraContent = ""
    private var isWriteLog = true

    private val mCrashActivityLifecycleCallbacks = object : DefaultActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            val activityName = activity::class.java.name
            mActivityNameTask.add(activityName)
            mActivityLifecycleState[activityName] = CREATED
            Log.d(TAG, "onActivityCreated: $activityName")
        }

        override fun onActivityResumed(activity: Activity) {
            val activityName = activity::class.java.name
            Log.d(TAG, "onActivityResumed $activityName")
            mCurActivityName = activity::class.java.name
            mActivityLifecycleState[activityName] = RESUMED
        }

        override fun onActivityPaused(activity: Activity) {
            val activityName = activity::class.java.name
            mActivityLifecycleState[activityName] = PAUSED
        }

        override fun onActivityStopped(activity: Activity) {
            val activityName = activity::class.java.name
            mActivityLifecycleState[activityName] = STOPPED
        }

        override fun onActivityDestroyed(activity: Activity) {
            val activityName = activity::class.java.name
            mActivityNameTask.remove(activityName)
            mActivityLifecycleState.remove(activityName)
            Log.d(TAG, "onActivityDestroyed $activityName")
        }
    }

    @JvmOverloads
    fun init(app: Application, listener: CrashListener? = null) {
        mApp = app
        mCrashListener = listener
        if (mInitialized) {
            return
        }
        app.registerActivityLifecycleCallbacks(mCrashActivityLifecycleCallbacks)
        try {
            packageName = app.packageName
            val packageManager = app.packageManager
            val pi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            pi?.let {
                versionName = pi.versionName
                versionCode = PackageInfoCompat.getLongVersionCode(pi)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        mCrashDir =
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && app.externalCacheDir != null) {
                app.externalCacheDir!!.absolutePath + File.separator + "crash" + File.separator
            } else {
                app.cacheDir.absolutePath + File.separator + "crash" + File.separator
            }
        Thread.setDefaultUncaughtExceptionHandler(this)
        mInitialized = true
    }

    fun setWriteLog(writeLog: Boolean) {
        isWriteLog = writeLog
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        val isHandle = handleException(t, e)
        handleExceptionWithListener(t, e)
        if (mDefaultCrashHandler != null && !isHandle) {
            mDefaultCrashHandler.uncaughtException(t, e)
        } else {
            mCrashListener?.afterHandleException()
        }
    }

    private fun handleExceptionWithListener(t: Thread, e: Throwable) {
        try {
            mCrashListener?.recordException(t, e)
        } catch (e : Exception){
            e.printStackTrace()
            mDefaultCrashHandler?.uncaughtException(t, e)
        }
    }

    private fun handleException(t: Thread, e: Throwable) : Boolean {
        if (e.localizedMessage != null) {
            //收集设备信息
            //保存错误报告文件
            if (isWriteLog) {
                CrashFileUtils.saveCrashInfoInFile(mApp, t, e)
            }
        }
        return false
    }

    fun setCrashDir(crashDir: String) {
        mCrashDir = crashDir
    }

    fun getCurActivityInfo() : Pair<String, String>? {
        if (!mActivityNameTask.isEmpty()) {
            val activityName = mActivityNameTask.lastElement()
            val activityLifecycleState =  mActivityLifecycleState[activityName]
            if (!activityLifecycleState.isNullOrEmpty()) {
                return Pair(activityName, activityLifecycleState)
            }
        }
        return null
    }
}