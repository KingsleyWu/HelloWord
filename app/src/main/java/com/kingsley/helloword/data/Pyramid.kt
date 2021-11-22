package com.kingsley.helloword.data

import com.kingsley.helloword.data.Changeable

class Pyramid(num: Int, length: Float, height: Float) : Shape(), Changeable<Shape?> {
    private val num = 0
    private val length = 0f
    private val height = 0f
    override fun initPoints() {
        points = arrayOfNulls(num + 1)
        val r = (length / (2 * Math.sin(Math.PI / num))).toFloat() //半径
        val h = height / 2
        var degree = 0f //起始度数0
        points[0] = Point(0, 0, h)
        for (i in 1 until num + 1) {
            points[i] = Point(
                Math.cos(degree.toDouble()).toFloat() * r, Math.sin(degree.toDouble()).toFloat() * r, -h
            )
            degree += (2 * Math.PI / num).toFloat()
        }
    }

    override fun initLines() {
        lines = arrayOfNulls(2 * num)
        var i = 0
        i = 0
        while (i < num - 1) {
            lines[i] = Line(points[0], points[i + 1])
            lines[i + num] = Line(points[i + 1], points[i + 2])
            i++
        }
        lines[i] = Line(points[0], points[i + 1])
        lines[i + num] = Line(points[i + 1], points[1])
    }

    init {
        this.num = num
        this.length = length
        this.height = height
        initPoints()
        initLines()
    }
}