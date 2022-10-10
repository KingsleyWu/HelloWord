package com.kingsley.helloword.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import java.util.concurrent.*

object AndroidExecutors {
    private val uiThread: Executor = UIThreadExecutor()
    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private const val KEEP_ALIVE_TIME = 3L
    private val CORE_POOL_SIZE = CPU_COUNT + 1
    private val MAX_POOL_SIZE = CPU_COUNT * 2 + 1

    @JvmStatic
    fun newCachedThreadPool(): ExecutorService {
        val executor = ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 3L, TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )
        allowCoreThreadTimeout(executor, true)
        return executor
    }

    @JvmStatic
    fun newCachedThreadPool(maxPoolSize: Int): ExecutorService {
        val executor = ThreadPoolExecutor(
            maxPoolSize,
            maxPoolSize,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingQueue()
        )
        allowCoreThreadTimeout(executor, true)
        return executor
    }

    @JvmStatic
    fun newCachedThreadPool(threadFactory: ThreadFactory?): ExecutorService {
        val executor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            LinkedBlockingQueue(),
            threadFactory
        )
        allowCoreThreadTimeout(executor, true)
        return executor
    }

    @SuppressLint("NewApi")
    @JvmStatic
    fun allowCoreThreadTimeout(executor: ThreadPoolExecutor, value: Boolean) {
        executor.allowCoreThreadTimeOut(value)
    }

    @JvmStatic
    fun uiThread(): Executor {
        return uiThread
    }

    @JvmStatic
    fun newSingleThread(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    internal class UIThreadExecutor : Executor {
        override fun execute(command: Runnable) {
            Handler(Looper.getMainLooper()).post(command)
        }
    }
}