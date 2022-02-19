package com.example.testapp.ui.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.testapp.R
import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.databinding.ItemRepositoryBinding


class ListAdapter(
    diffCallback: DiffUtil.ItemCallback<RemoteRepository>
) : PagingDataAdapter<RemoteRepository, ListAdapter.RepoHolder>(diffCallback) {
    private var listener: AdaptersListener? = null
    fun setOnClickListener(onClickListener: AdaptersListener) {
        this.listener = onClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepoHolder {
        val itemBinding =
            ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoHolder(
            itemBinding
        )
    }

    override fun onBindViewHolder(holder: RepoHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


   inner class RepoHolder(
        private val binding: ItemRepositoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(value: RemoteRepository) {
            binding.nameTextView.text = value.name
            binding.descriptionTextView.text = value.description

            binding.root.setOnClickListener {
                value?.let { repo -> listener?.onClickItem(repo) }
            }
        }
    }
}