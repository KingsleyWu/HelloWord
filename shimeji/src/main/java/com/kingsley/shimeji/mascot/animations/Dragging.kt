package com.kingsley.shimeji.mascot.animations

class Dragging : Animation() {
    override val direction: Direction

    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
    }

    fun drop(): Animation {
        return nextAnimation
    }

    override val isOneShot: Boolean
        get() = false
    override val nextAnimation: Animation
        get() = Falling(direction)

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(6, 0, 0, 8))
        arrayList.add(Sprite(4, 0, 0, 8))
        arrayList.add(Sprite(5, 0, 0, 8))
        arrayList.add(Sprite(7, 0, 0, 8))
        arrayList.add(Sprite(5, 0, 0, 8))
        arrayList.add(Sprite(4, 0, 0, 8))
        return arrayList
    }

    init {
        val direction: Direction = if (random.nextBoolean()) {
            Direction.LEFT
        } else {
            Direction.RIGHT
        }
        this.direction = direction
    }
}