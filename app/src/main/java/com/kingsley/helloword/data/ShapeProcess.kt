package com.kingsley.helloword.data

import com.kingsley.helloword.data.ShapeHolder.getHolderList
import com.kingsley.helloword.data.Shape.points
import com.kingsley.helloword.data.Point.updateTopDegree
import com.kingsley.helloword.data.Point.updateLeftDegree
import com.kingsley.helloword.data.ShapeHolder
import com.kingsley.helloword.data.ShapeProcess

class ShapeProcess {
    /**
     * 设置旋转速度
     *
     * @param rotateSpend 旋转速度
     */
    var rotateSpend = 0
    private var holder: ShapeHolder? = null

    constructor() {}
    constructor(holder: ShapeHolder?) {
        rotateSpend = ROTATESPEND_DEFAULT
        this.holder = holder
    }

    /**
     * 根据触屏横向以及纵向移动距离计算并返回旋转后的结果
     *
     * @param horizontal  横向移动距离
     * @param vertical    纵向移动距离
     * @param rotateSpend 旋转速度
     * @return 旋转完毕后的容器
     */
    fun rotate(horizontal: Float, vertical: Float): ShapeHolder? {
        val shapes = holder!!.getHolderList()
        //俯视图旋转角度
        val topRotateDegree = -horizontal / rotateSpend
        //左视图旋转角度
        val leftRotateDegree = -vertical / rotateSpend
        for (shape in shapes) {
            for (point in shape.points) {
                topRotate(point, topRotateDegree)
                leftRotate(point, leftRotateDegree)
            }
        }
        return holder
    }

    /**
     * 对纵向旋转处理更新
     *
     * @param point            旋转处理的点
     * @param leftRotateDegree 左视旋转角度
     */
    fun leftRotate(point: Point, leftRotateDegree: Float) {
        point.leftDegree += leftRotateDegree //增加左视角度
        val leftRadius = Math.sqrt((point.radius * point.radius - point.x * point.x).toDouble())
            .toFloat()
        point.y = (Math.cos(point.leftDegree.toDouble()) * leftRadius).toFloat()
        point.z = (Math.sin(point.leftDegree.toDouble()) * leftRadius).toFloat()
        point.updateTopDegree() //左角度的改变会影响到俯视角度
    }

    /**
     * 对横向旋转处理更新
     *
     * @param point           旋转处理的点
     * @param topRotateDegree 俯视旋转角度
     */
    fun topRotate(point: Point, topRotateDegree: Float) {
        point.topDegree += topRotateDegree //增加俯视旋转角度
        val topRadius = Math.sqrt((point.radius * point.radius - point.z * point.z).toDouble())
            .toFloat()
        point.x = Math.cos(point.topDegree.toDouble()).toFloat() * topRadius
        point.y = Math.sin(point.topDegree.toDouble()).toFloat() * topRadius
        point.updateLeftDegree() //俯视角度的改变会影响到左视角度
    }

    fun setHolder(holder: ShapeHolder?) {
        this.holder = holder
    }

    companion object {
        const val ROTATESPEND_SLOW = 200
        const val ROTATESPEND_DEFAULT = 100
        const val ROTATESPEND_NORMAL = 50
        const val ROTATESPEND_FAST = 10
    }
}