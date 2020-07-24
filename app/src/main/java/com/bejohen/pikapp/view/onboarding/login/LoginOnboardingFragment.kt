package com.bejohen.pikapp.view.onboarding.login

import android.annotation.SuppressLint
import android.content.Intent
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
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModel
import kotlinx.android.synthetic.main.fragment_login_onboarding.*

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

            val email = dataBinding.textFieldLogin.text.trim().toString()
            val password = dataBinding.textFieldPassword.text.trim().toString()
            Log.d("Debug","email : " + email)
            viewModel.login(email, password)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.emailValid.observe(this, Observer { isValid ->
            isValid?.let {
                if (!it) {
                    dataBinding.textErrorEmail.visibility = View.VISIBLE
                    if(viewModel.loginLoadError.value == true) {
                        dataBinding.textErrorEmail.text = "User not found"
                    } else {
                        dataBinding.textErrorEmail.text = "Please input your valid email address"
                    }
                } else {
                    dataBinding.textErrorEmail.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.passwordValid.observe(this, Observer { isValid ->
            isValid?.let {
                if(!it) {
                    dataBinding.textErrorPassword.visibility = View.VISIBLE
                    dataBinding.textErrorPassword.text = "Please input your valid password"
                } else {
                    dataBinding.textErrorPassword.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.loginResponse.observe(this, Observer { response ->
            response?.let {
                dataBinding.buttonLogin.isClickable = false
                val homeActivity = Intent(context, HomeActivity::class.java)
                context?.startActivity(homeActivity)
                (activity as OnboardingActivity).finish()
            }
        })

        viewModel.loginLoadError.observe(this, Observer { isError ->
            isError?.let {
                dataBinding.loadingView.visibility = View.GONE
//                loginError.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
                dataBinding.textErrorEmail.visibility = View.INVISIBLE
                dataBinding.textErrorPassword.visibility = View.INVISIBLE
            }
        })
    }

}