package com.bejohen.pikapp.view.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bejohen.pikapp.databinding.FragmentOnboardingThirdBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.onboarding.ViewPagerFragmentDirections
import com.bejohen.pikapp.viewmodel.SplashViewModel
import com.bejohen.pikapp.viewmodel.onboarding.OnboardingThirdViewModel
import kotlinx.android.synthetic.main.fragment_onboarding_third.view.*

class OnboardingThird : Fragment() {

    private var _binding: FragmentOnboardingThirdBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: OnboardingThirdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOnboardingThirdBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProviders.of(this).get(OnboardingThirdViewModel::class.java)

        view.buttonLogin.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionToLoginOnboardingFragment()
            Navigation.findNavController(view).navigate(action)
        }

        view.buttonSignup.setOnClickListener {
            val action = ViewPagerFragmentDirections.actionToSignupOnboardingFragment()
            Navigation.findNavController(view).navigate(action)
        }

        view.textSkipToHome.setOnClickListener {
            val homeActivity = Intent(activity as OnboardingActivity, HomeActivity::class.java)
            (activity as OnboardingActivity).startActivity(homeActivity)
            viewModel.setOnboardingFinished(true)
            (activity as OnboardingActivity).finish()
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}