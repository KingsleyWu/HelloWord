package com.kingsley.download.core

import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.kingsley.download.appContext
import com.kingsley.download.bean.DownloadGroup

interface INotifier {

    /**
     * 创建通知
     * @param data 当前下载的 group 信息
     */
    fun createNotification(data: DownloadGroup)

    companion object DefaultNotifier: INotifier {

        /** 通知管理器 */
        val mNotificationManager: NotificationManagerCompat by lazy {
            NotificationManagerCompat.from(appContext)
        }

        /** 通知渠道 id */
        var CHANNEL_ID: String = appContext.packageName + ".download.notification_channel"

        /** 創建通知渠道 */
        @JvmStatic
        @JvmOverloads
        fun createNotificationChannel(
            channelId: String = CHANNEL_ID,
            channelName: String? = null,
            channelDescription: String? = null
        ) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannelCompat.Builder(channelId, importance)
                    .setName(channelName)
                    .setDescription(channelDescription)
                    .build()
                // Register the channel with the system;
                // you can't change the importance
                // or other notification behaviors after this
                mNotificationManager.createNotificationChannel(channel)
            }
        }

        override fun createNotification(data: DownloadGroup) {
            // do nothing
        }
    }
}