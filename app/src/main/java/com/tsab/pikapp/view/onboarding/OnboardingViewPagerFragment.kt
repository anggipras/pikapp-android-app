package com.tsab.pikapp.view.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentOnboardingViewPagerBinding
import com.tsab.pikapp.view.onboarding.screens.OnboardingSecond
import com.tsab.pikapp.view.onboarding.screens.OnboardingStartFragment
import com.tsab.pikapp.view.onboarding.screens.OnboardingThird

class OnboardingViewPagerFragment : Fragment() {

    private lateinit var dataBinding: FragmentOnboardingViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_onboarding_view_pager,
            container,
            false
        )

        val fragmentList = arrayListOf<Fragment>(
            OnboardingStartFragment(),
            OnboardingSecond(),
            OnboardingThird()
        )

        val adapter = OnboardingViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        dataBinding.viewPager.adapter = adapter

        TabLayoutMediator(dataBinding.tabLayout, dataBinding.viewPager) { tab, position ->
            if (position == 0) {
                tab.setIcon(R.drawable.tab_selector_blue)
            } else if (position == 1) {
                tab.setIcon(R.drawable.tab_selector_blue)
            } else if (position == 2) {
                tab.setIcon(R.drawable.tab_selector)
            }
        }.attach()

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (dataBinding.viewPager.currentItem == 2) {
                    dataBinding.buttonOnboardingNext.visibility = View.GONE
                } else {
                    dataBinding.buttonOnboardingNext.visibility = View.VISIBLE
                }
                super.onPageSelected(position)
            }
        })
        dataBinding.buttonOnboardingNext.setOnClickListener {
            dataBinding.viewPager.currentItem += 1
        }
    }

    override fun onResume() {
        super.onResume()
        if (dataBinding.viewPager.currentItem == 2) {
            dataBinding.buttonOnboardingNext.visibility = View.GONE
        }
    }

}