package com.kingsley.skin.attrs

import android.view.View
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * AppCompatAutoCompleteTextView
 *
 * android:popupBackground
 */
class AutoCompleteAttr : SkinElementAttr() {

    companion object {
        const val TAG = "AutoCompleteAttr"

        fun registerSkinAttr(){
            SkinManager
                .registerSkinStyle("popupBackground", android.R.attr.popupBackground)
                .registerSkinAttr("popupBackground", AutoCompleteAttr::class.java)
        }
    }

    override fun apply(view: View?) {
        super.apply(view)
//        Log.d(TAG, "applyView:$view, this: $this")

        (view as? AppCompatAutoCompleteTextView)?.run {
            when (attrName) {
                "popupBackground" ->
                    setDropDownBackgroundDrawable(SkinManager.getDrawable(context, attrValueRefId))
            }
        }
    }
}