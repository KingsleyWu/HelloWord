package com.kingsley.shimeji.mascot.animations

class JumpRight : Jump() {
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
        get() = ClimbRight()

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(21, 10, -1, 2))
        return arrayList
    }
}
