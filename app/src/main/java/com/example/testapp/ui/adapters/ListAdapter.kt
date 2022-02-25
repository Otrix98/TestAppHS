package com.example.testapp.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testapp.R
import com.example.testapp.data.models.MenuItem
import com.example.testapp.databinding.ItemFoodBinding


class ListAdapter() : PagingDataAdapter<MenuItem, ListAdapter.RepoHolder>(ItemComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RepoHolder {
        val itemBinding =
            ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepoHolder(
            itemBinding
        )
    }

    override fun onBindViewHolder(holder: RepoHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            Log.d ("ISLISTEMPTY", item.toString() )
        }
    }

    private object ItemComparator:  DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem == newItem
        }
    }


   inner class RepoHolder(
        private val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(value: MenuItem) {
            val menuImageView = itemView.findViewById<ImageView>(R.id.foodImage)
            binding.nameTextView.text = value.title
            binding.restaurantChainTextView.text = value.restaurantChain
            binding.priceTextView.text = "от " + value.id.toString().substring(3) + " р"

            Glide.with(itemView)
                .load(value.image)
                .error(R.drawable.emptydish)
                .into(menuImageView)
        }
    }
}