package com.kingsley.download.base

import com.kingsley.download.core.IDownloader
import com.kingsley.download.bean.Result
import okhttp3.OkHttpClient
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

    override suspend fun download(start: String?, url: String?): Result {
        val response = downloadService.download(start ?: "0", url)
        if (response.isSuccessful) {
            val responseBody = response.body()
            responseBody ?: return Result.Error(response.code(), "ResponseBody is null")
            return Result.Success(responseBody.contentLength(), responseBody.byteStream())
        } else {
            return Result.Error(response.code(), response.message())
        }
    }
}