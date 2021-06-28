package com.kingsley.http

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpClientHelper {
    var timeout: Long = 15
    var timeUnit: TimeUnit = TimeUnit.SECONDS

    var okHttpClient: OkHttpClient =
        OkHttpClient
            .Builder()
            .callTimeout(timeout, timeUnit)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .build()
        private set

    fun setOkHttpClient(okHttpClient: OkHttpClient) {
        this.okHttpClient = okHttpClient
    }

    fun okHttpClientBuilder() = OkHttpClient.Builder()

    fun setTimeOut(timeout: Long) {
        okHttpClient.newBuilder()
            .callTimeout(timeout, TimeUnit.MILLISECONDS)
            .connectTimeout(timeout, TimeUnit.MILLISECONDS)
            .readTimeout(timeout, TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, TimeUnit.MILLISECONDS)
    }

    fun setTimeOut(timeout: Long, timeUnit: TimeUnit) {
        okHttpClient.newBuilder()
            .callTimeout(timeout, timeUnit)
            .connectTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
    }

    fun setRetryOnConnectionFailure(retryOnConnectionFailure: Boolean) {
        okHttpClient.newBuilder().retryOnConnectionFailure(retryOnConnectionFailure)
    }

    fun setCache(cache: Cache) {
        okHttpClient.newBuilder().cache(cache)
    }

    fun addInterceptor(interceptor: Interceptor) {
        okHttpClient.newBuilder().addInterceptor(interceptor)
    }

}