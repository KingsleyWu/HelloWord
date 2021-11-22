package com.kingsley.helloword.data

object ShapeDB {
    /**
     * 餐桌
     *
     * @param holder
     */
    @JvmStatic
    fun addChairsAndDesk(holder: ShapeHolder) {
        //桌子
        val top: Shape = Prism(6, 300f, 20f)
        top.moveDirection(0f, 0f, 200f)
        val bottom: Shape = Prism(4, 150f, 30f)
        bottom.moveDirection(0f, 0f, -200f)
        holder.add(Prism(4, 20f, 400f))
        holder.add(top)
        holder.add(bottom)
        //椅子
        val h = 200
        val h2 = h / 2
        val moveY = -450
        for (i in 0..5) {
            val degree = (Math.PI / 3 * i).toFloat()
            val foot1: Shape = Prism(4, 20f, h.toFloat())
            foot1.moveDirection(-h2.toFloat(), (-h2 + moveY).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot2: Shape = Prism(4, 20f, h.toFloat())
            foot2.moveDirection(h2.toFloat(), (-h2 + moveY).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot3: Shape = Prism(4, 20f, h.toFloat())
            foot3.moveDirection(-h2.toFloat(), (h2 + moveY).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val foot4: Shape = Prism(4, 20f, h.toFloat())
            foot4.moveDirection(h2.toFloat(), (h2 + moveY).toFloat(), -h2.toFloat()).deflectDegree(degree, 0f)
            val sit: Shape = Prism(4, (h + 20).toFloat(), 30f)
            sit.deflectDegree(Math.PI.toFloat() / 4, 0f).moveDirection(0f, moveY.toFloat(), 0f)
                .deflectDegree(degree, 0f)
            val back: Shape = Cuboid((h + 20).toFloat(), 30f, (h + 20).toFloat())
            back.moveDirection(0f, (-h2 + moveY).toFloat(), h2.toFloat()).deflectDegree(degree, 0f)
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
        shapeHolder.add(Prism(10, 100f, 100f))
        val second: Shape = Prism(10, 80f, 100f)
        second.moveDirection(0f, 0f, 100f)
        shapeHolder.add(second)
        val first: Shape = Prism(10, 50f, 100f)
        first.moveDirection(0f, 0f, 200f)
        shapeHolder.add(first)
        val candle: Shape = Cuboid(20f, 20f, 60f)
        candle.moveDirection(0f, 0f, 280f)
        shapeHolder.add(candle)
        val fire: Shape = Pyramid(4, 15f, 40f)
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
        shapeHolder.add(Cuboid(100f, 500f, 10f))
        val cuboid: Shape = Cuboid(100f, 100f, 10f)
        cuboid.moveDirection(100f, 200f, 0f)
        shapeHolder.add(cuboid)
        val cuboid2: Shape = Cuboid(100f, 200f, 10f)
        cuboid2.moveDirection(200f, 150f, 0f)
        shapeHolder.add(cuboid2)
        shapeHolder.setSize(2.5.toFloat())
    }

    /**
     * 蛋糕
     *
     * @param shapeHolder
     */
    @JvmStatic
    fun add2(shapeHolder: ShapeHolder) {
        val a: Shape = Prism(4, 2f, 500f)
        a.moveDirection((150 - 2).toFloat(), 0f, 0f)
        shapeHolder.add(a)
        val b: Shape = Prism(4, 2f, 400f)
        b.moveDirection((50 - 2).toFloat(), 0f, 50f)
        shapeHolder.add(b)
        val e: Shape = Prism(4, 2f, 500f)
        e.deflectDegree(90f, 90f)
        shapeHolder.add(e)
        val c: Shape = Prism(4, 2f, 100f)
        c.moveDirection((-50 - 2).toFloat(), 0f, -100f)
        shapeHolder.add(c)
        val d: Shape = Prism(4, 2f, 200f)
        d.moveDirection((-150 - 2).toFloat(), 0f, -150f)
        shapeHolder.add(d)
        shapeHolder.setSize(2.5.toFloat())
    }
}