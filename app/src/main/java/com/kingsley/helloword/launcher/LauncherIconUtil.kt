package com.kingsley.helloword.launcher

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Build
import com.kingsley.common.L
import com.kingsley.helloword.HelloWordApplication
import com.kingsley.helloword.Launcher1
import com.kingsley.helloword.Launcher2
import com.kingsley.helloword.MainActivity

/**
 * Created by kingsley on 2021/11/18.
 * 更换图标
 */
object LauncherIconUtil {
    private val componentNames = arrayListOf(
        ComponentName(HelloWordApplication.app, Launcher1::class.java.name),
        ComponentName(HelloWordApplication.app, Launcher2::class.java.name)
    )

    fun changeIcon(icon: String?) {
        if (icon.isNullOrEmpty() || Build.VERSION.SDK_INT < 26) return
        var hasEnabled = false
        componentNames.forEach {
            if (icon.equals(it.className.substringAfterLast("."), true)) {
                hasEnabled = true
                //启用
                setComponentEnabledSetting(it, false)
            } else {
                //禁用
                setComponentEnabledSetting(it, true)
            }
        }
        setComponentEnabledSetting(ComponentName(HelloWordApplication.app, MainActivity::class.java.name), hasEnabled)
    }

    private fun setComponentEnabledSetting(componentName: ComponentName, disabled: Boolean) {
        HelloWordApplication.app.packageManager.setComponentEnabledSetting(
            componentName,
            if (disabled)
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            else
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}