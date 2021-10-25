package com.kingsley.common

import android.util.Log

/**
 * @author Kingsley
 * Created on 2021/5/14.
 */
object L {
    private var debug = false
    private var tag = "X-LOG"

    fun setDebug(debug: Boolean) {
        this.debug = debug
    }

    fun setTag(tag: String){
        this.tag = tag
    }

    fun d(msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (debug) {
            Log.d(tag, msg)
        }
    }

    fun d(tag: String, msg: String, tr: Throwable?) {
        if (debug) {
            Log.d(tag, msg, tr)
        }
    }


    fun i(msg: String) {
        if (debug) {
            Log.i(tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (debug) {
            Log.i(tag, msg)
        }
    }

    fun i(tag: String, msg: String, tr: Throwable?) {
        if (debug) {
            Log.i(tag, msg, tr)
        }
    }

    fun e(msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (debug) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, msg: String, tr: Throwable?) {
        if (debug) {
            Log.e(tag, msg, tr)
        }
    }

    fun w(msg: String) {
        if (debug) {
            Log.w(tag, msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (debug) {
            Log.w(tag, msg)
        }
    }

    fun w(tag: String, msg: String, tr: Throwable?) {
        if (debug) {
            Log.w(tag, msg, tr)
        }
    }

    fun v(msg: String) {
        if (debug) {
            Log.v(tag, msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (debug) {
            Log.v(tag, msg)
        }
    }

    fun v(tag: String, msg: String, tr: Throwable?) {
        if (debug) {
            Log.v(tag, msg, tr)
        }
    }

    fun getStackTraceString(tr: Throwable?) = Log.getStackTraceString(tr)

    fun println(message: String) {
        if (debug) {
            kotlin.io.println("$tag : $message")
        }
    }

}