package com.kingsley.helloword.launcher

import android.content.ComponentName
import android.content.pm.PackageManager
import com.kingsley.helloword.HelloWordApplication

/**
 * Created by kingsley on 2021/11/18.
 * 更换图标
 */
object LauncherIconUtil {

    const val MAIN = "com.kingsley.helloword.MainActivity"
    const val MAIN_ALIAS = "com.kingsley.helloword.MainAlias"
    const val MAIN1 = "com.kingsley.helloword.Launcher1"
    const val MAIN2 = "com.kingsley.helloword.Launcher2"

    private val componentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, MAIN1),
        ComponentName(HelloWordApplication.app, MAIN2)
    )

    private val mainComponentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, MAIN)
    )

    private val mainAliasComponentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, MAIN_ALIAS)
    )

    @JvmStatic
    fun changeIcon(icon: String?) {
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

        mainAliasComponentNames.forEach {
            setComponentEnabledSetting(it, true)
        }

        mainComponentNames.forEach {
            setComponentEnabledSetting(it, hasEnabled)
        }
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