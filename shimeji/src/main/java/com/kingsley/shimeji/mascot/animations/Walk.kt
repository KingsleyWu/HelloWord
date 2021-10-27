package com.kingsley.shimeji.mascot.animations

abstract class Walk : Animation() {
    override val isOneShot: Boolean
        get() = false

    override fun getMaxDuration(): Int {
        if (paidEnabled) {
            val j = random.nextInt(60)
            return j + 10
        }
        val i = random.nextInt(150)
        return i + 10
    }

    override val optionalAnimation: Animation
        get() {
            val b: Byte = if (paidEnabled) {
                9
            } else {
                2
            }
            when (random.nextInt(b.toInt())) {
                7 -> {
                    return if (direction === Direction.LEFT) {
                        TrippingLeft()
                    } else {
                        TrippingRight()
                    }
                }
                6 -> return SitAndLookUp(direction)
                5 -> return SitWithLegsDown(direction)
                4 -> return SitAndDangleLegs(direction)
                3 -> return Wink(direction)
                2 -> return Sprawl(direction)
                1 -> return Stand(direction)
                0 -> return Sit(direction)
                else -> if (direction === Direction.LEFT) return CreepLeft()
            }
            return CreepRight()
        }
}
