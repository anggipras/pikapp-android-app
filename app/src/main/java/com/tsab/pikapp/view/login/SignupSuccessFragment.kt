package com.tsab.pikapp.view.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingSuccessViewModel
import kotlinx.android.synthetic.main.fragment_signup_onboarding_success.view.*

class SignupSuccessFragment : Fragment() {

    private lateinit var viewModel: SignupOnboardingSuccessViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_success, container, false)

        viewModel = ViewModelProviders.of(this).get(SignupOnboardingSuccessViewModel::class.java)

        view.buttonBack.setOnClickListener {
            viewModel.backToLogin(view)
        }

        return view
    }
}