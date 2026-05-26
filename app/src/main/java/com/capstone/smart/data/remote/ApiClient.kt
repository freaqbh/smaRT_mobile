package com.capstone.smart.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // URL server Laravel Anda. Gunakan 10.0.2.2 jika di emulator Android Studio.
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    // Variabel untuk menyimpan token
    var authToken: String? = null

    // Logging interceptor untuk melihat log request/response di Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // JWT Interceptor: Menyisipkan token ke header
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        
        val requestBuilder = originalRequest.newBuilder()
        authToken?.let { token ->
            requestBuilder.header("Authorization", "Bearer $token")
        }
        
        // Don't set Content-Type manually — Retrofit sets it automatically
        // (application/json for @Body, multipart/form-data for @Multipart)
        requestBuilder.header("Accept", "application/json")

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instance Retrofit
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
