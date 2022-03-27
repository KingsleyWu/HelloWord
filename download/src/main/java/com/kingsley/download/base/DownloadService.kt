package com.kingsley.download.base

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {

    @Streaming
    @GET
    suspend fun download(@Header("RANGE") start: String? = "0", @Url url: String?): Response<ResponseBody>
}