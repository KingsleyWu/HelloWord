package com.kingsley.download.core

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitDownload {

    val downloadService: DownloadService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
        val okHttpClient = createOkHttpClient()
        val retrofit = createRetrofit(okHttpClient)
        retrofit.create(DownloadService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://download")
            .client(client)
            .build()
    }
}