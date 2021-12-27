package com.example.retrofitnavigation.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object GithubService {

    private val retrofit by lazy(LazyThreadSafetyMode.NONE) { provideRetrofit() }
    val githubApi by lazy(LazyThreadSafetyMode.NONE) {
        retrofit.create<GithubApi>()
    }

    private fun provideRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}