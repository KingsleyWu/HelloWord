package com.kingsley.download.bean

import java.io.InputStream

sealed class Result {
    data class Success(val contentLength: Long, val byteStream: InputStream) : Result()
    data class Error(val code: Int, val message: String = "") : Result()
}
