package com.example.testapp.ui.adapters

import com.example.testapp.data.models.RemoteRepository

interface AdaptersListener {
    fun onClickItem(repo: RemoteRepository)
}