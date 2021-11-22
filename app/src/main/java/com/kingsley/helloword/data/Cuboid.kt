package com.kingsley.helloword.data

import com.kingsley.helloword.data.Changeable
import java.io.Serializable

class Cuboid(length: Float, width: Float, height: Float) : Shape(), Changeable<Shape?>, Serializable {
    private val length = 0f
    private val width = 0f
    private val height = 0f
    override fun initPoints() {
        points = arrayOfNulls(8)
        val lx = length / 2
        val ly = width / 2
        val lz = height / 2
        points[0] = Point(-lx, ly, lz)
        points[1] = Point(-lx, -ly, lz)
        points[2] = Point(lx, -ly, lz)
        points[3] = Point(lx, ly, lz)
        points[4] = Point(-lx, ly, -lz)
        points[5] = Point(lx, ly, -lz)
        points[6] = Point(lx, -ly, -lz)
        points[7] = Point(-lx, -ly, -lz)
    }

    override fun initLines() {
        lines = arrayOfNulls(12)
        lines[0] = Line(points[0], points[1])
        lines[1] = Line(points[1], points[2])
        lines[2] = Line(points[2], points[3])
        lines[3] = Line(points[0], points[3])
        lines[4] = Line(points[0], points[4])
        lines[5] = Line(points[1], points[7])
        lines[6] = Line(points[2], points[6])
        lines[7] = Line(points[3], points[5])
        lines[8] = Line(points[4], points[7])
        lines[9] = Line(points[7], points[6])
        lines[10] = Line(points[6], points[5])
        lines[11] = Line(points[5], points[4])
    }

    /**
     * 长方形的长宽高
     *
     * @param length 长
     * @param width  宽
     * @param height 高
     */
    init {
        this.width = width
        this.height = height
        this.length = length
        initPoints()
        initLines()
    }
}