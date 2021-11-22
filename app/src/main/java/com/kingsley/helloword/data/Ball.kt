package com.kingsley.helloword.data

import java.util.ArrayList

class Ball {
    /**
     * 球体点集合
     */
    private val points: MutableList<Point> = ArrayList()

    /**
     * 球体的半径
     */
    private var radius: Float

    /**
     * 构成球体的点数量
     * 默认：100
     * 最大值：625
     */
    private var pointsQuantity = 100

    constructor(radius: Float) {
        this.radius = radius
        buildBall()
    }

    constructor(radius: Float, pointsQuantity: Int) {
        var pointsQuantity = pointsQuantity
        this.radius = radius
        if (pointsQuantity > 625) pointsQuantity = 625
        this.pointsQuantity = pointsQuantity
        buildBall()
    }

    private fun buildBall() {
        val maxNum = maxIntSqrt(pointsQuantity) //一个平面最大点数
        val xyAddDegree = (2 * Math.PI / maxNum).toFloat() //横向每次要增加的角度
        val yzAddDegree = (Math.PI / (maxNum + 1)).toFloat() //纵向每次要增加的角度
        var yzDegree = 0f //纵向初始角度
        for (i in 0 until maxNum) {
            yzDegree += yzAddDegree
            val z = (Math.cos(yzDegree.toDouble()) * radius).toFloat()
            val curRadius = (Math.sin(yzDegree.toDouble()) * radius).toFloat() //小半径
            var xyDegree = 0f //横向初始角度
            for (j in 0 until maxNum) {
                xyDegree += xyAddDegree
                val x = Math.cos(xyDegree.toDouble()).toFloat() * curRadius
                val y = Math.sin(xyDegree.toDouble()).toFloat() * curRadius
                points.add(Point(x, y, z))
            }
        }
        points.add(Point(0, 0, radius)) //两极
        points.add(Point(0, 0, -radius))
    }

    /**
     * 开根取最大整数
     *
     * @param x 被开根数
     * @return 开根最大整数
     */
    private fun maxIntSqrt(x: Int): Int {
        var left = 1
        var right = x
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (mid == x / mid) {
                return mid
            } else if (mid < x / mid) {
                left = mid + 1
            } else {
                right = mid - 1
            }
        }
        return right
    }

    fun getPoints(): List<Point> {
        return points
    }

    fun getRadius(): Float {
        return radius
    }

    fun setRadius(radius: Float) {
        this.radius = radius
        buildBall()
    }

    fun getPointsQuantity(): Int {
        return pointsQuantity
    }

    fun setPointsQuantity(pointsQuantity: Int) {
        var pointsQuantity = pointsQuantity
        if (pointsQuantity > 625) pointsQuantity = 625
        this.pointsQuantity = pointsQuantity
        buildBall()
    }
}