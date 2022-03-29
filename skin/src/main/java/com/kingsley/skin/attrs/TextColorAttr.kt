package com.kingsley.skin.attrs

import android.view.View
import android.widget.TextView
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * TextView
 *
 * android:textColor
 */
class TextColorAttr : SkinElementAttr() {

    companion object {
        const val TAG = "TextColorAttr"
    }

    override fun apply(view: View?) {
        super.apply(view)
//        Log.d(TAG, "applyView:$view, this: $this")

        (view as? TextView)?.run {
            when (attrName) {
                "textColor" ->
                    setTextColor(SkinManager.getColorStateList(context, attrValueRefId))
                "textColorHint" ->
                    setHintTextColor(SkinManager.getColorStateList(context, attrValueRefId))

            }
        }
    }
}