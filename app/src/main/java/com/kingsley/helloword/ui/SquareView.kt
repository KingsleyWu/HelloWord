package com.kingsley.helloword.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * @author Kingsley
 * Created on 2021/7/19.
 */
class SquareView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var viewWidth = 0
    private var viewHeight = 0
    private var mPath: Path = Path()
    private var viewMin = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        viewMin = min(viewWidth, viewHeight)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.reset()
        mPath.lineTo(0f, viewMin.toFloat())
        mPath.lineTo(viewMin.toFloat() * 3 / 5, viewMin.toFloat())
        mPath.lineTo(viewMin.toFloat() * 3 / 5, viewMin.toFloat() * 3 / 5)
        mPath.lineTo(viewMin.toFloat() * 2 / 5, viewMin.toFloat() * 3 / 5)
        mPath.lineTo(viewMin.toFloat() * 2 / 5, viewMin.toFloat() * 4 / 5)
        mPath.lineTo(viewMin.toFloat() * 1 / 5, viewMin.toFloat() * 4 / 5)
        mPath.lineTo(viewMin.toFloat() * 1 / 5, 0f)
//        mPath.lineTo(0f, 500f)
//        mPath.lineTo(300f,500f)
//        mPath.lineTo(300f,300f)
//        mPath.lineTo(200f,300f)
//        mPath.lineTo(200f,400f)
//        mPath.lineTo(100f,400f)
//        mPath.lineTo(100f,0f)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }
}