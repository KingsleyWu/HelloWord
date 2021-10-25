package com.kingsley.helloword.ui

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import com.kingsley.common.L
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random


/**
 * @author Kingsley
 * Created on 2021/5/17.
 */
class JigsawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener {

    private var TAG = "TAG"

    //表格大小
    private var tableSize = 3

    //二维数组，存放图标块
    private val pictureBlock2dMap = Array(tableSize) { Array<PictureBlock?>(tableSize) { null } }

    //手势监听
    private var gestureDetector: GestureDetector = GestureDetector(context, this)

    //是否开始
    private var isStart: Boolean = false

    //空白点坐标
    private var moveBlockPoint: Point = Point(-1, -1)

    //top偏移
    private var offsetTop: Int = 0

    //left偏移
    private var offsetLeft: Int = 0

    //图片大小
    private var gridItemSize = 0
    private var slideAnimatorDuration: Long = 150
    private var showSourceBitmap = false

    //移动步数
    private var step: Int = 0

    private var itemMovInterpolator: Interpolator = OvershootInterpolator()

    //目标Bitmap
    private lateinit var targetPicture: Bitmap

    fun setPicture(bitmap: Bitmap) {
        post {
            targetPicture = bitmap.getCenterBitmap()
            parsePicture()
            step = 0
        }
    }

    //分割图片
    private fun parsePicture() {
        var top: Int
        var left: Int
        var position = 0
        for (i in pictureBlock2dMap.indices) {
            for (j in pictureBlock2dMap[i].indices) {
                position++
                left = j * gridItemSize
                top = i * gridItemSize
                pictureBlock2dMap[i][j] =
                    PictureBlock(
                        createBitmap(left, top, gridItemSize),
                        position,
                        left,
                        top
                    )
            }
        }
        pictureBlock2dMap[tableSize - 1][tableSize - 1]!!.bitmap = createSolidColorBitmap(width)
        isStart = true
        randomPosition()
        invalidate()

    }

