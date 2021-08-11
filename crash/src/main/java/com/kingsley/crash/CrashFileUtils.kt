package com.kingsley.crash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception

internal object CrashFileUtils {

    @SuppressLint("DEPRECATION")
    fun saveCrashInfoInFile(t: Thread, e: Throwable) {
        val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val baseCrashInfo = StringBuilder().apply {
            //组合Android相关信息
            append("\n软件App的Id:").append(CrashUtils.packageName)
            append("\n是否是DEBUG版本:").append(BuildConfig.BUILD_TYPE)
            append("\n崩溃的时间:").append(dataFormat.format(System.currentTimeMillis()))
            append("\n是否root:").append(isDeviceRooted())
            append("\n系统硬件商:").append(Build.MANUFACTURER)
            append("\n设备的品牌:").append(Build.BRAND)
            append("\n手机的型号:").append(getModel())
            append("\n设备版本号:").append(Build.ID)
            append("\nCPU的类型:")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                append(Build.SUPPORTED_ABIS)
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
        }
        return false
    }

    /**
     * 获取设备 AndroidID
     *
     * @return AndroidID
     */
    @SuppressLint("HardwareIds")
    fun getAndroidID(context: Context): String? {
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
    fun getModel(): String {
        var model = Build.MODEL
        model = model?.trim { it <= ' ' }?.replace("\\s*".toRegex(), "") ?: ""
        return model
    }
}