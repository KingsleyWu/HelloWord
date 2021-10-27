package com.kingsley.shimeji.mascot.animations

abstract class Descend : Animation() {
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        nextAnimationRequested = paramBoolean2
    }

    override val isOneShot: Boolean
        get() = false

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(13, 0, 0, 16))
        arrayList.add(Sprite(13, 0, 2, 4))
        arrayList.add(Sprite(11, 0, 2, 4))
        arrayList.add(Sprite(12, 0, 2, 4))
        arrayList.add(Sprite(12, 0, 0, 16))
        arrayList.add(Sprite(12, 0, 1, 4))
        arrayList.add(Sprite(11, 0, 1, 4))
        arrayList.add(Sprite(13, 0, 1, 4))
        return arrayList
    }
}
