package com.example.retrofitnavigation.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.size.ViewSizeResolver
import coil.transform.CircleCropTransformation
import com.example.retrofitnavigation.R
import com.example.retrofitnavigation.databinding.ItemUserBinding
import com.example.retrofitnavigation.model.GithubUser

class UserViewHolder(
    private val binding: ItemUserBinding,
    private val onUserClicked: (GithubUser) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: GithubUser) {
        with(binding) {
            image.load(user.avatarUrl) {
                scale(Scale.FIT)
                size(ViewSizeResolver(root))
            }
            textName.text = user.login

            root.setOnClickListener {
                onUserClicked(user)
            }
        }
    }
}