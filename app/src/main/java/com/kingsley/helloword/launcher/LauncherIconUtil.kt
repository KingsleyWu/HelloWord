package com.kingsley.helloword.launcher

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import com.kingsley.helloword.HelloWordApplication
import com.kingsley.helloword.MainActivity

/**
 * Created by kingsley on 2021/11/18.
 * 更换图标
 */
object LauncherIconUtil {

    const val MAIN = "com.kingsley.helloword.MainActivity"
    const val MAIN1 = "com.kingsley.helloword.Launcher1"
    const val MAIN2 = "com.kingsley.helloword.Launcher2"

    private val componentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, MAIN1),
        ComponentName(HelloWordApplication.app, MAIN2)
    )

    fun changeIcon(icon: String?) {
        if (icon.isNullOrEmpty() || Build.VERSION.SDK_INT < 26) return
        var hasEnabled = false
        componentNames.forEach {
            if (icon.equals(it.className, true) || icon.equals(it.className.substringAfterLast("."), true)) {
                hasEnabled = true
                //启用
                setComponentEnabledSetting(it, false)
            } else {
                //禁用
                setComponentEnabledSetting(it, true)
            }
        }
        setComponentEnabledSetting(
            ComponentName(
                HelloWordApplication.app,
                MainActivity::class.java.name
            ), hasEnabled
        )
    }

    private fun setComponentEnabledSetting(componentName: ComponentName, disabled: Boolean) {
        val packageManager = HelloWordApplication.app.packageManager
        val state: Int = packageManager.getComponentEnabledSetting(componentName)
        //當前組件 已经禁用 || 已经啟用
        if ((disabled && state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
            || (!disabled && state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        ) {
            return
        }
        packageManager.setComponentEnabledSetting(
            componentName,
            if (disabled)
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            else
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}