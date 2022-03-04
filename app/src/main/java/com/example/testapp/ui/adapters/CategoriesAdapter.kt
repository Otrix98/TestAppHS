package com.example.testapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.models.CategoryItem
import com.example.testapp.data.models.MenuItem
import com.example.testapp.databinding.ItemCategoryBinding

class CategoriesAdapter(
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {
    private var listener: AdaptersListener? = null
    fun setOnClickListener(onClickListener: AdaptersListener) {
        this.listener = onClickListener
    }

    var categoryList = emptyList<CategoryItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val itemBinding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(
            itemBinding
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(value: CategoryItem) {
            binding.textViewCategoryName.text = value.name
            if (value.isPressed) {
                binding.textViewCategoryName.setBackgroundResource(R.drawable.ic_category_bg_active)
                binding.textViewCategoryName.setTextColor(getColor(R.color.pink))
            } else {
                binding.textViewCategoryName.setBackgroundResource(R.drawable.ic_category_bg_inactive)
                binding.textViewCategoryName.setTextColor(getColor(R.color.gray))
            }

            initListener(value)
        }

        private fun initListener(item: CategoryItem){
            binding.root.setOnClickListener {
                listener?.onClickItem(item)
            }
        }

        private fun getColor(@ColorRes id: Int): Int {
            return ResourcesCompat.getColor(itemView.resources, id, itemView.context.theme)
        }

    }
}

