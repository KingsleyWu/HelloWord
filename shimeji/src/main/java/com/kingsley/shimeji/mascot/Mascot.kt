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

    private fun applyVelocityX(velocityX: Int) {
        setX(dx + velocityX)
    }

    private fun applyVelocityY(velocityY: Int) {
        setY(dy + velocityY)
    }

    private fun checkConditions() {
        if (isBeingDragged) {
            if (animation !is Dragging) {
                animation = Dragging()
            }
        } else if (isBeingFlung) {
            if (animation !is Flinging){
                animation = Flinging(flingVelocityX)
            }
        } else {
            val temp = animation
            if (temp is Dragging) {
                animation = temp.drop()
            } else if (temp is Flinging) {
                animation = temp.drop()
            }
        }
        animation.checkBorders(
            dy <= margins.top,
            dy + height >= margins.bottom,
            dx <= margins.left,
            dx + width >= margins.right
        )
    }

    private fun getAnimationDelay(): Int {
        return 30
    }

    private fun loadBitmaps(sprites: Sprites) {
        frames = sprites
        height = sprites.height
        width = frames.width
        xOffset = frames.xOffset
    }

    private fun moveManually(x: Int, y: Int) {
        setX(x)
        setY(y)
    }

    private fun setPlayground(playground: Playground) {
        margins = playground
    }

    private fun setX(dx: Int) {
        this.dx = when {
            dx < margins.left -> {
                margins.left
            }
            dx > margins.right - width -> {
                margins.right - width
            }
            else -> {
                dx
            }
        }
    }

    private fun setY(dy: Int) {
        this.dy = when {
            dy > margins.bottom - height -> {
                margins.bottom - height
            }
            dy < margins.top -> {
                margins.top
            }
            else -> {
                dy
            }
        }
    }

    private fun updateAnimation() {
        checkConditions()
        var velocityX = animation.xVelocity.toDouble()
        var velocityY = speedMultiplier
        applyVelocityX((velocityX * velocityY).toInt())
        velocityY = animation.yVelocity.toDouble()
        velocityX = speedMultiplier
        applyVelocityY((velocityY * velocityX).toInt())
        isFacingLeft = animation.isFacingLeft
        animation = animation.frameTick()
    }

    fun drag(dx: Int, dy: Int) {
        setX(dx)
        setY(dy)
        isBeingDragged = true
    }

    fun endDragging() {
        isBeingDragged = false
    }

    fun endFlinging() {
        isBeingFlung = false
    }

    fun fling(x: Int, y: Int) {
        setX(x)
        setY(y)
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

    fun handleTouchEvent(event: MotionEvent): Boolean {
        val actionMasked = event.actionMasked
        var actionIndex = 1
        var k = 0
        if (actionMasked != 0) {
            if (actionMasked != 1) if (actionMasked != 2) {
                if (actionMasked != 3) {
                    if (actionMasked == 6 && isBeingDragged) {
                        isBeingDragged = false
                        k = event.actionIndex
                        if (event.getPointerId(k) == touch.mActivePointerId) {
                            if (k != 0) {
                                actionIndex = 0
                            }
                            touch.mLastTouchX = event.getX(actionIndex)
                            touch.mLastTouchY = event.getY(actionIndex)
                            touch.mActivePointerId = event.getPointerId(actionIndex)
                        }
                    }
                    return isBeingDragged
                }
            } else {
                if (isBeingDragged) {
                    actionIndex = event.findPointerIndex(touch.mActivePointerId)
                    if (actionIndex == -1) {
                        actionIndex = k
                    }
                    val x = event.getX(actionIndex)
                    val y = event.getY(actionIndex)
                    val lx: Float = touch.mLastTouchX
                    val ly: Float = touch.mLastTouchY
                    moveManually((x - lx).toInt() + dx, (y - ly).toInt() + dy)
                    touch.mLastTouchX = x
                    touch.mLastTouchY = y
                }
                return isBeingDragged
            }
            if (isBeingDragged) {
                touch.invalidate()
                isBeingDragged = false
            }
        } else {
            actionIndex = event.actionIndex
            val x = event.getX(actionIndex)
            val y = event.getY(actionIndex)
            if (event.x > dx - 20 && event.y > dy - 20 && event.x < dx + width + 20 && event.y < dy + height + 20) {
                isBeingDragged = true
                touch.mLastTouchX = x
                touch.mLastTouchY = y
                touch.mActivePointerId = event.getPointerId(0)
            }
        }
        return isBeingDragged
    }

    fun initialize(sprites: Sprites, speedMultiplier: Double, playground: Playground) {
        loadBitmaps(sprites)
        setPlayground(
            Playground(
                playground.top - xOffset,
                playground.bottom,
                playground.left - xOffset,
                playground.right + xOffset
            )
        )
        setSpeedMultiplier(speedMultiplier)
        animation = Falling()
        setX(Random().nextInt(margins.right - width))
    }

    fun isBeingFlung(): Boolean {
        return isBeingFlung
    }

    fun kill() {
        thread?.isRunning = false
        thread?.interrupt()
    }

    fun resetEnvironmentVariables(playground: Playground) {
        setPlayground(
            Playground(
                playground.top,
                playground.bottom,
                playground.left - xOffset,
                playground.right + xOffset
            )
        )
        animation = Falling()
    }

    fun setFlingSpeed(flingVelocityX: Int, flingVelocityY: Int) {
        this.flingVelocityX = flingVelocityX
        this.flingVelocityY = flingVelocityY
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
        thread = MascotThread()
        if (thread!!.state == Thread.State.NEW) {
            thread!!.start()
        }
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
