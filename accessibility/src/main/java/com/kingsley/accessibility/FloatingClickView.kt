package com.kingsley.accessibility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView

class FloatingClickView(private val mContext: Context) : FrameLayout(mContext) {

    private lateinit var mWindowManager: FloatingManager
    private var mParams: WindowManager.LayoutParams? = null

    private lateinit var mView: View

    //ζδΈεζ 
    private var mTouchStartX = -1f
    private var mTouchStartY = -1f

    val STATE_CLICKING = "state_clicking"
    val STATE_NORMAL = "state_normal"
    private var mCurrentState = STATE_NORMAL

    private var ivIcon: AppCompatImageView? = null

    init {
        initView()
    }


    private fun initView() {
        mView = LayoutInflater.from(context).inflate(R.layout.view_floating_click, null)
        ivIcon = mView.findViewById(R.id.iv_icon)
        mWindowManager = FloatingManager.getInstance(mContext)
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        mView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    mParams?.let {
                        it.x += (event.rawX - mTouchStartX).toInt()
                        it.y += (event.rawY - mTouchStartY).toInt()
                        mWindowManager.updateView(mView, it)
                    }
                    mTouchStartX = event.rawX
                    mTouchStartY = event.rawY
                }
            }
            false
        }

        mView.setOnClickListener {

            val location = IntArray(2)
            it.getLocationOnScreen(location)
            val intent = Intent(context, MyAccessibilityService::class.java)
            when (mCurrentState) {
                STATE_NORMAL -> {
                    mCurrentState = STATE_CLICKING
                    intent.putExtra(MyAccessibilityService.FLAG_ACTION, MyAccessibilityService.ACTION_PLAY)
                    intent.putExtra("pointX", (location[0] - 1).toFloat())
                    intent.putExtra("pointY", (location[1] - 1).toFloat())
                    ivIcon?.setImageResource(R.drawable.ic_launcher_background)
                }
                STATE_CLICKING -> {
                    mCurrentState = STATE_NORMAL
                    intent.putExtra(MyAccessibilityService.FLAG_ACTION, MyAccessibilityService.ACTION_STOP)
                    ivIcon?.setImageResource(R.drawable.ic_launcher_foreground)
                }
            }
            context.startService(intent)
        }
    }

    fun show() {
        mParams = WindowManager.LayoutParams()
        mParams?.apply {
            gravity = Gravity.CENTER
            //ζ»ζ―εΊη°ε¨εΊη¨η¨εΊηͺε£δΉδΈ
            type = if (Build.VERSION.SDK_INT >= 26) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            //θ?Ύη½?εΎηζ ΌεΌοΌζζδΈΊθζ―ιζ
            format = PixelFormat.RGBA_8888

            flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH

            width = LayoutParams.WRAP_CONTENT
            height = LayoutParams.WRAP_CONTENT
            if (mView.isAttachedToWindow) {
                mWindowManager.removeView(mView)
            }
            mWindowManager.addView(mView, this)
        }
    }

    fun remove() {
        mWindowManager.removeView(mView)
    }

}