    private fun randomPosition() {
        for (i in 1..pictureBlock2dMap.size * pictureBlock2dMap.size) {
            val srcIndex = Random.nextInt(0, pictureBlock2dMap.size)
            val dstIndex = Random.nextInt(0, pictureBlock2dMap.size)
            val srcIndex1 = Random.nextInt(0, pictureBlock2dMap.size)
            val dstIndex2 = Random.nextInt(0, pictureBlock2dMap.size)
            pictureBlock2dMap[srcIndex][dstIndex]!!.swap(pictureBlock2dMap[srcIndex1][dstIndex2]!!)
        }

        for (i in pictureBlock2dMap.indices) {
            for (j in pictureBlock2dMap[i].indices) {
                val item = pictureBlock2dMap[i][j]!!
                if (item.position == tableSize * tableSize) {
                    moveBlockPoint.set(i, j)
                    return
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        offsetTop = (h - w) / 2
        if (offsetTop < 0) {
            offsetTop = 0
        }
        offsetLeft = if (w > h) (w - h) / 2 else 0
        gridItemSize = min(w, h) / tableSize
        L.d("wwc onSizeChanged offsetTop = $offsetTop, gridItemSize = $gridItemSize")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val min = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(min, min)
    }

    private val pictureRect = Rect()
    private val mRect = Rect()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        if (!isStart) {
            return
        }
        if (showSourceBitmap) {
            pictureRect.set(0, 0, targetPicture.width, targetPicture.height)
            mRect.set(offsetLeft, 0, measuredWidth + offsetLeft, measuredHeight)
            canvas.drawBitmap(targetPicture, pictureRect, mRect, mPaint)
            return
        }
        var left: Int
        var top: Int
        for (i in pictureBlock2dMap.indices) {
            for (j in pictureBlock2dMap[i].indices) {
                val item = pictureBlock2dMap[i][j]!!
                left = item.left + offsetLeft
                top = item.top
                val bitmap = pictureBlock2dMap[i][j]!!.bitmap
                pictureRect.set(0, 0, bitmap.width, bitmap.height)
                mRect.set(
                    left,
                    top + offsetTop,
                    gridItemSize + left,
                    gridItemSize + top + offsetTop
                )
                canvas.drawBitmap(bitmap, pictureRect, mRect, mPaint)
            }
        }

    }

    //交换内容
    private fun PictureBlock.swap(target: PictureBlock) {
        target.position = this.position.also {
            this.position = target.position
        }
        target.bitmap = this.bitmap.also {
            this.bitmap = target.bitmap
        }
    }

    private fun Bitmap.getCenterBitmap(): Bitmap {
        //如果图片宽度大于View宽度
        val min = min(this.height, this.width)
        val minSize = min(measuredWidth, measuredHeight)
        L.d("wwc onSizeChanged getCenterBitmap height = $height , width = $width , measuredWidth = $measuredWidth")
        if (min < minSize) {
            val matrix = Matrix()
            val sx: Float = minSize / min.toFloat()
            matrix.setScale(sx, sx)
            return when (min) {
                height -> {
                    Bitmap.createBitmap(
                        this, (width - height) / 2, 0,
                        height,
                        height,
                        matrix,
                        true
                    )
                }
                width -> {
                    Bitmap.createBitmap(
                        this, 0, (height - width) / 2,
                        width,
                        width,
                        matrix,
                        true
                    )
                }
                else -> {
                    ThumbnailUtils.extractThumbnail(this, minSize, minSize)
                }
            }
        }
        return this
    }

    fun setTarget(targetPicture: Bitmap) {
        this.targetPicture = targetPicture
    }

    private fun createSolidColorBitmap(size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.TRANSPARENT)
        return bitmap
    }

    private fun createBitmap(left: Int, top: Int, size: Int): Bitmap {
        L.d("wwc onSizeChanged createBitmap left = $left , top = $top , size = $size , targetPicture.width = ${targetPicture.width} , targetPicture.height = ${targetPicture.height} , top + size = ${top + size}")
        // Bitmap.createBitmap(targetPicture, left, top, size, size)
        return Bitmap.createBitmap(targetPicture, left, top, size, size)
    }


    private fun List<Int>.isOrder(): Boolean {
        for (i in 1 until this.size) {
            if (this[i] - this[i - 1] != 1) {
                return false
            }
        }
        return true
    }

    private fun isFinish() {
        val list = mutableListOf<Int>()
        for (i in pictureBlock2dMap.indices) {
            for (j in pictureBlock2dMap[i].indices) {
                val item = pictureBlock2dMap[i][j]!!
                list.add(item.position)
            }
        }
        if (list.isOrder()) {
            finish()
        }
    }

    private fun finish() {
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
    }

    private fun startAnimator(
        start: Int,
        end: Int,
        srcPoint: Point,
        dstPoint: Point,
        type: Boolean
    ) {
        val handler = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                pictureBlock2dMap[dstPoint.x][dstPoint.y] =
                    pictureBlock2dMap[srcPoint.x][srcPoint.y].also {
                        pictureBlock2dMap[srcPoint.x][srcPoint.y] =
                            pictureBlock2dMap[dstPoint.x][dstPoint.y]!!
                    }
                invalidate()
                isFinish()

            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        }
        val animatorSet = AnimatorSet()
        animatorSet.addListener(handler)
        animatorSet.playTogether(ValueAnimator.ofFloat(start.toFloat(), end.toFloat()).apply {
            duration = slideAnimatorDuration
            interpolator = itemMovInterpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                if (type) {
                    pictureBlock2dMap[srcPoint.x][srcPoint.y]!!.left = value.toInt()
                } else {
                    pictureBlock2dMap[srcPoint.x][srcPoint.y]!!.top = value.toInt()

                }
                invalidate()
            }
        }, ValueAnimator.ofFloat(end.toFloat(), start.toFloat()).apply {
            duration = slideAnimatorDuration
            interpolator = itemMovInterpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                if (type) {
                    pictureBlock2dMap[dstPoint.x][dstPoint.y]!!.left = value.toInt()
                } else {
                    pictureBlock2dMap[dstPoint.x][dstPoint.y]!!.top = value.toInt()

                }
                invalidate()
            }
        })
        animatorSet.start()

    }


    private fun doMoveTopBottom(direction: Boolean) {
        if ((moveBlockPoint.x == 0 && direction) || (moveBlockPoint.x == tableSize - 1 && !direction)) {
            return
        }
        step++
        val value = if (direction) 1 else {
            -1
        }

        val start = moveBlockPoint.x * gridItemSize
        val end = (moveBlockPoint.x - (value)) * gridItemSize

        startAnimator(
            start, end, Point(moveBlockPoint.x, moveBlockPoint.y),
            Point(moveBlockPoint.x - (value), moveBlockPoint.y),
            false
        )
        moveBlockPoint.x = moveBlockPoint.x - (value)

    }

    private fun doMoveLeftRight(direction: Boolean) {
        if ((moveBlockPoint.y == 0 && direction) || (moveBlockPoint.y == tableSize - 1 && !direction)) {
            return
        }
        step++
        val value = if (direction) 1 else {
            -1
        }

        val start = moveBlockPoint.y * gridItemSize
        val end = (moveBlockPoint.y - (value)) * gridItemSize

        startAnimator(
            start, end, Point(moveBlockPoint.x, moveBlockPoint.y),
            Point(moveBlockPoint.x, moveBlockPoint.y - (value)),
            true
        )

        moveBlockPoint.y = moveBlockPoint.y - (value)

    }

    /**
     * 图片块
     */
    inner class PictureBlock(
        var bitmap: Bitmap,
        var position: Int = 0,
        var left: Int = 0,
        var top: Int = 0
    )

    override fun onTouchEvent(event: MotionEvent): Boolean {
        L.i(TAG, "onTouchEvent: ")
        if (event.action == MotionEvent.ACTION_UP) {
            L.i(TAG, "onDown: ACTION_UP")
            showSourceBitmap = false
            invalidate()
        }
        return gestureDetector.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent?) {
        L.i(TAG, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        L.i(TAG, "onSingleTapUp: ")
        return true

    }

    override fun onDown(e: MotionEvent): Boolean {
        L.i(TAG, "onDown: ")
        return true
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val moveXDistance = abs(e1.x - e2.x)
        val moveYDistance = abs(e1.y - e2.y)
        if (moveXDistance > moveYDistance) {
            doMoveLeftRight(e1.x < e2.x)
            return true
        }
        doMoveTopBottom(e1.y < e2.y)
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        showSourceBitmap = true
        invalidate()
        postDelayed({
            showSourceBitmap = false
            invalidate()
        }, 5000)
    }
}