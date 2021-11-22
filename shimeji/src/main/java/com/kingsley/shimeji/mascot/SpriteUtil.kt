package com.kingsley.shimeji.mascot

enum class SpriteUtil(sprite: String, var values: IntArray) {

    WALK("WALK", intArrayOf(0, 1, 0, 2)),
    DRAGGING("DRAGGING", intArrayOf(4, 5, 6, 7)),
    JUMP("JUMP", intArrayOf(21)),
    FALLING("FALLING", intArrayOf(3)),
    CLIMB_WALL("CLIMB_WALL", intArrayOf(11, 12, 13)),
    CLIMB_CEILING("CLIMB_CEILING", intArrayOf(22, 23, 24)),
    BOUNCE("BOUNCE", intArrayOf(17, 18)),
    WINK("WINK", intArrayOf(14, 16)),
    SIT("SIT", intArrayOf(10)),
    SIT_LOOK_UP("SIT_LOOK_UP", intArrayOf(30, 31)),
    SPRAWL("SPRAWL", intArrayOf(20)),
    CREEP("CREEP", intArrayOf(19, 20)),
    SIT_DANGLE_LEGS("SIT_DANGLE_LEGS", intArrayOf(25)),
    TRIP("TRIP", intArrayOf(17, 18, 19));

    companion object {

        fun getUsedSprites(): HashSet<Int> {
            val hashSet: HashSet<Int> = HashSet()
            val values = values()
            for (b in 0..13) {
                val arrayOfInt = values[b].values
                val i = arrayOfInt.size
                for (b1 in 0 until i) {
                    hashSet.add(arrayOfInt[b1])
                }
            }
            return hashSet
        }
    }

}