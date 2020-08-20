package com.bejohen.pikapp.view.onboarding.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentLoginOnboardingBinding
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModel

class LoginOnboardingFragment : Fragment() {

    private lateinit var viewModel: LoginOnboardingViewModel
    private lateinit var dataBinding: FragmentLoginOnboardingBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_login_onboarding, container, false)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_onboarding, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(LoginOnboardingViewModel::class.java)

        dataBinding.buttonLogin.setOnClickListener{
            if (viewModel.getOnboardingFinished()) {
                (activity as LoginActivity).hideKeyboard()
            } else {
                (activity as OnboardingActivity).hideKeyboard()
            }
            val email = dataBinding.textFieldLogin.text.trim().toString()
            val password = dataBinding.textFieldPassword.text.trim().toString()
            Log.d("Debug","email : " + email)
            viewModel.login(email, password)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.emailError.observe(this, Observer { t ->
            dataBinding.textErrorEmail.apply {
                visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                text = t
            }
        })

        viewModel.passwordError.observe(this, Observer { t ->
            dataBinding.textErrorPassword.apply {
                visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                text = t
            }
        })

        viewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                dataBinding.buttonLogin.isClickable = false

                Log.d("debug", "nama : ${response.newEvent!!}")
                if(response.newEvent!!) {
                    viewModel.goToUserExclusive(activity as OnboardingActivity)
                }
                else {
                    if (viewModel.getOnboardingFinished()) {
                        viewModel.goToHome(activity as LoginActivity)
                    } else {
                        viewModel.goToHome(activity as OnboardingActivity)
                    }
                }
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingViewLogin.visibility = if(it) View.VISIBLE else View.GONE
                dataBinding.buttonLogin.isClickable = !it

            }
        })

        viewModel.loginErrorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { it1 -> viewModel.createToast(it1) }
                Log.d("debug", "errorrrr : ${errorResponse.errCode}, ${errorResponse.errMessage}")
            }
        })
    }
}