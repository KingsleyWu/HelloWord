package com.kingsley.shimeji.mascot.animations

abstract class Jump : Animation() {
    override val isOneShot: Boolean
        get() = false
}