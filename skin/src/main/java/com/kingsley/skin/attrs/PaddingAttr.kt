package com.kingsley.skin.attrs

import android.view.View
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * view
 * android:paddingXXXX
 */
class PaddingAttr : SkinElementAttr() {

    companion object {
        const val TAG = "PaddingAttr"
    }

    override fun apply(view: View?) {
        super.apply(view)
        view?.run {
            val padding = SkinManager.getDimension(context, attrValueRefId).toInt()

            when (attrName) {
                "padding" -> {
                    setPadding(padding, padding, padding, padding)
                }
                "paddingLeft" -> {
                    setPadding(padding, paddingTop, paddingRight, paddingBottom)
                }
                "paddingStart" -> {
                    setPadding(padding, paddingTop, paddingRight, paddingBottom)
                }
                "paddingRight" -> {
                    setPadding(paddingLeft, paddingTop, padding, paddingBottom)
                }
                "paddingEnd" -> {
                    setPadding(paddingLeft, paddingTop, padding, paddingBottom)
                }
                "paddingTop" -> {
                    setPadding(paddingLeft, padding, paddingRight, paddingBottom)
                }
                "paddingBottom" -> {
                    setPadding(paddingLeft, paddingTop, paddingRight, padding)
                }
            }
        }
    }
}