package com.kingsley.skin.attrs

import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * android:textSize
 */
class TextSizeAttr : SkinElementAttr() {

    companion object {
        const val TAG = "TextSizeAttr"
    }

    override fun apply(view: View?) {
        super.apply(view)
        (view as? TextView)?.run {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, SkinManager.getDimension(context, attrValueRefId))
        }
    }
}