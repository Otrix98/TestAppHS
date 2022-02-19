package com.example.scopedstorage.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<T: ViewBinding>(
    private val inflateBinding: (
    inflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean) -> T
): Fragment() {


    private var fragmentBinding: T? = null

    val binding get() = fragmentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = inflateBinding.invoke(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentBinding = null
    }
}