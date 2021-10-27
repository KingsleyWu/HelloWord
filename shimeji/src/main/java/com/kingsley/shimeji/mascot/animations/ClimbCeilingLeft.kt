package com.kingsley.shimeji.mascot.animations

class ClimbCeilingLeft : ClimbCeiling() {
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
                DescendLeft()
            } else {
                ClimbCeilingRight()
            }
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(24, 0, 0, 16))
        arrayList.add(Sprite(24, -1, 0, 4))
        arrayList.add(Sprite(22, -1, 0, 4))
        arrayList.add(Sprite(23, -1, 0, 4))
        arrayList.add(Sprite(23, 0, 0, 16))
        arrayList.add(Sprite(23, -2, 0, 4))
        arrayList.add(Sprite(22, -2, 0, 4))
        arrayList.add(Sprite(24, -2, 0, 4))
        return arrayList
    }
}