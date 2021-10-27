package com.kingsley.shimeji.mascot.animations

class DescendLeft : Descend() {
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
}
