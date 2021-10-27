package com.kingsley.shimeji.mascot.animations

internal class Wink(override val direction: Direction) :
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
        arrayList.add(Sprite(14, 0, 0, 30))
        arrayList.add(Sprite(16, 0, 0, 5))
        arrayList.add(Sprite(14, 0, 0, 30))
        return arrayList
    }
}
