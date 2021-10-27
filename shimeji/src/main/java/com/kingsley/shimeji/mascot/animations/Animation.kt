package com.kingsley.shimeji.mascot.animations

import java.util.*

abstract class Animation {
    private var frameNumber = 0
    private var lastSpriteFrame = 0
    private val mMaxDuration: Int
        get() = getMaxDuration()
    var nextAnimationRequested = false
    var random: Random = Random()
    private var spriteIndex = 0
    private val mSprites: List<Sprite>
        get() = getSprites()

    private fun updateSprite(): Boolean {
        lastSpriteFrame = frameNumber
        val i = spriteIndex
        val j = mSprites.size
        var bool = false
        if (i + 1 >= j) {
            bool = isOneShot
            spriteIndex = 0
        } else {
            spriteIndex++
        }
        return bool
    }

    abstract fun checkBorders(
        paramBoolean1: Boolean,
        paramBoolean2: Boolean,
        paramBoolean3: Boolean,
        paramBoolean4: Boolean
    )

    fun frameTick(): Animation {
        val i = frameNumber + 1
        frameNumber = i
        if (nextAnimationRequested) return nextAnimation
        val j = mMaxDuration
        return if (j in 1..i && optionalAnimation != null) optionalAnimation!! else if (frameNumber > lastSpriteFrame + (mSprites[spriteIndex] as Sprite).duration && updateSprite()) nextAnimation else this
    }

    abstract val direction: Direction
    abstract val isOneShot: Boolean

    open fun getMaxDuration(): Int {
        return 0
    }

    abstract val nextAnimation: Animation
    open val optionalAnimation: Animation?
        get() = null
    val spriteIdentifier: Int
        get() = mSprites[spriteIndex].index

    abstract fun getSprites(): List<Sprite>

    val xVelocity: Int
        get() = mSprites[spriteIndex].xVelocity
    val yVelocity: Int
        get() = mSprites[spriteIndex].yVelocity
    val isFacingLeft: Boolean
        get() {
            return direction == Direction.LEFT
        }

    enum class Direction(direction: String, index: Int) {
        LEFT("LEFT", 0), RIGHT("RIGHT", 1);
    }

    companion object {
        var flingEnabled = false
        var paidEnabled = false
    }
}
