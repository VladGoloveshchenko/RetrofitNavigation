package com.example.retrofitnavigation.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitnavigation.R
import com.example.retrofitnavigation.adapter.UserAdapter
import com.example.retrofitnavigation.databinding.FragmentListBinding
import com.example.retrofitnavigation.model.GithubUser
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

    private val adapter = UserAdapter()

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
                        handleSearch(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        return true
                    }
                })

            layoutSwiperefresh.setOnRefreshListener {
                adapter.submitList(emptyList())
                loadUsers {
                    layoutSwiperefresh.isRefreshing = false
                }
            }

            recyclerView.adapter = adapter
            recyclerView.addHorizontalSpaceDecoration(RECYCLER_ITEM_SPACE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUsers(onFinishLoading: () -> Unit = {}) {
        GithubService.githubApi.getUsers(0, 50)
            .enqueue(object : Callback<List<GithubUser>> {
                override fun onResponse(
                    call: Call<List<GithubUser>>,
                    response: Response<List<GithubUser>>
                ) {
                    if (response.isSuccessful) {
                        adapter.submitList(response.body())
                    }
                    onFinishLoading()
                }

                override fun onFailure(call: Call<List<GithubUser>>, t: Throwable) {
                    onFinishLoading()
                    Snackbar.make(binding.root, t.message ?: "", Snackbar.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun handleSearch(query: String) {

    }

    companion object {
        private const val RECYCLER_ITEM_SPACE = 50
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