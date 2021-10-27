package com.kingsley.shimeji.service

import android.annotation.SuppressLint
import android.app.*
import com.kingsley.shimeji.mascot.MascotView

import com.kingsley.shimeji.AppConstants

import android.view.WindowManager

import android.graphics.BitmapFactory

import android.content.*

import android.view.ViewGroup

import android.widget.Toast

import com.kingsley.shimeji.data.SpritesService

import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager.BadTokenException
import androidx.core.app.NotificationCompat
import com.kingsley.shimeji.MyApplication
import com.kingsley.shimeji.R
import com.kingsley.shimeji.data.Helper
import com.kingsley.shimeji.mascot.MascotView.MascotEventNotifier
import com.kingsley.shimeji.purchases.ExtraAnimationCallback
import com.kingsley.shimeji.purchases.PayFeatures
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory
import java.lang.Exception
import java.lang.StringBuilder


class ShimejiService : Service() {
    private val CHANNEL_ID = "notification_channel"
    private lateinit var handler: Handler
    private var isShimejiVisible = true
    private var mCheckout: Checkout? = null
    private var mWindowManager: WindowManager? = null
    private val mascotViews: MutableList<MascotView> = ArrayList()
    private var pref_listener: PreferenceChangeListener? = null
    private var prefs: SharedPreferences? = null
    private val screenStatusReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(param1Context: Context?, param1Intent: Intent) {
            val str = param1Intent.action
            if ("android.intent.action.SCREEN_OFF" == str) {
                onScreenOff()
            } else if ("android.intent.action.SCREEN_ON" == str) {
                onScreenOn()
            }
        }
    }
    private val viewParams = HashMap<Long, WindowManager.LayoutParams>()

    private fun add(paramMascotView: MascotView) {
        paramMascotView.resumeAnimation()
    }

    private fun initMascotViews() {
        val list: List<Int> = Helper.getActiveTeamMembers(this)
        SpritesService.instance.loadSpritesForMascots(this, list)
        val iterator: Iterator<Int> = list.iterator()
        while (iterator.hasNext()) {
            val index = iterator.next()
            if (index != -1) {
                val layoutParams: WindowManager.LayoutParams = if (Build.VERSION.SDK_INT >= 26) {
                    WindowManager.LayoutParams(-2, -2, 2038, 520, -3)
                } else {
                    WindowManager.LayoutParams(-2, -2, 2002, 520, -3)
                }
//                layoutParams.gravity = 8388659
                layoutParams.gravity = 8388659
                val mascotView = MascotView(this, index)
                mascotView.setMascotEventsListener(object : MascotEventNotifier {
                    override fun hideMascot() {
                        this@ShimejiService.remove(mascotView)
                    }

                    override fun showMascot() {
                        add(mascotView)
                    }
                })
                mascotViews.add(mascotView)
                layoutParams.width = mascotView.bitmapWidth
                layoutParams.height = mascotView.bitmapHeight
                viewParams[java.lang.Long.valueOf(mascotView.uniqueId)] = layoutParams
                try {
                    mWindowManager!!.addView(mascotView as View?, layoutParams as ViewGroup.LayoutParams)
                } catch (badTokenException: BadTokenException) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        val stringBuilder = StringBuilder()
                        stringBuilder.append("package:")
                        stringBuilder.append(packageName)
                        startActivity(
                            Intent(
                                "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                                Uri.parse(stringBuilder.toString())
                            )
                        )
                        continue
                    }
                    Toast.makeText(
                        this,
                        "Please enable 'Draw on top of other apps' permission for this feature",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (badTokenException: SecurityException) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        val stringBuilder = StringBuilder()
                        stringBuilder.append("package:")
                        stringBuilder.append(packageName)
                        startActivity(
                            Intent(
                                "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                                Uri.parse(stringBuilder.toString())
                            )
                        )
                        continue
                    }
                    Toast.makeText(
                        this as Context,
                        "Please enable 'Draw on top of other apps' permission for this feature",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        handler.sendEmptyMessage(MSG_ANIMATE)
    }

    private fun onHandleMessage(message: Message): Boolean {
        if (message.what != MSG_ANIMATE) return false
        handler.removeMessages(MSG_ANIMATE)
        for (mascotView in mascotViews) {
            if (mascotView.isHidden && mascotView.isAnimationRunning) {
                val layoutParams = viewParams[java.lang.Long.valueOf(mascotView.uniqueId)]
                mWindowManager!!.addView(mascotView as View, layoutParams as ViewGroup.LayoutParams?)
                mascotView.isHidden = false
            }
            if (mascotView.isAnimationRunning) {
                val layoutParams = viewParams[java.lang.Long.valueOf(mascotView.uniqueId)]
                layoutParams!!.height = mascotView.height
                layoutParams.width = mascotView.width
                layoutParams.x = mascotView.x.toInt()
                layoutParams.y = mascotView.y.toInt()
                mWindowManager!!.updateViewLayout(mascotView as View, layoutParams as ViewGroup.LayoutParams?)
            }
        }
        handler.sendEmptyMessageDelayed(MSG_ANIMATE, 16L)
        return true
    }

    private fun onScreenOff() {
        handler.removeMessages(MSG_ANIMATE)
        for (mascotView in mascotViews) {
            if (!mascotView.isHidden) mascotView.pauseAnimation()
        }
    }

    private fun onScreenOn() {
        handler.removeMessages(MSG_ANIMATE)
        for (mascotView in mascotViews) {
            if (!mascotView.isHidden) mascotView.resumeAnimation()
        }
        handler.sendEmptyMessage(MSG_ANIMATE)
    }

    private fun remove(paramMascotView: MascotView?) {
        if (paramMascotView != null && paramMascotView.isShown) {
            paramMascotView.pauseAnimation()
            paramMascotView.isHidden = true
            mWindowManager!!.removeViewImmediate(paramMascotView as View?)
        }
    }

    private fun removeMascotViews() {
        handler.removeMessages(MSG_ANIMATE)
        for (mascotView in mascotViews) {
            if (mascotView.isShown) {
                mascotView.pauseAnimation()
                mWindowManager!!.removeViewImmediate(mascotView as View)
            }
        }
        mascotViews.clear()
        viewParams.clear()
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun setForegroundNotification(paramBoolean: Boolean) {
        if (Helper.getNotificationVisibility(this as Context)) {
            var charSequence: CharSequence?
            val pendingIntent = PendingIntent.getService(
                this as Context, 0, Intent(
                    this as Context,
                    ShimejiService::class.java
                ).setAction(ACTION_TOGGLE), 0
            )
            var builder1: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)

            charSequence = if (paramBoolean) {
                getText(R.string.app_name)
            } else {
                getText(R.string.app_name)
            }
            builder1 = builder1.setContentTitle(charSequence)
            if (paramBoolean) {
                charSequence = getText(R.string.app_name)
            } else {
                charSequence = getText(R.string.app_name)
            }
            val builder2: NotificationCompat.Builder = builder1.setContentText(charSequence)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
                .setOngoing(true)
                .setPriority(-2)
                .setContentIntent(pendingIntent)
            if (Build.VERSION.SDK_INT >= 26) {
                setupNotificationChannel()
                builder2.setChannelId(CHANNEL_ID)
            }
            val notification: Notification = builder2.build()
            if (paramBoolean) {
                startForeground(NOTIFY_ID, notification)
            } else {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
                notificationManager?.notify(NOTIFY_ID, notification)
            }
        }
    }

    private fun setupNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val name: String = getString(R.string.app_name)
        val str2: String = getString(R.string.app_name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = str2
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun toggleShimejiStatus() {
        val i: Boolean = (isShimejiVisible xor true)
        isShimejiVisible = i
        setForegroundNotification(isShimejiVisible)
        for (mascotView in mascotViews) {
            if (isShimejiVisible) {
                add(mascotView)
                continue
            }
            mascotView.cancelReappearTimer()
            remove(mascotView)
        }
    }

    override fun onBind(paramIntent: Intent?): IBinder? {
        return null
    }

    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        if (configuration.orientation == 2 || configuration.orientation == 1) {
            val iterator: Iterator<MascotView> = mascotViews.iterator()
            while (iterator.hasNext()) iterator.next().notifyLayoutChange(this as Context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        handler = Handler(Looper.getMainLooper()) { message -> onHandleMessage(message) }
        prefs = getSharedPreferences(AppConstants.MY_PREFS, Context.MODE_MULTI_PROCESS)
        try {
            val checkout: Checkout = Checkout.forService(this, MyApplication.get().billing)
            mCheckout = checkout
            checkout.start()
            val inventory: Inventory = mCheckout!!.makeInventory()
            val request: Inventory.Request =
                Inventory.Request.create().loadAllPurchases().loadSkus("inapp", PayFeatures.list)
            val extraAnimationCallback = ExtraAnimationCallback()
            inventory.load(request, extraAnimationCallback as Inventory.Callback)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCheckout?.stop()
        prefs?.unregisterOnSharedPreferenceChangeListener(pref_listener)
        try {
            unregisterReceiver(screenStatusReceiver)
        } catch (exception: Exception) {
            Log.d("SHIMEJI", "Prevented unregister Receiver crash")
        }
        removeMascotViews()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent != null && ACTION_TOGGLE == intent.action) {
            toggleShimejiStatus()
        } else {
            setForegroundNotification(isShimejiVisible)
            initMascotViews()
            pref_listener = PreferenceChangeListener()
            prefs?.registerOnSharedPreferenceChangeListener(pref_listener)
            registerReceiver(screenStatusReceiver, IntentFilter("android.intent.action.SCREEN_OFF"))
            registerReceiver(screenStatusReceiver, IntentFilter("android.intent.action.SCREEN_ON"))
        }
        return Service.START_STICKY
    }

    inner class PreferenceChangeListener : OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(param1SharedPreferences: SharedPreferences, param1String: String) {
            if (isShimejiVisible && (param1String == AppConstants.ACTIVE_MASCOTS_IDS || param1String == AppConstants.SIZE_MULTIPLIER)) {
                removeMascotViews()
                initMascotViews()
            } else if (param1String == AppConstants.ANIMATION_SPEED) {
                val d: Double = Helper.getSpeedMultiplier(this@ShimejiService.baseContext)
                val iterator: Iterator<MascotView> = mascotViews.iterator()
                while (iterator.hasNext()) iterator.next().setSpeedMultiplier(java.lang.Double.valueOf(d).toDouble())
            } else if (param1String == AppConstants.SHOW_NOTIFICATION) {
                if (Helper.getNotificationVisibility(this@ShimejiService.baseContext)) {
                    val shimejiService = this@ShimejiService
                    shimejiService.setForegroundNotification(shimejiService.isShimejiVisible)
                } else {
                    if (!isShimejiVisible) toggleShimejiStatus()
                    this@ShimejiService.stopForeground(true)
                }
            } else if (param1String == AppConstants.REAPPEAR_DELAY) {
                for (mascotView in mascotViews) {
                    if (isShimejiVisible) {
                        mascotView.cancelReappearTimer()
                        mascotView.resumeAnimation()
                    }
                }
            }
        }
    }

    companion object {
        const val ACTION_START = "com.digitalcosmos.start"
        const val ACTION_STOP = "com.digitalcosmos.stop"
        const val ACTION_TOGGLE = "com.digitalcosmos.toggle"
        private const val MSG_ANIMATE = 1
        private const val NOTIFY_ID = 99
    }
}
