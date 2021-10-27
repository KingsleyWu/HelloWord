package com.kingsley.shimeji.mascot.animations

class WalkRight : Walk() {
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        nextAnimationRequested = paramBoolean4
    }

    override val direction: Direction
        get() = Direction.RIGHT
    override val nextAnimation: Animation
        get() {
            return if (random.nextBoolean()) {
                ClimbRight()
            } else {
                WalkLeft()
            }
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(0, 2, 0, 6))
        arrayList.add(Sprite(1, 2, 0, 6))
        arrayList.add(Sprite(0, 2, 0, 6))
        arrayList.add(Sprite(2, 2, 0, 6))
        return arrayList
    }
}
