package com.kingsley.shimeji.mascot.animations

class ClimbLeft : Climb() {
    override val direction: Direction
        get() = Direction.LEFT
    override val nextAnimation: Animation
        get() = ClimbCeilingRight()
    override val optionalAnimation: Animation
        get() {
            return if (random.nextInt(100) < 70) {
                JumpRight()
            } else {
                Falling(direction)
            }
        }
}
