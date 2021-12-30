package com.example.retrofitnavigation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.retrofitnavigation.databinding.FragmentDetailsBinding
import com.example.retrofitnavigation.model.GithubUserDetails
import com.example.retrofitnavigation.retrofit.GithubService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = requireNotNull(_binding) {
        "View was destroyed"
    }

    val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDetailsBinding.inflate(inflater, container, false)
            .also { binding ->
                _binding = binding
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserDetails()

        with(binding) {
            toolbar.setupWithNavController(findNavController())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserDetails() {
        GithubService.githubApi.getUserDetails(args.username)
            .enqueue(object : Callback<GithubUserDetails> {
                override fun onResponse(
                    call: Call<GithubUserDetails>,
                    response: Response<GithubUserDetails>
                ) {
                    if (response.isSuccessful) {
                        val userDetails = response.body() ?: return
                        with(binding) {
                            avatar.load(userDetails.avatarUrl)
                            login.text = userDetails.login
                            followers.text = "Followers: ${userDetails.followers}"
                            following.text = "Following: ${userDetails.following}"
                        }
                    } else {
                        handleErrors(response.errorBody()?.string() ?: "")
                    }
                }

                override fun onFailure(call: Call<GithubUserDetails>, t: Throwable) {
                    handleErrors(t.message ?: "")
                }
            })
    }

    private fun handleErrors(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT)
            .setAction(android.R.string.ok) {}
            .show()
    }
}