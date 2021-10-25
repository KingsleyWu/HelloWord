package com.kingsley.helloword.customize_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.kingsley.base.dp

class ProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {

    }

    /** 線的寬度 */
    private var mStrokeWidth = 6f.dp / 2
    private val mCornerRadii = floatArrayOf(mStrokeWidth, mStrokeWidth, mStrokeWidth, mStrokeWidth, 0f, 0f, 0f, 0f)

    private val mGradientDrawable = GradientDrawable().apply {
        cornerRadii = mCornerRadii
    }

    private val mPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPath.reset()
        mPath.lineTo(0f, mStrokeWidth)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addArc(0f, 0f, mStrokeWidth * 2, mStrokeWidth, 0f, 180f)
        }
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }
}