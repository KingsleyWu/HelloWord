package com.kingsley.download.base

import com.kingsley.common.L
import com.kingsley.download.bean.DownloadResult
import com.kingsley.download.core.IDownloader
import okhttp3.OkHttpClient
import okhttp3.internal.closeQuietly
import retrofit2.Retrofit

object RetrofitDownload : IDownloader {

    private val downloadService: DownloadService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient)
        retrofit.create(DownloadService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://download")
            .client(client)
            .build()
    }

    override suspend fun download(start: String?, url: String): DownloadResult {
        val response = downloadService.download(start ?: "0", url)
        if (response.isSuccessful) {
            val responseBody = response.body()
            responseBody ?: return DownloadResult.Error(response.code(), "ResponseBody is null")
            val supportRange = !response.headers()["Content-Range"].isNullOrEmpty()
            L.d("wwc download supportRange = $supportRange")
            return DownloadResult.Success(responseBody.contentLength(), supportRange, responseBody.byteStream())
        } else {
            return DownloadResult.Error(response.code(), response.message())
        }
    }

    override suspend fun redirect(url: String): DownloadResult {
        val response = downloadService.redirect(url)
        if (response.isSuccessful) {
            val responseBody = response.body()
            responseBody ?: return DownloadResult.Error(response.code(), "ResponseBody is null")
            val newUrl = response.headers()["Location"] ?: response.headers()["location"] ?: response.raw().request.url.toString()
            val contentLength = responseBody.contentLength()
            responseBody.closeQuietly()
            return DownloadResult.Redirect(newUrl, contentLength)
        } else {
            return DownloadResult.Error(response.code(), response.message())
        }
    }
}