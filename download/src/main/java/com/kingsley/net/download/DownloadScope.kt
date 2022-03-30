package com.kingsley.net.download

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

object DownloadScope : CoroutineScope by CoroutineScope(EmptyCoroutineContext)