package com.example.retrofitnavigation.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.retrofitnavigation.databinding.ItemUserBinding
import com.example.retrofitnavigation.model.GithubUser

class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: GithubUser) {
        with(binding) {
            image.load(user.avatarUrl)
            textName.text = user.login
        }
    }
}