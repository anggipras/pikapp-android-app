package com.bejohen.pikapp.view.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentLoginOnboardingBinding
import com.bejohen.pikapp.databinding.FragmentOnboardingThirdBinding
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.onboarding.OnboardingViewPagerFragmentDirections
import com.bejohen.pikapp.viewmodel.onboarding.OnboardingThirdViewModel
import kotlinx.android.synthetic.main.fragment_onboarding_third.view.*

class OnboardingThird : Fragment() {

    private lateinit var dataBinding: FragmentOnboardingThirdBinding
    private lateinit var viewModel: OnboardingThirdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_third, container, false)
        viewModel = ViewModelProviders.of(this).get(OnboardingThirdViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getOnboardingFinished()) {
            dataBinding.textSkipToHome.visibility = View.INVISIBLE
        }

        dataBinding.buttonLogin.setOnClickListener {
            viewModel.goToLogin(view)
        }

        dataBinding.buttonSignup.setOnClickListener {
            viewModel.goToSignup(view)
        }

        dataBinding.textSkipToHome.setOnClickListener {
            val homeActivity = Intent(activity as OnboardingActivity, HomeActivity::class.java)
            (activity as OnboardingActivity).startActivity(homeActivity)
            viewModel.setOnboardingFinished(true)
            (activity as OnboardingActivity).finish()
        }
    }
}