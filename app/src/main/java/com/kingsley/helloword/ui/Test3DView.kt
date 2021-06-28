package com.kingsley.helloword.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kingsley.helloword.R


/**
 * @author Kingsley
 * Created on 2021/5/19.
 */
class Test3DView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //摄像机
    private var mCamera: Camera

    //翻转用的图片
    private var face: Bitmap
    private val mMatrix: Matrix = Matrix()
    private val mPaint: Paint = Paint()

    private var mLastMotionX = 0
    private var mLastMotionY: Int = 0

    //图片的中心点坐标
    private var centerX = 0
    //图片的中心点坐标
    private var centerY = 0

    //转动的总距离，跟度数比例1:1
    private var deltaX = 0
    //转动的总距离，跟度数比例1:1
    private var deltaY = 0

    //图片宽度高度
    private var bWidth = 0
    //图片宽度高度
    private var bHeight = 0

    init {
        setWillNotDraw(false)
        mCamera = Camera()
        mPaint.isAntiAlias = true
        face = BitmapFactory.decodeResource(resources, R.drawable.test)
        bWidth = face.width
        bHeight = face.height
        centerX = bWidth shr 1
        centerY = bHeight shr 1
    }

    /**
     * 转动
     * @param degreeX
     * @param degreeY
     */
    fun rotate(degreeX: Int, degreeY: Int) {
        deltaX += degreeX
        deltaY += degreeY
        mCamera.save()
        mCamera.rotateY(deltaX.toFloat())
        mCamera.rotateX((-deltaY).toFloat())
        mCamera.translate(0F, 0F, (-centerX).toFloat())
        mCamera.getMatrix(mMatrix)
        mCamera.restore()
        //以图片的中心点为旋转中心,如果不加这两句，就是以（0,0）点为旋转中心
        mMatrix.preTranslate((-centerX).toFloat(), (-centerY).toFloat())
        mMatrix.postTranslate(centerX.toFloat(), centerY.toFloat())
        mCamera.save()
        postInvalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionX = x
                mLastMotionY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mLastMotionX
                val dy: Int = y - mLastMotionY
                rotate(dx, dy)
                mLastMotionX = x
                mLastMotionY = y
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(face, mMatrix, mPaint)
    }
}