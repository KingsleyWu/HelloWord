package com.kingsley.helloword.data

import com.kingsley.helloword.data.Point.updateAll
import com.kingsley.helloword.data.Point.updateRadius
import com.kingsley.helloword.data.Changeable
import com.kingsley.helloword.data.ShapeProcess
import java.io.Serializable

abstract class Shape : Changeable<Shape?>, Serializable {
    var points: Array<Point>
        protected set
    var lines: Array<Line>
        protected set

    protected abstract fun initPoints()
    protected abstract fun initLines()
    override fun moveDirection(x: Float, y: Float, z: Float): Shape {
        for (point in points) {
            point.x += x
            point.y += y
            point.z += z
            point.updateAll() //位移需要改变所有数据
        }
        return this
    }

    override fun setSize(multiple: Float): Shape {
        for (point in points) {
            point.x *= multiple
            point.y *= multiple
            point.z *= multiple
            point.updateRadius() //缩放不需要改变角度
        }
        return this
    }

    override fun deflectDegree(xy: Float, yz: Float): Shape {
        val process = ShapeProcess()
        for (point in points) {
            process.topRotate(point, xy)
            process.leftRotate(point, yz)
        }
        return this
    }
}