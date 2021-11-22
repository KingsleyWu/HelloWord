package com.kingsley.helloword.data

import kotlin.math.cos
import kotlin.math.sin

class Pyramid(private val num: Int = 0, private val length: Float = 0f, private val height: Float = 0f) : Shape(),
    Changeable<Shape?> {

    override fun initPoints() {
        points = arrayOf()
        val r = (length / (2 * sin(Math.PI / num))).toFloat() //半径
        val h = height / 2
        var degree = 0f //起始度数0
        points[0] = Point(0f, 0f, h)
        for (i in 1 until num + 1) {
            points[i] = Point(
                cos(degree.toDouble()).toFloat() * r, sin(degree.toDouble()).toFloat() * r, -h
            )
            degree += (2 * Math.PI / num).toFloat()
        }
    }

    override fun initLines() {
        lines = arrayOf()
        var i = 0
        while (i < num - 1) {
            lines[i] = Line(points[0], points[i + 1])
            lines[i + num] = Line(points[i + 1], points[i + 2])
            i++
        }
        lines[i] = Line(points[0], points[i + 1])
        lines[i + num] = Line(points[i + 1], points[1])
    }

    init {
        initPoints()
        initLines()
    }
}