package com.kingsley.tetris.view

import android.content.Context
import android.renderscript.RenderScript
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import kotlin.jvm.Volatile

class BlurShadow private constructor() {
    private var renderScript: RenderScript? = null
    fun init(context: Context?) {
        if (renderScript == null) {
            renderScript = RenderScript.create(context)
        }
    }

    fun blur(view: ImageView, width: Int, height: Int, radius: Float): Bitmap {
        val src = getBitmapForView(view, DOWNSCALE_FACTOR, width, height)
        val input = Allocation.createFromBitmap(renderScript, src)
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(src)
        return src
    }

    private fun getBitmapForView(view: ImageView, downscaleFactor: Float, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            (width * downscaleFactor).toInt(),
            (height * downscaleFactor).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val matrix = Matrix()
        matrix.preScale(downscaleFactor, downscaleFactor)
        canvas.setMatrix(matrix)
        view.draw(canvas)
        return bitmap
    }

    companion object {
        private const val DOWNSCALE_FACTOR = 0.25f

        @Volatile
        private var blurShadow: BlurShadow? = null
        @JvmStatic
        val instance: BlurShadow?
            get() {
                if (blurShadow == null) {
                    synchronized(BlurShadow::class.java) {
                        if (blurShadow == null) {
                            blurShadow = BlurShadow()
                        }
                    }
                }
                return blurShadow
            }
    }
}