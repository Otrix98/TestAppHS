package com.example.testapp.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.testapp.ui.ViewBindingFragment
import com.example.testapp.R
import com.example.testapp.data.AccessToken
import com.example.testapp.databinding.FragmentAuthBinding
import com.example.testapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class AuthFragment : ViewBindingFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate){

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (checkToken()) {
            val action = AuthFragmentDirections.actionAuthFragmentToListFragment()
            findNavController().navigate(action)
        } else {
            bindViewModel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTH_REQUEST_CODE && data != null) {
            val tokenExchangeRequest = AuthorizationResponse.fromIntent(data)
                ?.createTokenExchangeRequest()
            val exception = AuthorizationException.fromIntent(data)
            when {
                tokenExchangeRequest != null && exception == null ->
                    viewModel.onAuthCodeReceived(tokenExchangeRequest)
                exception != null -> viewModel.onAuthCodeFailed(exception)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindViewModel() {
        binding.loginButton.setOnClickListener { viewModel.openLoginPage() }
        viewModel.loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
        viewModel.openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
        viewModel.toastLiveData.observe(viewLifecycleOwner, ::toast)
        viewModel.authSuccessLiveData.observe(viewLifecycleOwner) {
            findNavController().navigate(AuthFragmentDirections.actionAuthFragmentToListFragment())
        }
    }

    private fun checkToken(): Boolean {
        val token = context?.getSharedPreferences(AccessToken.TOKEN_PREFERENCES, Context.MODE_PRIVATE)
            ?.getString(AccessToken.TOKEN_STRING, null)
        return if (token != null) {
            Log.d("TOKEN", "Token ---> $token")
            AccessToken.value = token
            true
        } else false
    }

    private fun updateIsLoading(isLoading: Boolean) {
        binding.loginButton.isVisible = !isLoading
        binding.loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) {
        getResult.launch(intent)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                val tokenExchangeRequest = AuthorizationResponse.fromIntent(it.data!!)
                    ?.createTokenExchangeRequest()
                val exception = AuthorizationException.fromIntent(it.data)
                when {
                    tokenExchangeRequest != null && exception == null ->
                        viewModel.onAuthCodeReceived(tokenExchangeRequest)
                    exception != null -> viewModel.onAuthCodeFailed(exception)
                }
            } else {
                toast(R.string.auth_canceled)
            }
        }

    companion object {
        private const val AUTH_REQUEST_CODE = 342
    }
}