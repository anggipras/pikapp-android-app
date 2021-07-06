package com.tsab.pikapp.view.onboarding.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.onboarding.login.SignupOnboardingSuccessViewModel
import kotlinx.android.synthetic.main.fragment_signup_onboarding_success.view.*

class SignupOnboardingSuccessFragment : Fragment() {

    private lateinit var viewModel: SignupOnboardingSuccessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_onboarding_success, container, false)

        viewModel = ViewModelProviders.of(this).get(SignupOnboardingSuccessViewModel::class.java)

        view.buttonBack.setOnClickListener {
            viewModel.backToLogin(view)
        }

        return view
    }
}