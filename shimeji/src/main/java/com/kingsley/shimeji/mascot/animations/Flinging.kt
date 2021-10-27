package com.kingsley.shimeji.mascot.animations

class Flinging(paramInt: Int) : Animation() {
    override val direction: Direction
    private var grabBottom = false
    private var grabLeft = false
    private var grabRight = false
    private var grabTop = false
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        var paramBoolean1 = paramBoolean1
        grabTop = paramBoolean1
        grabBottom = paramBoolean2
        grabLeft = paramBoolean3
        grabRight = paramBoolean4
        paramBoolean1 = paramBoolean1 || paramBoolean2 || paramBoolean3 || paramBoolean4
        nextAnimationRequested = paramBoolean1
    }

    fun drop(): Animation {
        return nextAnimation
    }

    override val isOneShot: Boolean
        get() = false
    override val nextAnimation: Animation
        get() {
            if (grabLeft) return ClimbLeft()
            if (grabRight) return ClimbRight()
            if (grabTop) {
                return if (direction === Direction.LEFT) {
                    ClimbCeilingLeft()
                } else {
                    ClimbCeilingRight()
                }
            }
            return (if (grabBottom) Bounce(direction) else Falling(direction))
        }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(7, 0, 0, 250))
        return arrayList
    }

    init {
        val direction: Direction = if (paramInt < 0) {
            Direction.LEFT
        } else {
            Direction.RIGHT
        }
        this.direction = direction
    }
}
