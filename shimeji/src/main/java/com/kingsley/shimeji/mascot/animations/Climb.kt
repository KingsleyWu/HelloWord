package com.kingsley.shimeji.mascot.animations

abstract class Climb : Animation() {
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
        nextAnimationRequested = paramBoolean1
    }

    override val isOneShot: Boolean
        get() = false

    override fun getMaxDuration(): Int {
        return random.nextInt(300) + 50
    }

    override fun getSprites(): List<Sprite> {
        val arrayList: ArrayList<Sprite> = ArrayList()
        arrayList.add(Sprite(13, 0, 0, 16))
        arrayList.add(Sprite(13, 0, -1, 4))
        arrayList.add(Sprite(11, 0, -1, 4))
        arrayList.add(Sprite(12, 0, -1, 4))
        arrayList.add(Sprite(12, 0, 0, 16))
        arrayList.add(Sprite(12, 0, -2, 4))
        arrayList.add(Sprite(11, 0, -2, 4))
        arrayList.add(Sprite(13, 0, -2, 4))
        return arrayList
    }
}