package com.kingsley.download.core

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 下载的协程作用域
 */
object DownloadScope : CoroutineScope by CoroutineScope(EmptyCoroutineContext)