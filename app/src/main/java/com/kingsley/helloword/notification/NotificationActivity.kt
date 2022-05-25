package com.kingsley.helloword.notification

import android.app.Notification
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kingsley.base.activity.BaseVbActivity
import com.kingsley.helloword.R
import com.kingsley.helloword.databinding.NotificationActivityBinding

class NotificationActivity: BaseVbActivity<NotificationActivityBinding>() {

    private val CHANNEL_IDS = arrayOf("channel_one_home", "channel_two_home", "channel_three", "channel_one_work", "channel_two_work")
    private val CHANNEL_NAMES = arrayOf("Channel One", "Channel Two", "Channel Three", "Channel One", "Channel Two")
    private val CHANNEL_DESCRIPTIONS = arrayOf("Channel One Home", "Channel Two Home", "Channel Three", "Channel One Work", "Channel Two Work")
    private val CHANNEL_GROUPS = intArrayOf(0, 0, -1, 1, 1) // Channel Three is in no group

    private val GROUP_IDS = arrayOf("group_home", "group_work")
    private val GROUP_NAMES = arrayOf("Home", "Work")
    private val GROUP_DESCRIPTIONS = arrayOf("Group Home", "Group Work")

    override fun initView(savedInstanceState: Bundle?) {}

    override fun initData() {
        initChannels()
        with(mViewBinding) {
            val adapter = ArrayAdapter<CharSequence>(this@NotificationActivity, android.R.layout.simple_spinner_item, CHANNEL_DESCRIPTIONS)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerChannels.adapter = adapter
            btnNotify.setOnClickListener{
                val channelId = spinnerChannels.selectedItemPosition
                if (channelId >= 0 && channelId < CHANNEL_IDS.size) {
                    doNotify(channelId)
                }
            }
        }
    }

    private fun initChannels() {
        val notificationManager = NotificationManagerCompat.from(this)

        for (i in GROUP_IDS.indices) {
            val group = NotificationChannelGroupCompat
                .Builder(GROUP_IDS[i])
                .setName(GROUP_NAMES[i])
                .setDescription(GROUP_DESCRIPTIONS[i])
                .build()
            notificationManager.createNotificationChannelGroup(group)
        }

        for (i in CHANNEL_IDS.indices) {
            val name: CharSequence = CHANNEL_NAMES[i]
            val description = CHANNEL_DESCRIPTIONS[i]
            val importance = NotificationManagerCompat.IMPORTANCE_HIGH
            val channel = NotificationChannelCompat
                .Builder(CHANNEL_IDS[i], importance)
                .setName(name)
                .setDescription(description)
                .also {
                    if (CHANNEL_GROUPS[i] >= 0) {
                        it.setGroup(GROUP_IDS[CHANNEL_GROUPS[i]])
                    }
                }.build()
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun doNotify(index: Int) {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_IDS[index])
            .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
            .setContentTitle(CHANNEL_NAMES[index])
            .setContentText(CHANNEL_DESCRIPTIONS[index])
            .build()
        NotificationManagerCompat.from(this).notify(index, notification)
    }

}