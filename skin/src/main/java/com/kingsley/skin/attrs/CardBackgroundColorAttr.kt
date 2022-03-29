package com.kingsley.skin.attrs

import android.view.View
import androidx.cardview.widget.CardView
import com.kingsley.skin.SkinElementAttr
import com.kingsley.skin.SkinManager

/**
 * CardView
 *
 * android:cardBackgroundColor
 */
class CardBackgroundColorAttr : SkinElementAttr() {

    companion object {
        const val TAG = "CardBackgroundColorAttr"

        fun registerSkinAttr() {
            SkinManager
                .registerSkinAttr("cardBackgroundColor", CardBackgroundColorAttr::class.java)
                .registerSkinStyle(
                    "cardBackgroundColor",
                    androidx.cardview.R.styleable.CardView_cardBackgroundColor,
                )
        }
    }

    override fun apply(view: View?) {
        (view as? CardView)?.run {
            setCardBackgroundColor(SkinManager.getColorStateList(context, attrValueRefId))
        }
    }

}