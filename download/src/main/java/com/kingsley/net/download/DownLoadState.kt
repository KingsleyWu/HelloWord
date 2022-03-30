package com.kingsley.net.download

import java.io.InputStream

/** 无状态 0 */
const val NONE = 0

/** 等待中 1 */
const val WAITING = 1

/** 下载中 2 */
const val LOADING = 2

/** 暂停 3 */
const val PAUSE = 3

/** 错误 4 */
const val ERROR = 4

/** 完成 5 */
const val DONE = 5

sealed class DownLoadState {
    data class NONE(val contentLength: Long, val byteStream: InputStream) : DownLoadState()
}
