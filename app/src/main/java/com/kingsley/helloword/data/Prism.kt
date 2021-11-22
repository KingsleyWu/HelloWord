package com.kingsley.helloword.data

import com.kingsley.helloword.data.Changeable

class Prism(num: Int, length: Float, height: Float) : Shape(), Changeable<Shape?> {
    private val num = 0
    private val length = 0f
    private val height = 0f
    override fun initPoints() {
        points = arrayOfNulls(2 * num)
        val r = (length / (2 * Math.sin(Math.PI / num))).toFloat() //半径
        val h = height / 2
        var degree = 0f //起始度数0
        for (i in 0 until num) {
            //上层形状赋值
            points[i] = Point(
                Math.cos(degree.toDouble()).toFloat() * r,
                Math.sin(degree.toDouble()).toFloat() * r, h
            )
            //下层形状赋值
            points[i + num] = Point(
                Math.cos(degree.toDouble()).toFloat() * r,
                Math.sin(degree.toDouble()).toFloat() * r, -h
            )
            degree += (2 * Math.PI / num).toFloat()
        }
    }

    override fun initLines() {
        lines = arrayOfNulls(3 * num)
        var i = 0
        i = 0
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

    /**
     * 创建一个边长为length，高为height的正num棱柱
     *
     * @param num    棱数
     * @param length 边长
     * @param height 高度，即棱长
     */
    init {
        this.num = num
        this.length = length
        this.height = height
        initPoints()
        initLines()
    }
}