package com.kingsley.crash

interface CrashListener {

    /**
     * 捕获的crash数据
     * @param t     當前出現錯誤的線程
     * @param ex    捕获的crash数据
     */
    fun recordException(t: Thread, ex: Throwable)

    /**
     * 处理未捕获异常后
     */
    fun afterHandleException()
}