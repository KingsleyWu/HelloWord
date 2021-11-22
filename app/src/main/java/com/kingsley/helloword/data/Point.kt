package com.kingsley.helloword.data

import java.io.Serializable
import kotlin.math.atan2
import kotlin.math.sqrt

class Point : Serializable {
    @JvmField
    var x = 0f
    @JvmField
    var y = 0f
    @JvmField
    var z = 0f
    @JvmField
    var topDegree = 0f
    @JvmField
    var leftDegree = 0f
    @JvmField
    var radius = 0f

    constructor()

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
        radius = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
    }

    /**
     * 更新俯视图点坐标的角度
     */
    fun updateTopDegree() {
        topDegree = atan2(y.toDouble(), x.toDouble()).toFloat()
    }

    /**
     * 更新左视图点坐标的角度
     */
    fun updateLeftDegree() {
        leftDegree = atan2(z.toDouble(), y.toDouble()).toFloat()
    }
}