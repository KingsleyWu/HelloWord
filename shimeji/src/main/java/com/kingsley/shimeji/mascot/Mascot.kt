package com.kingsley.shimeji.mascot

import android.view.MotionEvent

import android.graphics.Bitmap
import com.kingsley.shimeji.mascot.animations.Animation
import com.kingsley.shimeji.mascot.animations.Dragging
import com.kingsley.shimeji.mascot.animations.Falling
import com.kingsley.shimeji.mascot.animations.Flinging
import java.util.*


/**
 * 吉祥物
 */
class Mascot {
    private lateinit var animation: Animation
    private var dx = 0
    private var dy = 0
    private var flingVelocityX = 0
    private var flingVelocityY = 0
    private lateinit var frames: Sprites
    private var height = 0
    private var isBeingDragged = false
    private var isBeingFlung = false

    @Volatile
    var isFacingLeft = false
    private lateinit var margins: Playground
    var name: String? = null
    private var speedMultiplier = 0.0
    private var thread: MascotThread? = null
    private val touch: MotionAttributes = MotionAttributes()
    private var width = 0
    private var xOffset = 0
    private fun applyVelocityX(paramInt: Int) {
        setX(dx + paramInt)
    }

    private fun applyVelocityY(paramInt: Int) {
        setY(dy + paramInt)
    }

    private fun checkConditions() {
        val bool2: Boolean
        if (isBeingDragged) {
            if (animation !is Dragging) animation = Dragging()
        } else if (isBeingFlung) {
            if (animation !is Flinging) animation = Flinging(flingVelocityX)
        } else {
            val temp = animation
            if (temp is Dragging) {
                animation = temp.drop()
            } else if (temp is Flinging) {
                animation = temp.drop()
            }
        }
        val animation = animation
        val i = dy
        val j: Int = margins.top
        var bool1 = true
        bool2 = i <= j
        val bool3: Boolean = dy + height >= margins.bottom
        val bool4 = dx <= margins.left
        if (dx + width < margins.right) bool1 = false
        animation.checkBorders(bool2, bool3, bool4, bool1)
    }

    private fun getAnimationDelay(): Int {
        return 30
    }

    private fun loadBitmaps(paramSprites: Sprites) {
        frames = paramSprites
        height = paramSprites.height
        width = frames.width
        xOffset = frames.xOffset
    }

    private fun moveManually(paramInt1: Int, paramInt2: Int) {
        setX(paramInt1)
        setY(paramInt2)
    }

    private fun setPlayground(paramPlayground: Playground) {
        margins = paramPlayground
    }

    private fun setX(paramInt: Int) {
        dx = when {
            paramInt < margins.left -> {
                margins.left
            }
            paramInt > margins.right - width -> {
                margins.right - width
            }
            else -> {
                paramInt
            }
        }
    }

    private fun setY(paramInt: Int) {
        dy = when {
            paramInt > margins.bottom - height -> {
                margins.bottom - height
            }
            paramInt < margins.top -> {
                margins.top
            }
            else -> {
                paramInt
            }
        }
    }

    private fun updateAnimation() {
        checkConditions()
        var d1: Double = animation.xVelocity.toDouble()
        var d2 = speedMultiplier
        java.lang.Double.isNaN(d1)
        applyVelocityX((d1 * d2).toInt())
        d2 = animation.yVelocity.toDouble()
        d1 = speedMultiplier
        java.lang.Double.isNaN(d2)
        applyVelocityY((d2 * d1).toInt())
        isFacingLeft = animation.isFacingLeft
        animation = animation.frameTick()
    }

    fun drag(paramInt1: Int, paramInt2: Int) {
        setX(paramInt1)
        setY(paramInt2)
        isBeingDragged = true
    }

    fun endDragging() {
        isBeingDragged = false
    }

    fun endFlinging() {
        isBeingFlung = false
    }

    fun fling(paramInt1: Int, paramInt2: Int) {
        setX(paramInt1)
        setY(paramInt2)
        isBeingDragged = false
    }

    fun getFrameBitmap(): Bitmap {
        return frames[animation.spriteIdentifier]!!
    }

