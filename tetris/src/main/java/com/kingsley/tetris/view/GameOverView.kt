package com.kingsley.tetris.view

import android.content.Context
import kotlin.jvm.JvmOverloads
import android.graphics.Bitmap
import com.kingsley.tetris.view.GameOverView
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import com.kingsley.tetris.R
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import java.util.*

class GameOverView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    private var mContext: Context? = null
    private var mHandler: Handler? = null
    private var mBitmap: Bitmap? = null
    private var animCurrentPage = 0

    //动画帧数
    private val animMaxPage = 4
    var animState = ANIM_STOP
        private set
    private var mTimeInterval = 100
    private var mTimer: Timer? = null
    private fun init(context: Context) {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mContext = context
        mBitmap = BitmapFactory.decodeResource(mContext!!.resources, R.drawable.icon_wait)
        mHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    ANIM_RUN -> {
                        if (animCurrentPage < animMaxPage) {
                            animCurrentPage++
                        } else {
                            animCurrentPage = 1
                        }
                        invalidate()
                    }
                    ANIM_STOP -> {
                        animCurrentPage = 0
                        invalidate()
                    }
                    else -> {
                        animCurrentPage = 0
                        invalidate()
                    }
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val sideLength = mBitmap!!.width / animMaxPage
        val bitmapHeight = mBitmap!!.height
        val src = Rect(sideLength * (animCurrentPage - 1), 0, sideLength * animCurrentPage, sideLength)
        val dst = Rect(0, 0, sideLength, bitmapHeight)
        canvas.drawBitmap(mBitmap!!, src, dst, null)
    }

    fun start() {
        if (animState == ANIM_RUN) {
            return
        }
        animState = ANIM_RUN
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        mTimer = Timer()
        mTimer!!.schedule(object : TimerTask() {
            override fun run() {
                mHandler!!.sendEmptyMessage(ANIM_RUN)
            }
        }, 0, mTimeInterval.toLong())
    }

    fun stop() {
        if (animState == ANIM_STOP) {
            return
        }
        animState = ANIM_STOP
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        mHandler!!.sendEmptyMessage(ANIM_STOP)
    }

    fun setTimeInterval(timeInterval: Int) {
        mTimeInterval = timeInterval
    }

    val isRunning: Boolean
        get() = animState == ANIM_RUN

    companion object {
        //运行状态
        private const val ANIM_RUN = 0

        //停止状态
        private const val ANIM_STOP = 1
    }

    init {
        init(context)
    }
}