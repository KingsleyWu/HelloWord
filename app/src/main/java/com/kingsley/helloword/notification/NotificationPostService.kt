package com.kingsley.helloword.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.kingsley.common.L

class NotificationPostService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        L.d("onNotificationPosted onCreate = $packageName")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val nc = it.notification // 通知栏对象
            val packageName = "" + it.packageName // 应用的包名
            val tickerText = "" + nc.tickerText  // 通知栏摘要
            val extrasTitle = "" + nc.extras.get("android.title") // 通知栏标题
            val extrasText = "" + nc.extras.get("android.text") // 通知栏内容
            L.d("onNotificationPosted packageName = $packageName, tickerText = $tickerText, extrasTitle = $extrasTitle, extrasText = $extrasText")
            nc.extras.let { bundle ->
                for (key in bundle.keySet()) {
//                    L.d("onNotificationPosted bundle key = $key, content = ${bundle.get(key)}")
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            val nc = it.notification // 通知栏对象
            val packageName = "" + it.packageName // 应用的包名
            val tickerText = "" + nc.tickerText  // 通知栏摘要
            val extrasTitle = "" + nc.extras.get("android.title") // 通知栏标题
            val extrasText = "" + nc.extras.get("android.text") // 通知栏内容
            L.d("onNotificationRemoved packageName = $packageName, tickerText = $tickerText, extrasTitle = $extrasTitle, extrasText = $extrasText")
            nc.extras.let { bundle ->
                for (key in bundle.keySet()) {
//                    L.d("onNotificationRemoved bundle key = $key, content = ${bundle.get(key)}")
                }
            }
        }
    }
}