package com.kingsley.shimeji.mascot

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder

import android.util.Log

import android.view.MotionEvent

import android.widget.Scroller

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener

import android.view.SurfaceView
import com.kingsley.shimeji.data.Helper
import com.kingsley.shimeji.data.SpritesService
import com.kingsley.shimeji.mascot.animations.Animation
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.atomic.AtomicLong


class MascotView(mContext: Context, var mascotId: Int) : SurfaceView(mContext), SurfaceHolder.Callback {
    private val drawRunner = Runnable { draw() }
    private var eventNotifier: MascotEventNotifier? = null
    var flipHorizontalMatrix: Matrix = Matrix()
    private val gestureDetector: GestureDetector

    private val timer: Timer = Timer()
    private var gestureListener = object : SimpleOnGestureListener() {
        private var initialX = 0
        private var initialY = 0
        override fun onDoubleTap(event: MotionEvent): Boolean {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Double tap on mascot: ")
            stringBuilder.append(mascotId)
            Log.v("SHIMEJI", stringBuilder.toString())
            eventNotifier?.let {
                it.hideMascot()
                timerTask = object : TimerTask() {
                    override fun run() {
                        eventNotifier?.showMascot()
                    }
                }
                timer.schedule(timerTask!!, Helper.getReappearDelayMs(mContext))
            }

            return true
        }

        override fun onDown(event: MotionEvent): Boolean {
            mascot?.let {
                initialX = it.getX()
                initialY = it.getY()
            }
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            if (Animation.flingEnabled && mascot != null) {
                val x = velocityX.toInt()
                val y = velocityY.toInt()
                mascot.setFlingSpeed(x, y)
                scroller.fling(
                    mascot.getX(),
                    mascot.getY(),
                    x,
                    y,
                    playground.left - 100,
                    playground.right + 100,
                    playground.top - 100,
                    playground.bottom + 100
                )
                val stringBuilder = StringBuilder()
                stringBuilder.append("Fling on mascot: ")
                stringBuilder.append(mascotId)
                Log.v("SHIMEJI", stringBuilder.toString())
            }
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            drag(
                initialX + (e2.rawX - e1.rawX).toInt(),
                initialY + (e2.rawY - e1.rawY).toInt()
            )
            return false
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Single tap on mascot: ")
            stringBuilder.append(mascotId)
            Log.v("SHIMEJI", stringBuilder.toString())
            return super.onSingleTapConfirmed(event)
        }
    }
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    var bitmapHeight: Int = 0
    var bitmapWidth: Int = 0
    val uniqueId: Long = NEXT_ID.getAndIncrement()
    var isAnimationRunning = true
    var isHidden = false
    private val mascot: Mascot?
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var playground: Playground
    private val scroller: Scroller
    private val sizeMultiplier: Double
    private val speedMultiplier: Double
    private val spritesService: SpritesService = SpritesService.instance
    private var timerTask: TimerTask? = null

    fun cancelReappearTimer() {
        timerTask?.cancel()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        super.dispatchTouchEvent(event)
        performClick()
        if (event.action == MotionEvent.ACTION_UP) {
            endDrag()
        }
        return gestureDetector.onTouchEvent(event)
    }

    fun drag(paramInt1: Int, paramInt2: Int) {
        mascot?.drag(paramInt1, paramInt2)
    }

    fun draw() {
        if (isAnimationRunning) {
            val canvas: Canvas? = holder.lockCanvas()
            if (canvas != null) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                val bitmap = mascot?.getFrameBitmap()
                if (bitmap != null) {
                    bitmapHeight = bitmap.height
                    bitmapWidth = bitmap.width
                    if (mascot!!.isFacingLeft) {
                        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
                    } else {
                        flipHorizontalMatrix.setScale(-1.0f, 1.0f)
                        flipHorizontalMatrix.postTranslate(bitmapWidth.toFloat(), 0.0f)
                        canvas.drawBitmap(bitmap, flipHorizontalMatrix, paint)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
        mHandler.removeCallbacks(drawRunner)
        mHandler.postDelayed(drawRunner, 16L)
    }

    fun endDrag() {
        mascot?.endDragging()
    }

    override fun getX(): Float {
        if (mascot?.isBeingFlung() == true) {
            if (scroller.computeScrollOffset()) {
                mascot.fling(scroller.currX, scroller.currY)
            } else {
                mascot.endFlinging()
            }
        }
        return mascot?.getX()?.toFloat() ?: 0f
    }

    override fun getY(): Float {
        return mascot?.getY()?.toFloat() ?: 0f
    }

    fun notifyLayoutChange(context: Context) {
        this.playground = Playground(context, false)
        mascot?.resetEnvironmentVariables(playground)
    }

    fun pauseAnimation() {
        isAnimationRunning = false
    }

    fun resumeAnimation() {
        isAnimationRunning = true
    }

    fun setMascotEventsListener(eventListener: MascotEventNotifier?) {
        this.eventNotifier = eventListener
    }

    fun setSpeedMultiplier(speedMultiplier: Double) {
        mascot?.setSpeedMultiplier(speedMultiplier)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceCreated(holder: SurfaceHolder) {
        mHandler.post(drawRunner)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mHandler.removeCallbacks(drawRunner)
    }

    interface MascotEventNotifier {
        fun hideMascot()
        fun showMascot()
    }

    companion object {
        val NEXT_ID = AtomicLong(0L)
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
        setZOrderOnTop(true)
        speedMultiplier = Helper.getSpeedMultiplier(mContext)
        sizeMultiplier = Helper.getSizeMultiplier(mContext)
        spritesService.setSizeMultiplier(mContext, sizeMultiplier)
        val sprites: Sprites = spritesService.getSpritesById(mContext, mascotId)
        bitmapHeight = sprites.height
        bitmapWidth = sprites.width
        mascot = Mascot()
        this.playground = Playground(mContext, false)
        mascot.initialize(sprites, speedMultiplier, playground)
        mascot.startAnimation()
        holder.setFormat(-2)
        holder.addCallback(this)
        gestureDetector = GestureDetector(mContext, gestureListener)
        scroller = Scroller(mContext)
    }
}
