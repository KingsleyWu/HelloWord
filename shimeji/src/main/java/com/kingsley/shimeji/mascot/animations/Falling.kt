package com.kingsley.shimeji.mascot.animations

class Falling : Animation {
    override var direction: Direction
        private set

    constructor() {
        val direction: Direction
        this.direction = Direction.RIGHT
        direction = if (random.nextBoolean()) {
            Direction.LEFT
        } else {
            Direction.RIGHT
        }
        this.direction = direction
    }

    internal constructor(paramDirection: Direction) {
        direction = Direction.RIGHT
        direction = paramDirection
    }

    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        nextAnimationRequested = paramBoolean2
    }

    override val isOneShot: Boolean
        get() = false
    override val nextAnimation: Animation
        get() = Bounce(direction)

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(3, 0, 15, 250))
        return arrayList
    }
}
