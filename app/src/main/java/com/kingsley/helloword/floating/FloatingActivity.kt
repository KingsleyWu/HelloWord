package com.kingsley.helloword.floating

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.kingsley.base.activity.BaseActivity
import com.kingsley.common.L
import com.kingsley.helloword.R

/**
 * @author Kingsley
 * Created on 2021/6/28.
 */
class FloatingActivity : BaseActivity() {

    /**
     * 声优view
     */
    private val sLocalFloatingPlayerView = ThreadLocal<FloatingPlayerView>()
    private val mBtnShowFloating: Button by lazy { findViewById(R.id.btn_show_floating) }
    private val mBtnHideFloating: Button by lazy { findViewById(R.id.btn_hide_floating) }
    private val mBtnResetFloating: Button by lazy { findViewById(R.id.btn_reset_floating) }

    private val mCVReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        private var isCreated = false
        private val mLock = Any()
        override fun onReceive(context: Context, intent: Intent) {
            L.d("wwc onReceive intent.getAction() = " + intent.action)
            if (TextUtils.equals(FloatingPlayerView.ACTION_OPEN, intent.action)) {
                var floatingPlayerView: FloatingPlayerView? = sLocalFloatingPlayerView.get()
                if (floatingPlayerView == null) {
                    if (isCreated) {
                        return
                    }
                    // 防止 多创建
                    synchronized(mLock) {
                        isCreated = true
                        floatingPlayerView = FloatingPlayerView(context)
                        sLocalFloatingPlayerView.set(floatingPlayerView)
                    }
                }
                floatingPlayerView?.handleIntent(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.floating_activity)
        val intentFilter = IntentFilter(FloatingPlayerView.ACTION_OPEN)
        LocalBroadcastManager.getInstance(this).registerReceiver(mCVReceiver, intentFilter)
        mBtnShowFloating.setOnClickListener {
            grantedShowFloatingView("ddd", null, false)
        }
        mBtnHideFloating.setOnClickListener {
           closeFloatingView()
        }
        mBtnResetFloating.setOnClickListener {
            resetFloatingView(false)
        }
    }

    private fun grantedShowFloatingView(title: CharSequence?, iconUri: Uri?, previewing: Boolean) {
        //没有弹窗权限
        if (!FloatWindowPermissionChecker.checkFloatWindowPermission(this)) {
            FloatWindowPermissionChecker.tryJumpToPermissionPage(this)
            return
        }
        //創建浮動頭像
        val it = Intent(FloatingPlayerView.ACTION_OPEN)
        it.putExtra("isBackground", false)
        if (title != null) {
            it.putExtra("title", title)
        }
        if (iconUri != null) {
            it.putExtra("iconUri", iconUri)
        }
        L.d("wwc ShowFloatingView")
        it.putExtra("previewing", previewing)
        LocalBroadcastManager.getInstance(this).sendBroadcast(it)
    }

    private fun resetFloatingView(checkPreview: Boolean) {
        //没有弹窗权限
        if (!FloatWindowPermissionChecker.checkFloatWindowPermission(this)) {
            FloatWindowPermissionChecker.tryJumpToPermissionPage(this)
            return
        }
        val it = Intent(FloatingPlayerView.ACTION_OPEN)
        it.putExtra("isBackground", false)
        it.putExtra("reset", true)
        it.putExtra("checkPreview", checkPreview)
        L.d("wwc resetFloatingView checkPreview = $checkPreview")
        LocalBroadcastManager.getInstance(this).sendBroadcast(it)
    }

    fun closeFloatingView() {
        //没有弹窗权限
        if (!FloatWindowPermissionChecker.checkFloatWindowPermission(this)) {
            FloatWindowPermissionChecker.tryJumpToPermissionPage(this)
            return
        }
        val it = Intent(FloatingPlayerView.ACTION_CLOSE)
        L.d("wwc closeFloatingView")
        LocalBroadcastManager.getInstance(this).sendBroadcast(it)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCVReceiver)
    }
}