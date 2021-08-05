package com.kingsley.helloword.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * @author Kingsley
 * Created on 2021/7/19.
 */
class DashboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.RED
    }
    private var viewWidth = 0f
    private var viewHeight = 0f
    private var mPath: Path = Path()
    private var viewMin = 0f
    private var offset = 0f
    private var isWidthMin = false
    private var padding = 10f
    private var count = 10

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat() - padding
        viewHeight = h.toFloat() - padding
        if (viewWidth < viewHeight) {
            isWidthMin = true
            viewMin = viewWidth
            offset = (viewHeight - viewWidth) / 2
        } else {
            isWidthMin = false
            viewMin = viewHeight
            offset = (viewWidth - viewHeight) / 2
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.reset()

        for (i in 0..count) {
            if (isWidthMin) {
                // 畫豎線
                setVerticalPathWithWidth(i, mPath)
                // 畫橫線
                setHorizontalPathWithWidth(i, mPath)
            } else {
                // 畫豎線
                setVerticalPathWithHeight(i, mPath)
                // 畫橫線
                setHorizontalPathWithHeight(i, mPath)
            }
        }
        canvas.drawPath(mPath, mPaint)
    }

    private fun setHorizontalPathWithHeight(position: Int, path: Path) {
        path.moveTo(offset + padding / 2, (viewMin * position / count) + padding / 2)
        path.lineTo(offset + padding / 2 + viewMin, (viewMin * position / count) + padding / 2)
    }

    private fun setVerticalPathWithHeight(position: Int, path: Path) {
        path.moveTo(offset + viewMin * position / count + padding / 2, padding / 2)
        path.lineTo(offset + viewMin * position / count + padding / 2, viewMin + padding / 2)
    }

    private fun setHorizontalPathWithWidth(position: Int, path: Path) {
        path.moveTo(padding / 2, offset + (viewMin * position / count))
        path.lineTo(padding / 2 + viewMin, offset + (viewMin * position / count))
    }

    private fun setVerticalPathWithWidth(position: Int, path: Path) {
        path.moveTo(viewMin * position / count + padding / 2, offset)
        path.lineTo(viewMin * position / count + padding / 2, viewMin + offset)
    }
}