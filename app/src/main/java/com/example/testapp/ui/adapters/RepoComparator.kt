package com.example.testapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.testapp.data.models.RemoteRepository

object RepoComparator:  DiffUtil.ItemCallback<RemoteRepository>() {
    override fun areItemsTheSame(oldItem: RemoteRepository, newItem: RemoteRepository): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RemoteRepository, newItem: RemoteRepository): Boolean {
        return oldItem == newItem
    }
}