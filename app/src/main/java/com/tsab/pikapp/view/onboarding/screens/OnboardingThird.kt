package com.tsab.pikapp.view.onboarding.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentOnboardingThirdBinding
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.viewmodel.onboarding.OnboardingThirdViewModel

class OnboardingThird : Fragment() {

    private lateinit var dataBinding: FragmentOnboardingThirdBinding
    private lateinit var viewModel: OnboardingThirdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_third, container, false)
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

        dataBinding.buttonSK.setOnClickListener {
            val url = "https://pikapp.id/syarat-dan-ketentuan/"
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            (activity as OnboardingActivity).startActivity(intent)
        }

        dataBinding.buttonPrivacyPolicy.setOnClickListener {
            val url = "https://pikapp.id/kebijakan-privasi/"
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            (activity as OnboardingActivity).startActivity(intent)
        }
    }
}