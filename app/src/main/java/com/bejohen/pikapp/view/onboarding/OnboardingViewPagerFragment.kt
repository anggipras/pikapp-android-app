package com.bejohen.pikapp.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bejohen.pikapp.R
import com.bejohen.pikapp.view.onboarding.screens.OnboardingFirst
import com.bejohen.pikapp.view.onboarding.screens.OnboardingSecond
import com.bejohen.pikapp.view.onboarding.screens.OnboardingThird
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_onboarding_view_pager.view.*
import kotlinx.android.synthetic.main.fragment_onboarding_view_pager.view.viewPager

class OnboardingViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            OnboardingFirst(),
            OnboardingSecond(),
            OnboardingThird()
        )

        val adapter = OnboardingViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle )
        view.viewPager.adapter = adapter

        TabLayoutMediator(view.tabLayout, view.viewPager)   {
            tab, position ->
        }.attach()

        return view
    }
}