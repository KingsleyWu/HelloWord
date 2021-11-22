package com.kingsley.helloword.data

import java.io.Serializable

class Point : Serializable {
    var x = 0f
    var y = 0f
    var z = 0f
    var topDegree = 0f
    var leftDegree = 0f
    var radius = 0f

    constructor() {}
    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
        updateAll()
    }

    /**
     * 更新所有数据：俯视角度、左视角度、旋转半径
     */
    fun updateAll() {
        updateTopDegree()
        updateLeftDegree()
        updateRadius()
    }

    /**
     * 更新点坐标与原点的距离，即旋转半径
     */
    fun updateRadius() {
        radius = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }

    /**
     * 更新俯视图点坐标的角度
     */
    fun updateTopDegree() {
        topDegree = Math.atan2(y.toDouble(), x.toDouble()).toFloat()
    }

    /**
     * 更新左视图点坐标的角度
     */
    fun updateLeftDegree() {
        leftDegree = Math.atan2(z.toDouble(), y.toDouble()).toFloat()
    }
}