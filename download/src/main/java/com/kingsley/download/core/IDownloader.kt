package com.kingsley.download.core

import com.kingsley.download.bean.DownloadResult

interface IDownloader {
    suspend fun download(start: String? = "0", url: String): DownloadResult

    suspend fun redirect(url: String): DownloadResult
}