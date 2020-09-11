package com.bejohen.pikapp.view.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bejohen.pikapp.BuildConfig
import com.bejohen.pikapp.R
import kotlinx.android.synthetic.main.fragment_onboarding_first.view.*

class OnboardingFirst : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_first, container, false)
        return view
    }
}