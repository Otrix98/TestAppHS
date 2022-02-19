package com.example.testapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.scopedstorage.utils.ViewBindingFragment
import com.example.testapp.R
import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class DetailsFragment: ViewBindingFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {

    private val args: DetailsFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showDetails(args.repo)
        }

    @SuppressLint("SetTextI18n")
    private fun showDetails(repo: RemoteRepository){

        with(binding){
            nameTextView.text = repo.name
            authorTextView.text = repo.owner.login
            forksTextView.text = resources.getString(R.string.fork) + repo.forks.toString()
            watchersTextView.text = resources.getString(R.string.watch) + repo.watchers.toString()
            starsTextView.text = resources.getString(R.string.star) + repo.stars.toString()
            descriptionTextView.text = repo.description
            languageTextView.text =
                when(repo.language){
                    null -> ""
                    else -> {resources.getString(R.string.language) + repo.language}
                }
            }

        Glide.with(requireContext())
            .load(repo.owner.avatar_url)
            .error(R.drawable.avatar_nobody)
            .into(binding.profileImage)
        }
    }
