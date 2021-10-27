package com.kingsley.shimeji.mascot

class MotionAttributes {
    private val INVALID_POINTER_ID = -1
    var mActivePointerId = -1
    var mLastTouchX = 0f
    var mLastTouchY = 0f
    fun invalidate() {
        mActivePointerId = -1
    }
}
