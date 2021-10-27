package com.kingsley.shimeji.mascot.animations

class WalkLeft : Walk() {
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        nextAnimationRequested = paramBoolean3
    }

    override val direction: Direction
        get() = Direction.LEFT
    override val nextAnimation: Animation
        get() {
            return if (random.nextBoolean()) {
                ClimbLeft()
            } else {
                WalkRight()
            }
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(0, -2, 0, 6))
        arrayList.add(Sprite(1, -2, 0, 6))
        arrayList.add(Sprite(0, -2, 0, 6))
        arrayList.add(Sprite(2, -2, 0, 6))
        return arrayList
    }
}
