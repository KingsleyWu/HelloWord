package com.kingsley.helloword.data

import com.kingsley.helloword.data.Shape.moveDirection
import com.kingsley.helloword.data.Shape.deflectDegree
import com.kingsley.helloword.data.ShapeHolder
import com.kingsley.helloword.data.Prism
import com.kingsley.helloword.data.Cuboid
import com.kingsley.helloword.data.Pyramid

object ShapeDB {
    /**
     * 餐桌
     *
     * @param holder
     */
    fun addChairsAndDesk(holder: ShapeHolder) {
        //桌子
        val top: Shape = Prism(6, 300, 20)
        top.moveDirection(0f, 0f, 200f)
        val bottom: Shape = Prism(4, 150, 30)
        bottom.moveDirection(0f, 0f, -200f)
        holder.add(Prism(4, 20, 400))
        holder.add(top)
        holder.add(bottom)
        //椅子
        val h = 200
        val h2 = h / 2
        val movey = -450
        for (i in 0..5) {
            val degree = (Math.PI / 3 * i).toFloat()
            val foot1: Shape = Prism(4, 20, h.toFloat())
            foot1.moveDirection(-h2.toFloat(), (-h2 + movey).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot2: Shape = Prism(4, 20, h.toFloat())
            foot2.moveDirection(h2.toFloat(), (-h2 + movey).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot3: Shape = Prism(4, 20, h.toFloat())
            foot3.moveDirection(-h2.toFloat(), (h2 + movey).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot4: Shape = Prism(4, 20, h.toFloat())
            foot4.moveDirection(h2.toFloat(), (h2 + movey).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val sit: Shape = Prism(4, (h + 20).toFloat(), 30)
            sit.deflectDegree(Math.PI.toFloat() / 4, 0f).moveDirection(0f, movey.toFloat(), 0f)
                .deflectDegree(degree, 0f)
            val back: Shape = Cuboid((h + 20).toFloat(), 30, (h + 20).toFloat())
            back.moveDirection(0f, (-h2 + movey).toFloat(), h2.toFloat()).deflectDegree(degree, 0f)
            holder.add(foot1)
            holder.add(foot2)
            holder.add(foot3)
            holder.add(foot4)
            holder.add(sit)
            holder.add(back)
        }
    }

    /**
     * 蛋糕
     *
     * @param shapeHolder
     */
    fun addCake(shapeHolder: ShapeHolder) {
        shapeHolder.add(Prism(10, 100, 100))
        val second: Shape = Prism(10, 80, 100)
        second.moveDirection(0f, 0f, 100f)
        shapeHolder.add(second)
        val first: Shape = Prism(10, 50, 100)
        first.moveDirection(0f, 0f, 200f)
        shapeHolder.add(first)
        val candle: Shape = Cuboid(20, 20, 60)
        candle.moveDirection(0f, 0f, 280f)
        shapeHolder.add(candle)
        val fire: Shape = Pyramid(4, 15, 40)
        fire.moveDirection(0f, 0f, 340f)
        shapeHolder.add(fire)
        shapeHolder.setSize(2.5.toFloat())
    }

    /**
     * 蛋糕
     *
     * @param shapeHolder
     */
    fun addSquare(shapeHolder: ShapeHolder) {
        shapeHolder.add(Cuboid(100, 500, 10))
        val cuboid: Shape = Cuboid(100, 100, 10)
        cuboid.moveDirection(100f, 200f, 0f)
        shapeHolder.add(cuboid)
        val cuboid2: Shape = Cuboid(100, 200, 10)
        cuboid2.moveDirection(200f, 150f, 0f)
        shapeHolder.add(cuboid2)
        shapeHolder.setSize(2.5.toFloat())
    }

    /**
     * 蛋糕
     *
     * @param shapeHolder
     */
    fun add2(shapeHolder: ShapeHolder) {
        val a: Shape = Prism(4, 2, 500)
        a.moveDirection((150 - 2).toFloat(), 0f, 0f)
        shapeHolder.add(a)
        val b: Shape = Prism(4, 2, 400)
        b.moveDirection((50 - 2).toFloat(), 0f, 50f)
        shapeHolder.add(b)
        val e: Shape = Prism(4, 2, 500)
        e.deflectDegree(90f, 90f)
        shapeHolder.add(e)
        val c: Shape = Prism(4, 2, 100)
        c.moveDirection((-50 - 2).toFloat(), 0f, -100f)
        shapeHolder.add(c)
        val d: Shape = Prism(4, 2, 200)
        d.moveDirection((-150 - 2).toFloat(), 0f, -150f)
        shapeHolder.add(d)
        shapeHolder.setSize(2.5.toFloat())
    }
}