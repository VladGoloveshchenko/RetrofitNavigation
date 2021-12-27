package com.example.retrofitnavigation.model

import com.google.gson.annotations.SerializedName

data class GithubUser(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)