package com.kingsley.skin.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

val pool: ExecutorService = ThreadPoolExecutor(1, 5, 10, TimeUnit.SECONDS, ArrayBlockingQueue(6) )

/**
 * 执行一个异步操作
 *
 * @param preExecute
 * @param doBackground 在后台线程执行的任务
 * @param postExecute 后台任务执行完成后执行的任务，该任务在ui现场执行
 *
 * @author fortunexiao
 */
fun <T> async(
    preExecute: (() -> Unit)? = null,
    doBackground: () -> T,
    postExecute: ((result: T) -> Unit)? = null
) {
    preExecute?.invoke()
    pool.execute{
        val result = doBackground.invoke()
        runUIThread{
            postExecute?.invoke(result)
        }
    }
}

/**
 * 异步执行一个操作
 */
fun async(doBackground: () -> Unit) {
    async<Unit>(doBackground = doBackground)
}

/**
 * 在ui线程运行某个任务
 */
fun runUIThread(action: (() -> Unit)?) {
    runUIThread(action, 0)
}

/**
 * 在ui线程延迟运行某个任务
 */
fun runUIThread(action: (() -> Unit)?, delay: Long) {
    Handler(Looper.getMainLooper()).postDelayed({
        action?.invoke()
    }, delay)
}