package com.kingsley.skin.attrs

import android.util.Log
import android.view.View
import android.widget.ImageView
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * ImageView
 * android:src
 */
class ImageSrcAttr : SkinElementAttr() {

    companion object {
        const val TAG = "ImageSrcAttr"
    }

    override fun apply(view: View?) {
        super.apply(view)
        Log.d(TAG, "applyView:$view, this: $this")
        (view as? ImageView)?.run {
            setImageDrawable(SkinManager.getDrawable(context, attrValueRefId))
        }
    }
}