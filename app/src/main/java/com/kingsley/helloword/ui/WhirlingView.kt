package com.kingsley.helloword.ui

import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Debug
import android.util.AttributeSet
import android.view.View
import com.kingsley.base.dp
import kotlin.math.min

/**
 * @author Kingsley
 * Created on 2021/5/17.
 */
class WhirlingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = RandomColor.color()
        textSize = 14f.dp
    } }

    private val mCamera by lazy { Camera().apply {
        rotateX(80f)
        setLocation(0f,0f, -20f * resources.displayMetrics.density)
    } }

    private var mText = ""
    private var mTextColor = mutableListOf<Int>()
    private var strokeWidth = 20f.dp

    fun setText(text: String) {
        mText = text
        val length = mText.length
        mTextColor.clear()
        for (i in 0..length) {
            mTextColor.add(i, RandomColor.color())
        }
        mPaint.strokeWidth = strokeWidth
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        mCamera.save()
        canvas.translate((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
        mCamera.applyToCanvas(canvas)
        canvas.translate(-(measuredWidth / 2).toFloat(), -(measuredHeight / 2).toFloat())
        mPaint.style = Paint.Style.STROKE
        canvas.drawCircle(
            (measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat(),
            min(measuredHeight, measuredWidth) / 2 - strokeWidth / 2, mPaint
        )
        canvas.restore()
        mCamera.restore()
        mPaint.style = Paint.Style.FILL
        val measureText = mPaint.measureText(mText)
        canvas.drawText(mText, (measuredWidth / 2).toFloat() - measureText / 2, (measuredHeight / 2).toFloat() + mPaint.textSize / 2, mPaint)
    }
}