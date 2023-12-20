package com.kingsley.download.utils

import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.kingsley.common.L
import com.kingsley.download.appContext
import com.kingsley.download.bean.DownloadInfo
import com.kingsley.download.bean.DownloadGroup
import com.kingsley.download.core.INotifier
import com.kingsley.download.core.INotifier.DefaultNotifier.mNotificationManager

open class DownloadNotifier : INotifier {

    override fun createNotification(data: DownloadGroup) {
        L.d("createNotification data.status = ${data.status}")
        val notifyId = data.id.hashCode()
        if (data.status == DownloadInfo.NONE) {
            mNotificationManager.cancel(notifyId)
        } else {
//            if (data.showNotification) {
//                val builder = getNotificationBuilder(
//                    context = appContext,
//                    notifyClickAction = data.current?.action,
//                    notifyId = notifyId,
//                    contentTitle = data.current?.title,
//                    data = data
//                )
//                if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                    return
//                }
//                mNotificationManager.notify(notifyId, builder.build())
//            }
        }
    }

}