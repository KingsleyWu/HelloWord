package com.kingsley.shimeji.mascot

import android.graphics.Bitmap
import java.lang.IllegalArgumentException


class Sprites(paramHashMap: HashMap<Int, Bitmap>?) : HashMap<Int, Bitmap?>() {
    val height: Int
        get() = if (size > 0) get(Integer.valueOf(0))!!.height else 0
    val width: Int
        get() = if (size > 0) get(Integer.valueOf(0))!!.width else 0
    val xOffset: Int
        get() = width / 3

    fun recycle() {
        for (integer in keys) {
            get(integer)?.recycle()
            put(integer, null)
        }
    }

    init {
        if (paramHashMap != null && paramHashMap.isNotEmpty()) {
            putAll(paramHashMap)
        } else {
            throw IllegalArgumentException()
        }
    }
}
