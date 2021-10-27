package com.kingsley.shimeji.mascot.animations

internal abstract class Creep : Animation() {
    override val isOneShot: Boolean
        get() = false
}
