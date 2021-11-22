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
            val b = if (paidEnabled) {
                9
            } else {
                2
            }
            when (random.nextInt(b)) {
                7 -> {
                    return if (direction === Direction.LEFT) {
                        // 向左绊倒
                        TrippingLeft()
                    } else {
                        // 向右绊倒
                        TrippingRight()
                    }
                }
                // 坐起來抬頭看
                6 -> return SitAndLookUp(direction)
                // 双腿坐下
                5 -> return SitWithLegsDown(direction)
                // 坐和悬垂腿
                4 -> return SitAndDangleLegs(direction)
                // 眨眼
                3 -> return Wink(direction)
                // 爬行
                2 -> return Sprawl(direction)
                // 站立
                1 -> return Stand(direction)
                // 坐
                0 -> return Sit(direction)
                else -> if (direction === Direction.LEFT) {
                    // 左邊爬行
                    return CreepLeft()
                }
            }
            // 右邊爬行
            return CreepRight()
        }
}
