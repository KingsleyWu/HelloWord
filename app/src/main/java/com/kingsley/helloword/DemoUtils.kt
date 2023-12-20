package com.kingsley.helloword

import android.content.SharedPreferences
import com.kingsley.helloword.apk.GetApkActivity
import com.kingsley.helloword.bean.SettingBean
import com.kingsley.helloword.bean.StartBean
import com.kingsley.helloword.compose.ComposableTestActivity
import com.kingsley.helloword.coroutine.CoroutinesActivity
import com.kingsley.helloword.datepicker.DatePickerActivity
import com.kingsley.helloword.document.FileCreateActivity
import com.kingsley.helloword.download.DownloadActivity
import com.kingsley.helloword.draw.DrawActivity
import com.kingsley.helloword.floating.FloatingActivity
import com.kingsley.helloword.geometric.GeometricActivity
import com.kingsley.helloword.html.HtmlActivity
import com.kingsley.helloword.keyboard.KeyboardActivity
import com.kingsley.helloword.launcher.LauncherIconActivity
import com.kingsley.helloword.link.LinkActivity
import com.kingsley.helloword.navigation.NavigationActivity
import com.kingsley.helloword.notification.NotificationActivity
import com.kingsley.helloword.splitapk.SplitApkInstallActivity
import com.kingsley.helloword.systembars.SystemBarsActivity
import com.kingsley.helloword.tts.TTSActivity
import com.kingsley.helloword.viewpager2.ViewPager2Activity
import com.kingsley.helloword.widget.JigsawViewActivity
import com.kingsley.helloword.widget.SquareMatrixViewActivity
import com.kingsley.helloword.widget.Test3DViewActivity
import com.kingsley.helloword.widget.WhirlingViewActivity
import com.kingsley.sample.recyclerview.DiffDemoActivity
import com.tencent.mmkv.MMKV

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
object DemoUtils {

    fun getStartData(): MutableList<StartBean> {
        val startData = mutableListOf<StartBean>()
        startData.add(StartBean("拼圖", JigsawViewActivity::class.java))
        startData.add(StartBean("3D測試", Test3DViewActivity::class.java))
        startData.add(StartBean("旋转", WhirlingViewActivity::class.java))
        startData.add(StartBean("旋转的立方体", SquareMatrixViewActivity::class.java))
        startData.add(StartBean("3D Model", GeometricActivity::class.java))

        startData.add(StartBean("系統欄", SystemBarsActivity::class.java))
        startData.add(StartBean("Navigation 使用", NavigationActivity::class.java))

        startData.add(StartBean("鍵盤", KeyboardActivity::class.java))
        startData.add(StartBean("懸浮框", FloatingActivity::class.java))
        startData.add(StartBean("安裝的Apk", GetApkActivity::class.java))
        startData.add(StartBean("Crash Test", CrashActivity::class.java))
        startData.add(StartBean("TTS", TTSActivity::class.java))
        startData.add(StartBean("Document", FileCreateActivity::class.java))
        startData.add(StartBean("自定義View", DrawActivity::class.java))
        startData.add(StartBean("協程", CoroutinesActivity::class.java))
        startData.add(StartBean("下載", DownloadActivity::class.java))
        startData.add(StartBean("DiffUtil", DiffDemoActivity::class.java))
        startData.add(StartBean("啟動圖標", LauncherIconActivity::class.java))
        startData.add(StartBean("Html", HtmlActivity::class.java))
        startData.add(StartBean("鏈接", LinkActivity::class.java))
        startData.add(StartBean("ViewPager2", ViewPager2Activity::class.java))
        startData.add(StartBean("Notification", NotificationActivity::class.java))
        startData.add(StartBean("Split Apk 安裝測試", SplitApkInstallActivity::class.java))
        startData.add(StartBean("Compose 重組測試", ComposableTestActivity::class.java))
        startData.add(StartBean("DateTimePicker", DatePickerActivity::class.java))
        return startData
    }


    fun getSettingData(): MutableList<SettingBean> {
        val settingData = mutableListOf<SettingBean>()
        val kv = MMKV.defaultMMKV()
        val off = kv?.decodeBool("NOTIFICATION_OFF") ?: false
        settingData.add(SettingBean("Notification 監聽 - ${if (off) "已關閉" else "已打開"}"))
        return settingData
    }
}