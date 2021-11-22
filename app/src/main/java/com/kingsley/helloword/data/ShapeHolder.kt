package com.kingsley.helloword.data

import com.kingsley.helloword.data.Shape.moveDirection
import com.kingsley.helloword.data.Shape.setSize
import com.kingsley.helloword.data.Shape.deflectDegree
import com.kingsley.helloword.data.Changeable
import com.kingsley.helloword.data.ShapeHolder
import java.io.Serializable
import java.util.ArrayList

class ShapeHolder : Changeable<ShapeHolder?>, Serializable {
    private val holderList: MutableList<Shape> = ArrayList()

    /**
     * 增加一个模型
     *
     * @param shape 增加的模型
     */
    fun add(shape: Shape) {
        holderList.add(shape)
    }

    /**
     * 获取容器中的模型列表
     *
     * @return 模型列表
     */
    fun getHolderList(): List<Shape> {
        return holderList
    }

    override fun moveDirection(x: Float, y: Float, z: Float): ShapeHolder {
        for (shape in holderList) {
            shape.moveDirection(x, y, z)
        }
        return this
    }

    override fun setSize(multiple: Float): ShapeHolder {
        for (shape in holderList) {
            shape.setSize(multiple)
        }
        return this
    }

    override fun deflectDegree(xy: Float, yz: Float): ShapeHolder {
        for (shape in holderList) {
            shape.deflectDegree(xy, yz)
        }
        return this
    }
}