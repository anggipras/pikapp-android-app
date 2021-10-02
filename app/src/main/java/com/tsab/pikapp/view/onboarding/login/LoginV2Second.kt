package com.tsab.pikapp.view.onboarding.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentLoginV2SecondBinding
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModelV2

class LoginV2Second : Fragment() {

    private lateinit var viewModel: LoginOnboardingViewModelV2
    private lateinit var dataBinding: FragmentLoginV2SecondBinding
    lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = requireArguments().getString("email").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login_v2_second,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(LoginOnboardingViewModelV2::class.java)

        dataBinding.logPin.addTextChangedListener {
            val email = email
            val password = dataBinding.logPin.text.toString()

            if (password.length == 6) {
                hideKeyboard()
            }

            viewModel.login(email, password)
        }

//        dataBinding.logPin.setPinViewEventListener { pinview, fromUser ->
//            val email = email
//            val password = dataBinding.logPin.value.toString()
//
//            viewModel.login(email, password)
//        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                activity?.startActivity(intent)
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            if (isLoading) hideKeyboard()
            dataBinding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}