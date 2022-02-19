package com.example.testapp.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.models.RemoteRepository
import com.example.testapp.databinding.FragmentListBinding
import com.example.testapp.ui.adapters.AdaptersListener
import com.example.testapp.ui.adapters.ListAdapter
import com.example.testapp.ui.adapters.RepoComparator
import com.example.testapp.ui.adapters.RepoLoadStateAdapter
import com.example.testapp.ui.viemodels.ListViewModel
import com.example.testapp.ui.viemodels.UiAction
import com.example.testapp.ui.viemodels.UiState
import com.example.testapp.utils.isInternetAvailable
import com.example.testapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class ListFragment : ViewBindingFragment<FragmentListBinding>(FragmentListBinding::inflate),
    AdaptersListener {

    private val viewModel by viewModels<ListViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isInternetAvailable(requireContext())) {
            toast(R.string.internet_toast)
        }
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
    }

    override fun onClickItem(repo: RemoteRepository) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment(repo)
        findNavController().navigate(action)
    }

    private fun FragmentListBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<RemoteRepository>>,
        uiActions: (UiAction) -> Unit
    ) {
        val repoAdapter = ListAdapter(
            RepoComparator
        )
        repoAdapter.setOnClickListener(this@ListFragment)
        val header = RepoLoadStateAdapter { repoAdapter.retry() }
        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = repoAdapter.withLoadStateHeaderAndFooter(
                header = header,
                footer = RepoLoadStateAdapter { repoAdapter.retry() }
            )
        }
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            feedAdapter = repoAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
        updatePhotoListFromInput(uiActions)
    }

    private fun FragmentListBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchPhotoEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchPhotoEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatePhotoListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchPhotoEditText::setText)
        }
    }

    private fun FragmentListBinding.updatePhotoListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchPhotoEditText.text.trim().let {
            if (it.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun FragmentListBinding.bindList(
        header: RepoLoadStateAdapter,
        feedAdapter: ListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<RemoteRepository>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { feedAdapter.retry() }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = feedAdapter.loadStateFlow
            .distinctUntilChangedBy { it.source.refresh }
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(feedAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerView.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            feedAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && feedAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty = loadState.refresh is LoadState.NotLoading && feedAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                recyclerView.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && feedAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}