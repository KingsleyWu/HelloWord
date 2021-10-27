package com.kingsley.shimeji.mascot.animations

class ClimbRight : Climb() {
    override val direction: Direction
        get() = Direction.RIGHT
    override val nextAnimation: Animation
        get() = ClimbCeilingLeft()
    override val optionalAnimation: Animation
        get() {
            return if (random.nextInt(100) < 70) {
                JumpLeft()
            } else {
                Falling(direction)
            }
        }
}
