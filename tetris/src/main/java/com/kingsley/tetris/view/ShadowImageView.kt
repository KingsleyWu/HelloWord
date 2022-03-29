package com.kingsley.tetris.view

import android.content.Context
import com.kingsley.tetris.view.BlurShadow.Companion.instance
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatImageView
import com.kingsley.tetris.R
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.ViewTreeObserver
import android.graphics.drawable.Drawable
import android.graphics.ColorMatrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet

class ShadowImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(
    context, attrs, defStyleAttr
) {
    private var radiusOffset = DEFAULT_RADIUS
    private var shadowWidth = 0f
    private var saturation = 0f
    private fun init(attrs: AttributeSet?) {
        instance!!.init(context)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowImageView, 0, 0)
        setRadiusOffset(typedArray.getFloat(R.styleable.ShadowImageView_radiusOffset, DEFAULT_RADIUS))
        val defaultShadowWidth = dp2px(DEFAULT_SHADOW_WIDTH)
        shadowWidth = typedArray.getDimension(R.styleable.ShadowImageView_shadowWidth, defaultShadowWidth.toFloat())
        saturation = typedArray.getFloat(R.styleable.ShadowImageView_saturation, DEFAULT_SATURATION)
        typedArray.recycle()
        cropToPadding = true
        setPadding(shadowWidth.toInt(), shadowWidth.toInt(), shadowWidth.toInt(), shadowWidth.toInt())
    }

    fun setRadiusOffset(newValue: Float) {
        if (newValue > 0 && newValue <= 25) {
            radiusOffset = newValue
        } else if (newValue > 25) {
            radiusOffset = 25f
        }
    }

    override fun setImageBitmap(bm: Bitmap) {
        if (height != 0 && measuredHeight != 0) {
            super.setImageBitmap(bm)
            makeBlurShadow()
        } else {
            super.setImageBitmap(bm)
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    makeBlurShadow()
                    return false
                }
            })
        }
    }

    override fun setImageResource(resId: Int) {
        if (height != 0 && measuredHeight != 0) {
            super.setImageResource(resId)
            makeBlurShadow()
        } else {
            super.setImageResource(resId)
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    makeBlurShadow()
                    return false
                }
            })
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (height != 0 && measuredHeight != 0) {
            super.setImageDrawable(drawable)
            makeBlurShadow()
        } else {
            super.setImageDrawable(drawable)
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    makeBlurShadow()
                    return false
                }
            })
        }
    }

    fun setImageResource(resId: Int, withShadow: Boolean) {
        if (withShadow) {
            setImageResource(resId)
        } else {
            super.setImageResource(resId)
        }
    }

    fun setImageDrawable(drawable: Drawable?, withShadow: Boolean) {
        if (withShadow) {
            setImageDrawable(drawable)
        } else {
            super.setImageDrawable(drawable)
        }
    }

    private fun makeBlurShadow() {
        val bitmap = bitmap
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, BRIGHTNESS,
                0f, 1f, 0f, 0f, BRIGHTNESS,
                0f, 0f, 1f, 0f, BRIGHTNESS,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix.setSaturation(saturation)
        val bitmapDrawable = BitmapDrawable(resources, bitmap)
        bitmapDrawable.colorFilter = ColorMatrixColorFilter(colorMatrix)
        super.setImageDrawable(bitmapDrawable)
        val radius = radiusOffset
        val blur = instance!!.blur(this, width, height - dp2px(HEIGHT_OFFSET), radius)
        background = BitmapDrawable(resources, blur)
    }

    private val bitmap: Bitmap
        get() {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            draw(canvas)
            return bitmap
        }

    private fun dp2px(dpValue: Float): Int {
        val scale = resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    companion object {
        private const val DEFAULT_RADIUS = 7.0f
        private const val BRIGHTNESS = -25f
        private const val DEFAULT_SATURATION = 1.0f
        private const val HEIGHT_OFFSET = 2f
        private const val DEFAULT_SHADOW_WIDTH = 6.0f
    }

    init {
        init(attrs)
    }
}