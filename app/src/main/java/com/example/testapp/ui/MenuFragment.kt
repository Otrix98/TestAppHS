package com.example.testapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.models.CategoryItem
import com.example.testapp.data.models.MenuItem
import com.example.testapp.databinding.FragmentMenuBinding
import com.example.testapp.ui.adapters.AdaptersListener
import com.example.testapp.ui.adapters.CategoriesAdapter
import com.example.testapp.ui.adapters.ListAdapter
import com.example.testapp.ui.adapters.LoadStateAdapter
import com.example.testapp.ui.viemodels.ListViewModel
import com.example.testapp.ui.viemodels.UiAction
import com.example.testapp.ui.viemodels.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class MenuFragment : ViewBindingFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate), AdaptersListener {

    private lateinit var categoriesAdapter: CategoriesAdapter
    private val viewModel by viewModels<ListViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategories()
        val itemDivider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(itemDivider)
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
    }

    override fun onClickItem(item: CategoryItem) {
        viewModel.updateCategory(item.nameEng)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initCategories() {
        categoriesAdapter = CategoriesAdapter()
        categoriesAdapter.setOnClickListener(this)
        with(binding.categoriesRecyclerView) {
            adapter = categoriesAdapter
        }
        viewModel.categoriesList.observe(viewLifecycleOwner) {categoriesAdapter.categoryList = it
        categoriesAdapter.notifyDataSetChanged()}
    }

    private fun FragmentMenuBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<MenuItem>>,
        uiActions: (UiAction) -> Unit
    ) {
        val menuAdapter = ListAdapter()
        val header = LoadStateAdapter { menuAdapter.retry() }
        recyclerView.adapter = menuAdapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = LoadStateAdapter { menuAdapter.retry() }
        )
        bindSearch(
            onQueryChanged = uiActions
        )
        bindList(
            header = header,
            menuAdapter = menuAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentMenuBinding.bindSearch(
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        viewModel.categoriesList.observe(viewLifecycleOwner) {updateListFromInput(onQueryChanged)}
    }

    private fun FragmentMenuBinding.updateListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        viewModel.categoriesList.value?.forEach {
            if (it.isPressed) {
                recyclerView.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.nameEng))
            }
        }
    }

    private fun FragmentMenuBinding.bindList(
        header: LoadStateAdapter,
        menuAdapter: ListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<MenuItem>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { menuAdapter.retry() }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = menuAdapter.loadStateFlow
            .asRemotePresentationState()
            .map { it == RemotePresentationState.PRESENTED }

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
            pagingData.collectLatest {
                menuAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerView.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            menuAdapter.loadStateFlow.collect { loadState ->
                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && menuAdapter.itemCount > 0 }
                    ?: loadState.prepend

                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && menuAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                recyclerView.isVisible =
                    loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryButton.isVisible =
                    loadState.mediator?.refresh is LoadState.Error && menuAdapter.itemCount == 0
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
