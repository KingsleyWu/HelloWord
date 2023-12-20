package com.kingsley.helloword.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kingsley.helloword.R

fun bigTextNotification(
    context: Context, notificationId: Int, notificationChannelId: String,
    contentTitle: String, contentText: String,
    bigText: String, bigContentTitle: String, summaryText: String,
    notifyIntent: Intent,
    autoCancel: Boolean = true,
    @NotificationCompat.NotificationVisibility visibility: Int = NotificationCompat.VISIBILITY_PUBLIC
) : NotificationCompat.Builder{

    // 1. Build the BIG_TEXT_STYLE.
    val bigTextStyle = NotificationCompat.BigTextStyle()
        .bigText(bigText)
        .setBigContentTitle(bigContentTitle)
        .setSummaryText(summaryText)
    val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }
    val notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, flag)

    // Dismiss Action
    val dismissIntent = Intent(context, NotificationPostService::class.java)
    dismissIntent.action = NotificationPostService.ACTION_DISMISS
    dismissIntent.putExtra("NOTIFICATION_ID", notificationId)
    val dismissPendingIntent = PendingIntent.getService(context, 0, dismissIntent, flag)
    val dismissAction: NotificationCompat.Action = NotificationCompat.Action.Builder(R.drawable.ic_launcher_background, NotificationPostService.ACTION_DISMISS, dismissPendingIntent).build()

    // 2. Build and issue the notification.
    // Notification Channel Id is ignored for Android pre O (26).
    val notificationCompatBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, notificationChannelId)

    notificationCompatBuilder
        .setStyle(bigTextStyle)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setSmallIcon(R.drawable.ic_launcher_background)
//        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background))
        .setContentIntent(notifyPendingIntent)
        .setColor(ContextCompat.getColor(context, R.color.textSubColor))
        .setCategory(Notification.CATEGORY_SERVICE)
        .setVisibility(visibility)
        .addAction(dismissAction)
        .setAutoCancel(autoCancel)
    return notificationCompatBuilder
}