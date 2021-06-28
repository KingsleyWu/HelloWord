package com.kingsley.http

import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    var baseUrl: String = ""
    private var builder: Builder = Builder()

    fun <T> create(baseUrl: String, service: Class<T>): T {
        builder.baseUrl = baseUrl
        return builder.create(service)
    }

    fun <T> create(service: Class<T>): T {
        builder.baseUrl = baseUrl
        return builder.create(service)
    }

    fun <T> create(okHttpClient: OkHttpClient, service: Class<T>): T {
        builder.baseUrl = baseUrl
        builder.setOkHttpClient(okHttpClient)
        return builder.create(service)
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient) : RetrofitHelper {
        builder.setOkHttpClient(okHttpClient)
        return this
    }

    fun addConverterFactory(factory: Converter.Factory) : RetrofitHelper{
        builder.addConverterFactory(factory)
        return this
    }

    fun addCallAdapterFactory(factory: CallAdapter.Factory) : RetrofitHelper{
        builder.addCallAdapterFactory(factory)
        return this
    }

    internal class Builder{
        var baseUrl: String = ""
        var builder: Retrofit.Builder = Retrofit
            .Builder()
            .client(OkHttpClientHelper.okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())

        fun <T> create(baseUrl: String, service: Class<T>): T {
            return builder.baseUrl(baseUrl).build().create(service)
        }

        fun <T> create(service: Class<T>): T {
            return builder.baseUrl(baseUrl).build().create(service)
        }

        fun <T> create(okHttpClient: OkHttpClient, service: Class<T>): T {
            return builder.client(okHttpClient).baseUrl(baseUrl).build().create(service)
        }

        fun setOkHttpClient(okHttpClient: OkHttpClient) {
            builder.client(okHttpClient)
        }

        fun addConverterFactory(factory: Converter.Factory) {
            builder.addConverterFactory(factory)
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory) {
            builder.addCallAdapterFactory(factory)
        }
    }
}