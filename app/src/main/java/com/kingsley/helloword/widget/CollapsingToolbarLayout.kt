package com.kingsley.helloword.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.CollapsingToolbarLayout


/**
 * 修复CollapsingToolbarLayout，当FitsSystemWindows时，CollapsingToolbarLayout底部出现多余padding的问题。
 */
class CollapsingToolbarLayout :
    CollapsingToolbarLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var tempHeightMeasureSpec = heightMeasureSpec
        super.onMeasure(widthMeasureSpec, tempHeightMeasureSpec)
        if (childCount > 0 && getChildAt(0).fitsSystemWindows) {
            try {
                val fs = CollapsingToolbarLayout::class.java.getDeclaredField("lastInsets")
                fs.isAccessible = true
                val mLastInsets = fs[this] as? WindowInsetsCompat
                val mode = MeasureSpec.getMode(tempHeightMeasureSpec)
                val topInset = mLastInsets?.systemWindowInsetTop ?: 0
                if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
                    // fix the bottom empty padding
                    tempHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        measuredHeight - topInset, MeasureSpec.EXACTLY
                    )
                    super.onMeasure(widthMeasureSpec, tempHeightMeasureSpec)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
