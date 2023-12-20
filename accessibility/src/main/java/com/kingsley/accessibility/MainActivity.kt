package com.kingsley.accessibility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kingsley.accessibility.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = javaClass::class.java.canonicalName
    private lateinit var mViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        initNotification()
        initListener()
        Log.d(TAG, "getAndroidId = " + getAndroidId(this))
    }


    fun getAndroidId(context: Context): String {
        try {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
        }
        return ""
    }

    private fun initListener() {
        mViewBinding.btnAccessibility.setOnClickListener {
            checkAccessibility()
        }

        mViewBinding.btnFloatingWindow.setOnClickListener {
//            checkFloatingWindow()
        }

        mViewBinding.btnShowWindow.setOnClickListener {
            hideKeyboard()
            if (TextUtils.isEmpty(mViewBinding.etInterval.text.toString())) {
                Snackbar.make(mViewBinding.etInterval, "请输入间隔", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("qoohelper://welcome")))
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("alipays://platformapi/startapp?saId=20000869")))
            showFloatingWindow()
        }

        mViewBinding.btnCloseWindow.setOnClickListener {
//            closeFloatWindow()
        }

        mViewBinding.btnTest.setOnClickListener {
            Log.d(TAG, "btn_test on click")
        }

    }

    /**
     * 跳转设置开启无障碍
     */
    private fun checkAccessibility() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    /**
     * 跳转设置顶层悬浮窗
     */
    private fun checkFloatingWindow() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "已开启", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(intent)
            }
        }
    }

    private fun showFloatingWindow() {
        val intent = Intent(this, MyAccessibilityService::class.java)
        intent.apply {
            putExtra(MyAccessibilityService.FLAG_ACTION, MyAccessibilityService.ACTION_SHOW)
            putExtra("interval", mViewBinding.etInterval.text.toString().toLong())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun closeFloatWindow() {
        val intent = Intent(this, MyAccessibilityService::class.java)
        intent.putExtra(MyAccessibilityService.FLAG_ACTION, MyAccessibilityService.ACTION_CLOSE)
        startService(intent)
    }


    private fun initNotification() {
        //注册渠道id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NotificationConstants.CHANNEl_NAME
            val descriptionText = NotificationConstants.CHANNEL_DES
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(NotificationConstants.CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
            channel.enableLights(true)
            channel.lightColor = Color.GREEN
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        val intent = Intent(this, MyAccessibilityService::class.java)
        stopService(intent)
        super.onDestroy()
    }

    //收起输入法
    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive && currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}