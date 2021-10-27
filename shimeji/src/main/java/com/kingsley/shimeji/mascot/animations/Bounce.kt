package com.kingsley.shimeji.mascot.animations

internal class Bounce(paramDirection: Direction) :
    Animation() {
    override var direction = Direction.RIGHT

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
            return if (random.nextBoolean()) {
                WalkLeft()
            } else {
                WalkRight()
            }
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(17, 0, 0, 8))
        arrayList.add(Sprite(18, 0, 0, 8))
        return arrayList
    }

    init {
        direction = paramDirection
    }
}