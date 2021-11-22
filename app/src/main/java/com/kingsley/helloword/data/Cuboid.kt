package com.kingsley.helloword.data

import java.io.Serializable

class Cuboid(private val length: Float = 0f,private val  width: Float = 0f,private val  height: Float = 0f) : Shape(), Changeable<Shape?>, Serializable {

    override fun initPoints() {
        points = arrayOf()
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
        lines = arrayOf()
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

    init {
        initPoints()
        initLines()
    }
}