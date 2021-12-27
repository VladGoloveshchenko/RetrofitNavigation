package com.example.retrofitnavigation

import com.example.retrofitnavigation.model.GithubUser

sealed class PageItem {
    data class Content(val user: GithubUser)

    object Loading : PageItem()
}
