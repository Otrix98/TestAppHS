package com.example.testapp.ui.adapters

import com.example.testapp.data.models.CategoryItem
import com.example.testapp.data.models.MenuItem


interface AdaptersListener {
    fun onClickItem(item: CategoryItem)
}