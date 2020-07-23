package ru.g0rd1.peoplesfinder.apiservice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ApiClientFactory @Inject constructor() {

    fun create(): ApiClient {
        val interceptor = HttpLoggingInterceptor()
        // interceptor.level = if (BuildConfig.DEBUG)
        //     HttpLoggingInterceptor.Level.BODY
        // else
        //     HttpLoggingInterceptor.Level.BASIC

        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiClient::class.java)
    }

    companion object {
        const val BASE_URL = "https://api.vk.com/method/"
    }

}