    fun getX(): Int {
        return dx
    }

    fun getY(): Int {
        return dy
    }

    fun handleTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        val i = paramMotionEvent.actionMasked
        var j = 1
        var k = 0
        if (i != 0) {
            if (i != 1) if (i != 2) {
                if (i != 3) {
                    if (i == 6 && isBeingDragged) {
                        isBeingDragged = false
                        k = paramMotionEvent.actionIndex
                        if (paramMotionEvent.getPointerId(k) == touch.mActivePointerId) {
                            if (k != 0) j = 0
                            touch.mLastTouchX = paramMotionEvent.getX(j)
                            touch.mLastTouchY = paramMotionEvent.getY(j)
                            touch.mActivePointerId = paramMotionEvent.getPointerId(j)
                        }
                    }
                    return isBeingDragged
                }
            } else {
                if (isBeingDragged) {
                    j = paramMotionEvent.findPointerIndex(touch.mActivePointerId)
                    if (j == -1) j = k
                    val f1 = paramMotionEvent.getX(j)
                    val f2 = paramMotionEvent.getY(j)
                    val f3: Float = touch.mLastTouchX
                    val f4: Float = touch.mLastTouchY
                    moveManually((f1 - f3).toInt() + dx, (f2 - f4).toInt() + dy)
                    touch.mLastTouchX = f1
                    touch.mLastTouchY = f2
                }
                return isBeingDragged
            }
            if (isBeingDragged) {
                touch.invalidate()
                isBeingDragged = false
            }
        } else {
            j = paramMotionEvent.actionIndex
            val f1 = paramMotionEvent.getX(j)
            val f2 = paramMotionEvent.getY(j)
            if (paramMotionEvent.x > dx - 20 && paramMotionEvent.y > dy - 20 && paramMotionEvent.x < dx + width + 20 && paramMotionEvent.y < dy + height + 20) {
                isBeingDragged = true
                touch.mLastTouchX = f1
                touch.mLastTouchY = f2
                touch.mActivePointerId = paramMotionEvent.getPointerId(0)
            }
        }
        return isBeingDragged
    }

    fun initialize(sprites: Sprites, paramDouble: Double, paramPlayground: Playground) {
        loadBitmaps(sprites)
        setPlayground(
            Playground(
                paramPlayground.top - xOffset,
                paramPlayground.bottom,
                paramPlayground.left - xOffset,
                paramPlayground.right + xOffset
            )
        )
        setSpeedMultiplier(paramDouble)
        animation = Falling()
        setX(Random().nextInt(margins.right - width))
    }

    fun isBeingFlung(): Boolean {
        return isBeingFlung
    }

    fun kill() {
        val mascotThread = thread
        if (mascotThread != null) {
            mascotThread.isRunning = false
            thread!!.interrupt()
        }
    }

    fun resetEnvironmentVariables(paramPlayground: Playground) {
        setPlayground(
            Playground(
                paramPlayground.top,
                paramPlayground.bottom,
                paramPlayground.left - xOffset,
                paramPlayground.right + xOffset
            )
        )
        animation = Falling()
    }

    fun setFlingSpeed(paramInt1: Int, paramInt2: Int) {
        flingVelocityX = paramInt1
        flingVelocityY = paramInt2
        isBeingFlung = true
    }

    fun setSpeedMultiplier(speedMultiplier: Double) {
        var tempSpeedMultiplier = speedMultiplier
        if (tempSpeedMultiplier <= 0.1) {
            tempSpeedMultiplier = 1.0
        } else {
            tempSpeedMultiplier += 0.9
        }
        this.speedMultiplier = tempSpeedMultiplier
    }

    fun startAnimation() {
        val mascotThread = MascotThread()
        thread = mascotThread
        if (mascotThread.state == Thread.State.NEW) thread!!.start()
    }

    private inner class MascotThread : Thread() {
        @Volatile
        var isRunning = true
        override fun run() {
            while (isRunning) {
                try {
                    updateAnimation()
                    sleep(getAnimationDelay().toLong())
                } catch (interruptedException: InterruptedException) {
                    isRunning = false
                }
            }
        }
    }
}
