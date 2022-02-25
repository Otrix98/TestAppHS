package com.example.testapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.databinding.FragmentMenuBinding
import com.example.testapp.ui.adapters.ListAdapter
import com.example.testapp.ui.viemodels.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class MenuFragment : ViewBindingFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate){

    private var pagingAdapter = ListAdapter()
    private val viewModel by viewModels<ListViewModel>()
    private val categoryList = listOf("Пицца", "Бургеры",  "Закуски",  "Роллы", "Десерты", "Напитки")
    private lateinit var lastView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategories()
        initList()
        bindViewModel()
        flowLoadDataToAdapter(categoryList[0])
    }

    private fun initList() {
        val itemDivider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        with(binding.recyclerView) {
            adapter = pagingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(itemDivider)
        }
    }

    private fun bindViewModel() {
        viewModel.progressBarVisible.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.serverConnectError.observe(viewLifecycleOwner) { errorText ->
            showErrorMessage(errorText)
        }
    }

    private fun flowLoadDataToAdapter(category: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.flowGetListBreweries(category)
                .catch { it.message?.let { it1 -> showErrorMessage(it1) } }
                .collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
        }
    }

    private fun showErrorMessage(errorText: String) {
        with(binding) {
            errorTextView.isVisible = true
            errorTextView.text = errorText
            retryButton.isVisible = true
        }
    }

    private fun updateList(category: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            flowLoadDataToAdapter(category)
            binding.retryButton.isVisible = false
            binding.errorTextView.isVisible = false
        }
    }

       @SuppressLint("InflateParams")
       private fun initCategories() {
           for (i in categoryList) {
               val view = LayoutInflater.from(requireContext())
                   .inflate(R.layout.item_top_category, null) as TextView
               val textView = view.findViewById<TextView>(R.id.categoryItem)
               textView.text = i
               if (i == categoryList[0]) {
                   tintClicked(textView); lastView = view
               }
               textView.setOnClickListener {
                   onClickCategory(it as TextView)
               }
               binding.categoriesLayout.addView(view)
           }

           binding.retryButton.setOnClickListener {updateList(lastView.text.toString())}
       }

    private fun onClickCategory(view: TextView) {
        returnBack(lastView)
        tintClicked(view)
        lastView = view
        updateList(view.text.toString())
    }

    private fun returnBack(textView: TextView) {
        textView.setTextColor(requireContext().getColor(R.color.gray_tint))
        textView.backgroundTintList =
            requireContext().resources.getColorStateList(
                R.color.poor_white,
                requireContext().theme)
    }

    private fun tintClicked(textView: TextView) {
        textView.setTextColor(requireContext().getColor(R.color.pink))
        textView.backgroundTintList =
            requireContext().resources.getColorStateList(
                R.color.backgroundClicked,
                requireContext().theme
            )
    }
}