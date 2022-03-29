package com.kingsley.skin.attrs

import android.view.View
import android.view.ViewGroup
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * View
 * android:marginX
 */
class MarginAttr : SkinElementAttr() {

    companion object {
        const val TAG = "MarginAttr"
    }

    override fun apply(view: View?) {
        super.apply(view)
        view?.run {
            val margin = SkinManager.getDimension(context, attrValueRefId).toInt()
            val lp = (layoutParams as? ViewGroup.MarginLayoutParams) ?: return
            when (attrName) {
                "layout_margin" -> {
                    lp.run {
                        leftMargin = margin
                        topMargin = margin
                        rightMargin = margin
                        bottomMargin = margin
                    }
                }
                "layout_marginLeft" -> {
                    lp.run {
                        leftMargin = margin
                    }
                }
                "layout_marginStart" -> {
                    lp.run {
                        leftMargin = margin
                    }
                }
                "layout_marginRight" -> {
                    lp.run {
                        rightMargin = margin
                    }
                }
                "layout_marginEnd" -> {
                    lp.run {
                        rightMargin = margin
                    }
                }
                "layout_marginTop" -> {
                    lp.run {
                        topMargin = margin
                    }
                }
                "layout_marginBottom" -> {
                    lp.run {
                        bottomMargin = margin
                    }
                }
                else -> {}
            }
        }
    }
}