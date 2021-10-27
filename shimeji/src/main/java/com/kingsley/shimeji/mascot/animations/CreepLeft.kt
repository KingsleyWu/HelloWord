package com.kingsley.shimeji.mascot.animations

internal class CreepLeft : Creep() {
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
        get() = ClimbLeft()

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(19, 0, 0, 28))
        arrayList.add(Sprite(19, -2, 0, 4))
        arrayList.add(Sprite(20, -2, 0, 4))
        arrayList.add(Sprite(20, -1, 0, 4))
        arrayList.add(Sprite(20, 0, 0, 24))
        return arrayList
    }
}