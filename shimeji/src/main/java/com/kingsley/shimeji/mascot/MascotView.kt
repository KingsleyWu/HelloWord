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
    private val drawRunner = Runnable { this@MascotView.draw() }
    private var eventNotifier: MascotEventNotifier? = null
    var flipHorizontalMatrix: Matrix = Matrix()
    private val gestureDetector: GestureDetector

    private val timer: Timer = Timer()
    var gestureListener = object : SimpleOnGestureListener() {
        private var initialX = 0
        private var initialY = 0
        override fun onDoubleTap(param1MotionEvent: MotionEvent): Boolean {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Double tap on mascot: ")
            stringBuilder.append(mascotId)
            Log.v("SHIMEJI", stringBuilder.toString())
            if (eventNotifier != null) {
                eventNotifier?.hideMascot()
                timerTask = object : TimerTask() {
                    override fun run() {
                        eventNotifier?.showMascot()
                    }
                }
                timer.schedule(timerTask!!, Helper.getReappearDelayMs(mContext))
            }
            return true
        }

        override fun onDown(param1MotionEvent: MotionEvent): Boolean {
            if (mascot != null) {
                initialX = mascot.getX()
                initialY = mascot.getY()
            }
            return false
        }

        override fun onFling(
            param1MotionEvent1: MotionEvent,
            param1MotionEvent2: MotionEvent,
            param1Float1: Float,
            param1Float2: Float
        ): Boolean {
            if (Animation.flingEnabled) {
                val mascot = mascot
                val i = param1Float1.toInt()
                val j = param1Float2.toInt()
                mascot!!.setFlingSpeed(i, j)
                scroller.fling(
                    this@MascotView.mascot!!.getX(),
                    this@MascotView.mascot.getY(),
                    i,
                    j,
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

        override fun onScroll(
            param1MotionEvent1: MotionEvent,
            param1MotionEvent2: MotionEvent,
            param1Float1: Float,
            param1Float2: Float
        ): Boolean {
            drag(
                initialX + (param1MotionEvent2.rawX - param1MotionEvent1.rawX).toInt(),
                initialY + (param1MotionEvent2.rawY - param1MotionEvent1.rawY).toInt()
            )
            return false
        }

        override fun onSingleTapConfirmed(param1MotionEvent: MotionEvent): Boolean {
            val stringBuilder = StringBuilder()
            stringBuilder.append("Single tap on mascot: ")
            stringBuilder.append(mascotId)
            Log.v("SHIMEJI", stringBuilder.toString())
            return super.onSingleTapConfirmed(param1MotionEvent)
        }
    } as GestureDetector.OnGestureListener
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

    override fun dispatchTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        super.dispatchTouchEvent(paramMotionEvent)
        performClick()
        if (paramMotionEvent.action == 1) endDrag()
        return gestureDetector.onTouchEvent(paramMotionEvent)
    }

    fun drag(paramInt1: Int, paramInt2: Int) {
        mascot!!.drag(paramInt1, paramInt2)
    }

    fun draw() {
        if (isAnimationRunning) {
            val canvas: Canvas? = holder.lockCanvas()
            if (canvas != null) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR)
                val bitmap = mascot!!.getFrameBitmap()
                bitmapHeight = bitmap.height
                bitmapWidth = bitmap.width
                if (mascot.isFacingLeft) {
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
                } else {
                    flipHorizontalMatrix.setScale(-1.0f, 1.0f)
                    flipHorizontalMatrix.postTranslate(bitmapWidth.toFloat(), 0.0f)
                    canvas.drawBitmap(bitmap, flipHorizontalMatrix, paint)
                }
                holder.unlockCanvasAndPost(canvas)
            }
        }
        mHandler.removeCallbacks(drawRunner)
        mHandler.postDelayed(drawRunner, 16L)
    }

    fun endDrag() {
        mascot!!.endDragging()
    }

    override fun getX(): Float {
        if (mascot!!.isBeingFlung()) if (scroller.computeScrollOffset()) {
            mascot.fling(scroller.currX, scroller.currY)
        } else {
            mascot.endFlinging()
        }
        return mascot.getX().toFloat()
    }

    override fun getY(): Float {
        return mascot!!.getY().toFloat()
    }

    fun notifyLayoutChange(context: Context) {
        val playground = Playground(context, false)
        this.playground = playground
        mascot!!.resetEnvironmentVariables(playground)
    }

    fun pauseAnimation() {
        isAnimationRunning = false
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun resumeAnimation() {
        isAnimationRunning = true
    }

    fun setMascotEventsListener(paramMascotEventNotifier: MascotEventNotifier?) {
        eventNotifier = paramMascotEventNotifier
    }

    fun setSpeedMultiplier(paramDouble: Double) {
        mascot!!.setSpeedMultiplier(paramDouble)
    }

    override fun surfaceChanged(paramSurfaceHolder: SurfaceHolder, paramInt1: Int, paramInt2: Int, paramInt3: Int) {}
    override fun surfaceCreated(paramSurfaceHolder: SurfaceHolder) {
        mHandler.post(drawRunner)
    }

    override fun surfaceDestroyed(paramSurfaceHolder: SurfaceHolder) {
        mHandler.removeCallbacks(drawRunner)
    }

    interface MascotEventNotifier {
        fun hideMascot()
        fun showMascot()
    }

    companion object {
        val NEXT_ID: AtomicLong = AtomicLong(0L)
    }

    init {
        setBackgroundColor(0)
        setZOrderOnTop(true)
        speedMultiplier = Helper.getSpeedMultiplier(mContext)
        val d: Double = Helper.getSizeMultiplier(mContext)
        sizeMultiplier = d
        spritesService.setSizeMultiplier(mContext, d)
        val sprites: Sprites = spritesService.getSpritesById(mContext, mascotId)
        bitmapHeight = sprites.height
        bitmapWidth = sprites.width
        mascot = Mascot()
        val playground = Playground(mContext, false)
        this.playground = playground
        mascot.initialize(sprites, speedMultiplier, playground)
        mascot.startAnimation()
        holder.setFormat(-2)
        holder.addCallback(this)
        gestureDetector = GestureDetector(mContext, gestureListener)
        scroller = Scroller(mContext)
    }
}
