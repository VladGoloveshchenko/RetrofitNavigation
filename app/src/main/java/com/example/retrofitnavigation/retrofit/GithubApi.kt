package com.example.retrofitnavigation.retrofit

import com.example.retrofitnavigation.model.GithubSearchUsers
import com.example.retrofitnavigation.model.GithubUser
import com.example.retrofitnavigation.model.GithubUserDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("users")
    fun getUsers(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int
    ): Call<List<GithubUser>>

    @GET("users/{username}")
    fun getUserDetails(
        @Path("username") username: String
    ): Call<GithubUserDetails>

    @GET("search/users")
    fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<GithubSearchUsers>
}