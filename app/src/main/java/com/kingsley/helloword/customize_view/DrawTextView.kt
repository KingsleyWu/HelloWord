package com.kingsley.helloword.customize_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.kingsley.base.sp

class DrawTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 32.sp
    }
    private val fontMetrics: Paint.FontMetrics = mPaint.fontMetrics
    var text = "国"

    override fun onDraw(canvas: Canvas){
        super.onDraw(canvas)
        mPaint.color = Color.BLUE
        canvas.drawText(text, mPaint, 0, measuredHeight - 32)

        mPaint.color = Color.GREEN
        canvas.drawText(text, mPaint, measuredHeight - 32, measuredHeight)
    }

    private fun Canvas.drawText(text: String, paint: Paint, top: Int, bottom: Int) {
        //裁剪Rect,绘制不变色的文字部分
        save() //保存画布
        val rect = Rect(0, top, measuredWidth, bottom)
        clipRect(rect)
        drawText(text, 0f, measuredHeight.toFloat() - fontMetrics.descent, paint)
        restore() //回收画布
    }
}