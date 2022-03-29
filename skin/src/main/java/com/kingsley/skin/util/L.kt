package com.kingsley.skin.util

import android.util.Log

/**
 * Created by kingsley on 2020/9/11
 */
object L {
    var DEBUG = true
    const val TAG = "L"

    fun i(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.i(tag, msg)
        }
    }

    fun v(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun w(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.w(tag, msg)
        }
    }

    fun e(tag: String = TAG, msg: String) {
        if (DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun i(tag: String = TAG, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.i(tag, msg, e)
        }
    }

    fun v(tag: String = TAG, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.v(tag, msg, e)
        }
    }

    fun d(tag: String = TAG, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.d(tag, msg, e)
        }
    }

    fun w(tag: String = TAG, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.w(tag, msg, e)
        }
    }

    fun e(tag: String = TAG, msg: String, e: Throwable) {
        if (DEBUG) {
            Log.e(tag, msg, e)
        }
    }

    fun w(tag: String = TAG, e: Throwable) {
        if (DEBUG) {
            Log.w(tag, e)
        }
    }

    fun e(tag: String = TAG, e: Throwable) {
        if (DEBUG) {
            Log.e(tag, Log.getStackTraceString(e))
        }
    }
}