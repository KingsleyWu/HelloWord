package com.kingsley.tetris.view

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import android.content.res.AssetManager
import android.graphics.Typeface
import android.util.AttributeSet
import com.kingsley.tetris.view.LedTextView
import java.io.File

class LedTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val assetManager = context.assets
        val font = Typeface.createFromAsset(assetManager, FONT_DIGITAL_7)
        typeface = font
    }

    companion object {
        private const val FONTS_FOLDER = "fonts"
        private val FONT_DIGITAL_7 = (FONTS_FOLDER + File.separator
                + "digital-7.ttf")
    }
}