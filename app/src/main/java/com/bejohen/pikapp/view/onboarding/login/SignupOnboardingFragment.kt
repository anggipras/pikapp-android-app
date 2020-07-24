package com.bejohen.pikapp.view.onboarding.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bejohen.pikapp.R

class SignupOnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup_onboarding, container, false)
//        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_onboarding, container, false)
//        return dataBinding.root
    }
}