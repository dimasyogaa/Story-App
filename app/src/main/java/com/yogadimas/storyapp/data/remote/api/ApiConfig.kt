package com.yogadimas.storyapp.data.remote.api

import com.yogadimas.storyapp.BuildConfig
import com.yogadimas.storyapp.BuildConfig.BASE_URL_API
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    companion object {

        fun getApiService(token: String): ApiService {

            val loggingInterceptor = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            } else {
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
            }

            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = if (token.isNotEmpty()) {
                    req.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    req.newBuilder()
                        .build()
                }
                chain.proceed(requestHeaders)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)

        }

    }
}