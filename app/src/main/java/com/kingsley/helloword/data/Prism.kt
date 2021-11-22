package com.kingsley.helloword.data

import kotlin.math.cos
import kotlin.math.sin

/**
 * 创建一个边长为length，高为height的正num棱柱
 *
 * @param num    棱数
 * @param length 边长
 * @param height 高度，即棱长
 */
class Prism(private val num: Int = 0,private val  length: Float = 0f,private val  height: Float = 0f) : Shape(), Changeable<Shape?> {
    override fun initPoints() {
        points = arrayOf()
        val r = (length / (2 * sin(Math.PI / num))).toFloat() //半径
        val h = height / 2
        var degree = 0f //起始度数0
        for (i in 0 until num) {
            //上层形状赋值
            points[i] = Point(
                cos(degree.toDouble()).toFloat() * r,
                sin(degree.toDouble()).toFloat() * r, h
            )
            //下层形状赋值
            points[i + num] = Point(
                cos(degree.toDouble()).toFloat() * r,
                sin(degree.toDouble()).toFloat() * r, -h
            )
            degree += (2 * Math.PI / num).toFloat()
        }
    }

    override fun initLines() {
        lines = arrayOf()
        var i = 0
        while (i < num - 1) {
            //上层当前点和下一点组成线段
            lines[i * 3] = Line(points[i], points[i + 1])
            //上下层点连接组成线段
            lines[i * 3 + 1] = Line(points[i + num], points[i + num + 1])
            //下层当前点和下一点组成线段
            lines[i * 3 + 2] = Line(points[i], points[i + num])
            i++
        }
        lines[i * 3] = Line(points[i], points[0])
        lines[i * 3 + 1] = Line(points[i + num], points[num])
        lines[i * 3 + 2] = Line(points[i], points[i + num])
    }

    init {
        initPoints()
        initLines()
    }
}