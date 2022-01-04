package com.example.retrofitnavigation.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitnavigation.R
import com.example.retrofitnavigation.adapter.UserAdapter
import com.example.retrofitnavigation.addPaginationScrollListener
import com.example.retrofitnavigation.databinding.FragmentListBinding
import com.example.retrofitnavigation.model.GithubUser
import com.example.retrofitnavigation.model.PagingData
import com.example.retrofitnavigation.retrofit.GithubService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = requireNotNull(_binding) {
        "View was destroyed"
    }

    private val adapter = UserAdapter { user ->
        findNavController().navigate(
            ListFragmentDirections.toDetails(user.login)
        )
    }

    private var isLoading = false
    private var currentPage = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentListBinding.inflate(inflater, container, false)
            .also { binding ->
                _binding = binding
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUsers()

        with(binding) {
            toolbar.menu
                .findItem(R.id.action_search)
                .let { it.actionView as SearchView }
                .setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        return true
                    }
                })

            layoutSwiperefresh.setOnRefreshListener {
                adapter.submitList(emptyList())
                currentPage = 0
                loadUsers {
                    layoutSwiperefresh.isRefreshing = false
                }
            }

            val linearLayoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.VERTICAL, false
            )

            recyclerView.adapter = adapter
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.addHorizontalSpaceDecoration(RECYCLER_ITEM_SPACE)
            recyclerView.addPaginationScrollListener(linearLayoutManager, COUNT_TO_LOAD) {
                loadUsers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUsers(onLoadingFinished: () -> Unit = {}) {
        if (isLoading) return

        isLoading = true

        val loadingFinishedCallback = {
            isLoading = false
            onLoadingFinished()
        }

        val since = currentPage * PAGE_SIZE
        GithubService.githubApi.getUsers(since, PAGE_SIZE)
            .enqueue(object : Callback<List<GithubUser>> {
                override fun onResponse(
                    call: Call<List<GithubUser>>,
                    response: Response<List<GithubUser>>
                ) {
                    if (response.isSuccessful) {
                        val newList = adapter.currentList
                            .dropLastWhile { it == PagingData.Loading }
                            .plus(response.body()?.map { PagingData.Content(it) }.orEmpty())
                            .plus(PagingData.Loading)
                        adapter.submitList(newList)
                        currentPage++
                    } else {
                        handleErrors(response.errorBody()?.string() ?: GENERAL_ERROR_MESSAGE)
                    }

                    loadingFinishedCallback()
                }

                override fun onFailure(call: Call<List<GithubUser>>, t: Throwable) {
                    handleErrors(t.message ?: GENERAL_ERROR_MESSAGE)
                    loadingFinishedCallback()
                }
            })
    }

    private fun handleErrors(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
            .setAction(android.R.string.ok) {}
            .show()
    }

    companion object {
        private const val RECYCLER_ITEM_SPACE = 50

        private const val PAGE_SIZE = 50
        private const val COUNT_TO_LOAD = 15

        private const val GENERAL_ERROR_MESSAGE = "Something went wrong"
    }
}

fun RecyclerView.addHorizontalSpaceDecoration(space: Int) {
    addItemDecoration(
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position != 0 && position != parent.adapter?.itemCount) {
                    outRect.top = space
                }
            }
        }
    )
}