package com.kingsley.shimeji.mascot

enum class SpriteUtil(sprite: String, index: Int, var values: IntArray) {

    WALK("WALK", 0, intArrayOf(0, 1, 0, 2)),
    DRAGGING("DRAGGING", 1, intArrayOf(4, 5, 6, 7)),
    JUMP("JUMP", 2, intArrayOf(21)),
    FALLING("FALLING", 3, intArrayOf(3)),
    CLIMB_WALL("CLIMB_WALL", 4, intArrayOf(11, 12, 13)),
    CLIMB_CEILING("CLIMB_CEILING", 5, intArrayOf(22, 23, 24)),
    BOUNCE("BOUNCE", 6, intArrayOf(17, 18)),
    WINK("WINK", 7, intArrayOf(14, 16)),
    SIT("SIT", 8, intArrayOf(10)),
    SIT_LOOK_UP("SIT_LOOK_UP", 9, intArrayOf(30, 31)),
    SPRAWL("SPRAWL", 10, intArrayOf(20)),
    CREEP("CREEP", 11, intArrayOf(19, 20)),
    SIT_DANGLE_LEGS("SIT_DANGLE_LEGS", 12, intArrayOf(25)),
    TRIP("TRIP", 13, intArrayOf(17, 18, 19));

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