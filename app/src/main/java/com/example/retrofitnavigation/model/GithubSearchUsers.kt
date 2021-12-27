package com.example.retrofitnavigation.model

import com.google.gson.annotations.SerializedName

data class GithubSearchUsers(
    @SerializedName("total_count")
    val totalCount: Int,
    val items: List<GithubUser>
)
