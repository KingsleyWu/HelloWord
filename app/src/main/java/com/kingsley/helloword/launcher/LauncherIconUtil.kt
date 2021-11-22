package com.kingsley.helloword

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import com.kingsley.common.L

/**
 * Created by kingsley on 2021/11/18.
 * 更换图标
 */
object LauncherIconHelp {
    private val packageManager: PackageManager = HelloWordApplication.app.packageManager
    private val componentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, Launcher1::class.java.name),
        ComponentName(HelloWordApplication.app, Launcher2::class.java.name)
    )

    fun changeIcon(icon: String?) {
        L.d("changeIcon icon : $icon")
        if (icon.isNullOrEmpty()) return
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        var hasEnabled = false
        componentNames.forEach {
            if (icon.equals(it.className.substringAfterLast("."), true)) {
                hasEnabled = true
                //启用
                packageManager.setComponentEnabledSetting(
                    it,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                //禁用
                packageManager.setComponentEnabledSetting(
                    it,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
        packageManager.setComponentEnabledSetting(
            ComponentName(HelloWordApplication.app, MainActivity::class.java.name),
            if (hasEnabled) PackageManager.COMPONENT_ENABLED_STATE_DISABLED else PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}