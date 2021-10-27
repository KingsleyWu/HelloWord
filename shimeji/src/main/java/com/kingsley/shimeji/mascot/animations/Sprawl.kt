package com.kingsley.shimeji.mascot.animations

internal class Sprawl(override val direction: Direction) :
    Animation() {

    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
    }

    override val isOneShot: Boolean
        get() = true
    override val nextAnimation: Animation
        get() {
            return if (direction === Direction.LEFT) {
                WalkLeft()
            } else {
                WalkRight()
            }
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(20, 0, 0, 250))
        return arrayList
    }
}
