package com.kingsley.helloword.notification

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.kingsley.common.L

class NotificationPostService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        L.d("onNotificationPosted onCreate = $packageName, hashCode = ${hashCode()}")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (ACTION_DISMISS == action) {
                handleActionDismiss(intent.getIntExtra("NOTIFICATION_ID", 1))
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d("onNotificationPosted onDestroy = $packageName, hashCode = ${hashCode()}")
    }

    private fun handleActionDismiss(notificationId: Int) {
        NotificationManagerCompat.from(applicationContext).cancel(notificationId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        L.d("onNotificationPosted 22 packageName = $packageName, tag = ${sbn?.tag}, key = ${sbn?.key}, hashCode = ${hashCode()}")
        sbn?.let {
            val nc = it.notification // 通知栏对象
            val packageName = "" + it.packageName // 应用的包名
            val tickerText = "" + nc.tickerText  // 通知栏摘要
            val extrasTitle = "" + nc.extras.getString("android.title") // 通知栏标题
            val extrasText = "" + nc.extras.getString("android.text") // 通知栏内容
            for (key in nc.extras.keySet()) {
                L.d("onNotificationPosted", "key: $key , value: ${nc.extras.get(key)}")
            }
            L.d("onNotificationPosted packageName = $packageName, tickerText = $tickerText, extrasTitle = $extrasTitle, extrasText = $extrasText, hashCode = ${hashCode()}")
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
            val extrasTitle = "" + nc.extras.getString("android.title") // 通知栏标题
            val extrasText = "" + nc.extras.getString("android.text") // 通知栏内容
            L.d("onNotificationRemoved packageName = $packageName, tickerText = $tickerText, extrasTitle = $extrasTitle, extrasText = $extrasText")
            nc.extras.let { bundle ->
                for (key in bundle.keySet()) {
//                    L.d("onNotificationRemoved bundle key = $key, content = ${bundle.get(key)}")
                }
            }
        }
    }

    companion object {
        const val ACTION_DISMISS: String = "dismiss"
    }
}