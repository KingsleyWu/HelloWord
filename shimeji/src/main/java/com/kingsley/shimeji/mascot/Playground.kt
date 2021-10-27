package com.kingsley.shimeji.mascot

import android.content.Context
import android.util.DisplayMetrics


class Playground {
    var bottom = 0
        private set
    var left: Int
        private set
    var right: Int
        private set
    var top: Int
        private set

    constructor(paramInt1: Int, paramInt2: Int, paramInt3: Int, paramInt4: Int) {
        top = paramInt1
        bottom = paramInt2
        left = paramInt3
        right = paramInt4
    }

    constructor(paramContext: Context, paramBoolean: Boolean) {
        val displayMetrics: DisplayMetrics = paramContext.resources.displayMetrics
        if (paramBoolean) {
            bottom = displayMetrics.heightPixels - dpToPx(128.0f, displayMetrics.density)
        } else {
            var i = 75
            val j: Int = paramContext.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (j > 0) i = paramContext.resources.getDimensionPixelSize(j)
            bottom = displayMetrics.heightPixels - i
        }
        right = displayMetrics.widthPixels
        top = 0
        left = 0
    }

    private fun dpToPx(paramFloat1: Float, paramFloat2: Float): Int {
        return (paramFloat2 * paramFloat1 + 0.5f).toInt()
    }
}
