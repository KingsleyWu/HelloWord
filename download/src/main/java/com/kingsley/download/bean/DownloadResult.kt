package com.kingsley.download.bean

import java.io.InputStream

sealed class DownloadResult {
    data class Success(val contentLength: Long, val supportRange: Boolean = false, val byteStream: InputStream) : DownloadResult()
    data class Redirect(val newUrl: String, val contentLength: Long) : DownloadResult()
    data class Error(val code: Int, val message: String = "") : DownloadResult()
}
