package com.example.retrofitnavigation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitnavigation.databinding.ItemLoadingBinding
import com.example.retrofitnavigation.databinding.ItemUserBinding
import com.example.retrofitnavigation.model.GithubUser
import com.example.retrofitnavigation.model.PagingData

class UserAdapter(
    private val onUserClicked: (GithubUser) -> Unit
) : ListAdapter<PagingData<GithubUser>, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is PagingData.Content -> TYPE_USER
            PagingData.Loading -> TYPE_LOADING
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_USER -> {
                UserViewHolder(
                    binding = ItemUserBinding.inflate(layoutInflater, parent, false),
                    onUserClicked = onUserClicked
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            else -> error("Unsupported viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = (getItem(position) as? PagingData.Content)?.data ?: return
        (holder as? UserViewHolder)?.bind(user)
    }

    companion object {

        private const val TYPE_USER = 1
        private const val TYPE_LOADING = 2

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PagingData<GithubUser>>() {
            override fun areItemsTheSame(
                oldItem: PagingData<GithubUser>,
                newItem: PagingData<GithubUser>
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PagingData<GithubUser>,
                newItem: PagingData<GithubUser>
            ): Boolean {
                val oldUser = oldItem as? PagingData.Content
                val newUser = newItem as? PagingData.Content
                return oldUser?.data == newUser?.data
            }
        }
    }
}