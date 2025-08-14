package com.bersamadapa.recylefood.network.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://enormous-mint-tomcat.ngrok-free.app/api/v1/"  // Replace with your base URL

    // Create a logging interceptor to view network logs in Logcat (useful during development)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
        .addInterceptor(loggingInterceptor)
        .build()

    // Create Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())  // Use Gson converter to handle JSON parsing
        .build()

    // Provide the API service
    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}
