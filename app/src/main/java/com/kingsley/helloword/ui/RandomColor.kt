package com.kingsley.helloword.ui

import java.util.*

/**
 * @author Kingsley
 * Created on 2021/5/19.
 */
object RandomColor {
    var rand = Random()

    fun color() : Int {
        // Will produce only bright / light colours:
        val r = (rand.nextFloat() / 2f + 0.5).toFloat()
        val g = (rand.nextFloat() / 2f + 0.5).toFloat()
        val b = (rand.nextFloat() / 2f + 0.5).toFloat()
        return rgb(r, g, b)
    }

    fun rgb(red: Float, green: Float, blue: Float) : Int{
        return -0x1000000 or
                ((red * 255.0f + 0.5f).toInt() shl 16) or
                ((green * 255.0f + 0.5f).toInt() shl 8) or
                (blue * 255.0f + 0.5f).toInt()
    }
}