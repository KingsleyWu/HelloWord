package com.kingsley.helloword.utils

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object CustomExecutors {

    private var scheduledExecutor: ScheduledExecutorService? = null
    private val SCHEDULED_EXECUTOR_LOCK = Any()

    private fun isAndroidRuntime(): Boolean {
        val javaRuntimeName = System.getProperty("java.runtime.name") ?: return false
        return javaRuntimeName.lowercase().contains("android")
    }

    /**
     * An [Executor] that executes tasks on the UI thread.
     */
    private val UI_THREAD_EXECUTOR: Executor = AndroidExecutors.uiThread()

    /**
     * An [Executor] that executes tasks in parallel.
     */
    private val BACKGROUND_EXECUTOR =
        if (!isAndroidRuntime()) Executors.newCachedThreadPool() else AndroidExecutors.newCachedThreadPool()

    /**
     * An [Executor] that executes tasks single thread for common
     */
    private val BACKGROUND_EXECUTOR_SINGLE: ExecutorService = AndroidExecutors.newSingleThread()

    /**
     * An [Executor] that executes tasks single thread for upload files
     */
    private val BACKGROUND_EXECUTOR_FOR_UPLOAD_SINGLE: ExecutorService =
        AndroidExecutors.newSingleThread()

    /**
     * An [Executor] that executes tasks single thread for im send message
     */
    private val BACKGROUND_EXECUTOR_FOR_SEND_SINGLE: ExecutorService =
        AndroidExecutors.newSingleThread()

    /**
     * An [Executor] that executes tasks single thread for im receive message
     */
    private val BACKGROUND_EXECUTOR_FOR_RECEIVE_SINGLE: ExecutorService =
        AndroidExecutors.newSingleThread()

    @JvmStatic
    fun scheduled(): ScheduledExecutorService? {
        synchronized(SCHEDULED_EXECUTOR_LOCK) {
            if (scheduledExecutor == null) {
                scheduledExecutor =
                    Executors.newScheduledThreadPool(1)
            }
        }
        return scheduledExecutor
    }

    @JvmStatic
    fun main(): Executor {
        return UI_THREAD_EXECUTOR
    }

    @JvmStatic
    fun io(): Executor {
        return BACKGROUND_EXECUTOR
    }

    @JvmStatic
    fun single(): ExecutorService {
        return BACKGROUND_EXECUTOR_SINGLE
    }

    @JvmStatic
    fun singleUpload(): ExecutorService {
        return BACKGROUND_EXECUTOR_FOR_UPLOAD_SINGLE
    }

    @JvmStatic
    fun singleSend(): ExecutorService {
        return BACKGROUND_EXECUTOR_FOR_SEND_SINGLE
    }

    @JvmStatic
    fun singleReceive(): ExecutorService {
        return BACKGROUND_EXECUTOR_FOR_RECEIVE_SINGLE
    }
}