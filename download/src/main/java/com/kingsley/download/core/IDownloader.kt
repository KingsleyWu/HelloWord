package com.kingsley.download.core

import com.kingsley.download.bean.Result

interface IDownloader {
    suspend fun download(start: String? = "0", url: String?): Result
}