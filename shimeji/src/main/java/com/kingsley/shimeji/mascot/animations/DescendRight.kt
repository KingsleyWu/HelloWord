package com.kingsley.shimeji.mascot.animations

class DescendRight : Descend() {
    override val direction: Direction
        get() = Direction.RIGHT
    override val nextAnimation: Animation
        get() {
            return if (random.nextBoolean()) {
                ClimbRight()
            } else {
                WalkLeft()
            }
        }
}