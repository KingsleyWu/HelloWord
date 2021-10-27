package com.kingsley.shimeji.mascot.animations


abstract class ClimbCeiling : Animation() {
    override val isOneShot: Boolean
        get() = false

    override fun getMaxDuration(): Int {
        return random.nextInt(170) + 30
    }

    override val optionalAnimation: Animation
        get() = Falling(direction)
}