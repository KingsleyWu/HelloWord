package com.kingsley.shimeji.mascot.animations

internal abstract class Tripping : Animation() {
    override fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    ) {
    }

    override val isOneShot: Boolean
        get() = true
}
