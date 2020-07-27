package com.bejohen.pikapp.view.onboarding.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bejohen.pikapp.R
import kotlinx.android.synthetic.main.fragment_signup_onboarding_success.view.*

class SignupOnboardingSuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup_onboarding_success, container, false)

        view.buttonBack.setOnClickListener {
            val action = SignupOnboardingSuccessFragmentDirections.actionBackToLoginFragment()
            Navigation.findNavController(view).navigate(action)
        }

        return view
    }
}