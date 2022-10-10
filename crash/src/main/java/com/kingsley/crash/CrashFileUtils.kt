package com.kingsley.crash

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

internal object CrashFileUtils {

    @SuppressLint("DEPRECATION", "ObsoleteSdkInt")
    fun saveCrashInfoInFile(context: Context, t: Thread, e: Throwable) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val crashTime = System.currentTimeMillis()
        val crashInfo = StringBuilder().run {
            //组合Android相关信息
            append("\n软件App的Id:").append(CrashUtils.packageName)
            append("\n是否是DEBUG版本:").append(BuildConfig.BUILD_TYPE)
            append("\n崩溃的时间:").append(dataFormat.format(crashTime))
            append("\n是否root:").append(isDeviceRooted())
            append("\n系统硬件商:").append(Build.MANUFACTURER)
            append("\n设备的品牌:").append(Build.BRAND)
            append("\n手机的型号:").append(getModel())
            append("\n设备版本号:").append(Build.ID)
            append("\nCPU的类型:")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (index in Build.SUPPORTED_ABIS.indices){
                    append(Build.SUPPORTED_ABIS[index]).append(" ")
                }
            } else {
                @Suppress("DEPRECATION")
                append(Build.CPU_ABI)
            }
            append("\n系统的版本:").append(Build.VERSION.RELEASE)
            append("\n系统版本值:").append(Build.VERSION.SDK_INT)
            append("\n当前的版本:").append(CrashUtils.versionName).append("—")
                .append(CrashUtils.versionCode)
            append("\n\n")
        }
        crashInfo.run {
            append("手机内存分析:")
            val pssInfo = getAppPssInfo(context, Process.myPid())
            append("\ndalvik堆大小:").append(getFormatSize(pssInfo.dalvikPss.toDouble()))
            append("\nnative堆使用的内存:").append(getFormatSize(pssInfo.nativePss.toDouble()))
            append("\nPSS内存使用量:").append(getFormatSize(pssInfo.totalPss.toDouble()))
            append("\n其他比例大小:").append(getFormatSize(pssInfo.otherPss.toDouble()))

            append("\n已用内存:").append(getFormatSize((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toDouble()))
            append("\n最大内存:").append(getFormatSize(Runtime.getRuntime().maxMemory().toDouble()))
            append("\n空闲内存:").append(getFormatSize(Runtime.getRuntime().freeMemory().toDouble()))
            append("\n总可用内存:").append(getFormatSize((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).toDouble()))
            append("\n应用最大可使用的内存:").append(getAppTotalDalvikHeapSize(context)).append("MB")
            append("\n")
            val memoryInfo = getMemoryInfo(context)
            append("\n系统总内存:").append(getFormatSize(memoryInfo.totalMem.toDouble()))
            append("\n系统剩余内存:").append(getFormatSize(memoryInfo.availMem.toDouble()))
            append("\n系统是否处于低内存运行:").append(memoryInfo.lowMemory)
            append("\n系统剩余内存低于").append(getFormatSize(memoryInfo.threshold.toDouble())).append("时为低内存运行")
            append("\n\n")
        }
        crashInfo.run {
            append("该App信息:")
            append("\n进程号:").append(Process.myPid())
            append("\n当前线程号:").append(Process.myTid())
            append("\n当前调用该进程的用户号:").append(Process.myUid())
            append("\n当前线程ID:").append(t.id)
            append("\n当前线程名称:").append(t.name)
            append("\n主线程ID:").append(context.mainLooper.thread.id)
            append("\n主线程名称:").append(context.mainLooper.thread.name)
            append("\n主线程优先级:").append(context.mainLooper.thread.priority)
            val curActivityInfo = CrashUtils.getCurActivityInfo()
            if (curActivityInfo != null) {
                append("\n当前Activity名称:").append(curActivityInfo.first)
                append("\n当前Activity处于的生命周期:").append(curActivityInfo.second)
            }
            append("\n\n")
        }
        dumpExceptionToFile(crashInfo, e, crashTime)
    }

    private fun dumpExceptionToFile(crashInfo : StringBuilder, ex: Throwable, crashTime: Long) {
        var pw: PrintWriter? = null
        try {
            //Log保存路径
            // SDCard/Android/data/<application package>/cache/crash
            // data/data/<application package>/cache/crash
            val dir = File(CrashUtils.mCrashDir)
            if (!dir.exists() && !dir.mkdirs()) {
                return
            }
            //Log文件的名字
            val fileName = "V${CrashUtils.versionName}_$crashTime"
            val file = File(dir, fileName)
            if (!file.exists()) {
                val createNewFileOk = file.createNewFile()
                if (!createNewFileOk) {
                    return
                }
            }
            //开始写日志
            pw = PrintWriter(BufferedWriter(FileWriter(file)))
            //判断有没有额外信息需要写入
            if (!TextUtils.isEmpty(CrashUtils.mCrashExtraContent)) {
                pw.println(CrashUtils.mCrashExtraContent)
            }
            //写入设备信息
            pw.println(crashInfo)
            //导出异常的调用栈信息
            ex.printStackTrace(pw)
            //异常信息
            var cause = ex.cause
            while (cause != null) {
                cause.printStackTrace(pw)
                cause = cause.cause
            }
            //重新命名文件
            val string = ex.toString()
            val splitEx = if (string.contains(":")) {
                ex.toString().split(":".toRegex()).toTypedArray()[0]
            } else {
                "java.lang.Exception"
            }
            val newName = "${fileName}_$splitEx"
            val newFile = File(dir, newName)
            //重命名文件
            file.renameTo(newFile)
        } catch (e: Exception) {
           e.printStackTrace()
        } finally {
            pw?.close()
        }
    }

    /**
     * 获取应用实际占用内存
     *
     * @return 应用pss信息KB
     */
    private fun getAppPssInfo(context: Context, pid: Int): Debug.MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return am.getProcessMemoryInfo(intArrayOf(pid))[0]
    }


    /**
     * 在App中获取内存信息
     *
     * @return 在App中获取内存信息
     */
    private fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        return info
    }

    /**
     * 获取应用能够获取的max dalvik堆内存大小
     * 和Runtime.getRuntime().maxMemory()一样
     *
     * @return 单位M
     */
    private fun getAppTotalDalvikHeapSize(context: Context): Int {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.memoryClass
    }


    /**
     * 判断设备是否 root
     *
     * @return the boolean`true`: 是<br></br>`false`: 否
     */
    private fun isDeviceRooted(): Boolean {
        try {
            val su = "su"
            val locations = arrayOf(
                "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 获取设备 AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    private fun getAndroidID(context: Context): String? {
        return try {
            Settings.Secure.getString(
                context.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        } catch (e: Exception) {
            return ""
        }
    }

    /**
     * 获取设备型号
     *
     * 如 MI2SC
     *
     * @return 设备型号
     */
    private fun getModel(): String {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }


    /**
     * 格式化单位
     * @param size
     * @return
     */
    private fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "Byte"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, RoundingMode.HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, RoundingMode.HALF_UP).toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, RoundingMode.HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, RoundingMode.HALF_UP).toPlainString() + "TB"
    }
}