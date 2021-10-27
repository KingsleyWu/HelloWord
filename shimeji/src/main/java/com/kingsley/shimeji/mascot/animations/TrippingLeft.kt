package com.kingsley.shimeji.mascot.animations

internal class TrippingLeft : Tripping() {
    override val direction: Direction
        get() = Direction.LEFT
    override val nextAnimation: Animation
        get() = WalkLeft()

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(18, -8, 0, 8))
        arrayList.add(Sprite(17, -4, 0, 4))
        arrayList.add(Sprite(19, -2, 0, 4))
        arrayList.add(Sprite(19, 0, 0, 10))
        arrayList.add(Sprite(18, -4, 0, 4))
        return arrayList
    }
}
