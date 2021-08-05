package com.kingsley.helloword.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * @author Kingsley
 * Created on 2021/7/19.
 */
class SquareMatrixView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mColors = intArrayOf(-0x5135c, -0x112390)
    private var mCamera: Camera = Camera()
    private var mMatrix: Matrix = Matrix()
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mRectF: RectF = RectF(-100f, -100f, 100f, 100f)
    private var mAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(20000)
    private var viewWidth = 0
    private var viewHeight = 0
    private var value = 0f

    init{
        mAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimator.addUpdateListener { animation ->
            value = animation.animatedValue as Float
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val degree = value * 360
        if (degree >= 0 && degree < 90) {
            drawCube(1, canvas)
            drawCube(0, canvas)
        }

        if (degree >= 90 && degree < 135) {
            drawCube(2, canvas)
            drawCube(1, canvas)
        }

        if (degree >= 135 && degree < 180) {
            drawCube(1, canvas)
            drawCube(2, canvas)
        }

        if (degree >= 180 && degree < 225) {
            drawCube(3, canvas)
            drawCube(2, canvas)
        }

        if (degree >= 225 && degree < 270) {
            drawCube(2, canvas)
            drawCube(3, canvas)
        }

        if (degree in 270.0..360.0) {
            drawCube(3, canvas)
            drawCube(0, canvas)
        }

        mCamera.save()
        mCamera.rotateY(45f)
        mCamera.rotateX(value * 360)
        mCamera.rotateY(90f)
        mCamera.translate(100f, 0f, 100f)
        mCamera.getMatrix(mMatrix)
        mCamera.restore()

        // control center
        mMatrix.preTranslate((-viewWidth / 2).toFloat(), (-viewHeight / 2).toFloat())
        mMatrix.postTranslate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())

        canvas.save()
        canvas.concat(mMatrix)
        canvas.translate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        mPaint.color = -0x2021d
        canvas.drawRect(mRectF, mPaint)
        canvas.restore()
    }

    fun drawCube1(canvas: Canvas){

    }


    /**
     *
     * @param position 面的序号 0~3 面的序号后面提到
     * @param canvas
     */
    fun drawCube1(position: Int, canvas: Canvas) {
        mCamera.save()
        //1.旋转成水平或者垂直
        mCamera.rotateX((position % 2 * 90).toFloat())
        when (position) {
            0 -> {
            }
            1 -> mCamera.translate(0f, 100f, 100f)
            2 -> mCamera.translate(0f, 0f, 200f)
            3 -> mCamera.translate(0f, 100f, -100f)
        }
        mCamera.getMatrix(mMatrix)
        mCamera.restore()

        //3.由于3D坐标系原点默认是屏幕左上角, 故调整3D中心点。而前乘再后乘,是matrix的一个小技巧
        mMatrix.preTranslate((-viewWidth / 2).toFloat(), (-viewHeight / 2).toFloat())
        mMatrix.postTranslate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())


        //4. 调整2D坐标系 并且画出正方体
        canvas.save()
        canvas.concat(mMatrix)
        canvas.translate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        mPaint.color = mColors[position % 2]
        canvas.drawRect(mRectF, mPaint)
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = measuredWidth
        viewHeight = measuredHeight
    }

    override fun onAttachedToWindow() {
        mAnimator.start()
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        mAnimator.cancel()
        super.onDetachedFromWindow()
    }

    /**
     *
     * @param position side of cube 0~3
     * @param canvas
     */
    fun drawCube(position: Int, canvas: Canvas) {
        mCamera.save()
        mCamera.rotateY(45F)
        mCamera.rotateX(value * 360 + position % 2 * 90)
        when (position) {
            0 -> {}
            1 -> mCamera.translate(0F, 100F, 100F)
            2 -> mCamera.translate(0F, 0F, 200F)
            3 -> mCamera.translate(0F, 100F, -100f)
        }
        mCamera.getMatrix(mMatrix)
        mCamera.restore()

        // control center
        mMatrix.preTranslate((-viewWidth / 2).toFloat(), (-viewHeight / 2).toFloat())
        mMatrix.postTranslate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        canvas.save()
        canvas.concat(mMatrix)
        canvas.translate((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat())
        mPaint.color = mColors[position % 2]
        canvas.drawRect(mRectF, mPaint)
        canvas.restore()
    }
}