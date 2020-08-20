package com.bejohen.pikapp.view.onboarding

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentOnboardingViewPagerBinding
import com.bejohen.pikapp.view.onboarding.screens.OnboardingFirst
import com.bejohen.pikapp.view.onboarding.screens.OnboardingSecond
import com.bejohen.pikapp.view.onboarding.screens.OnboardingThird
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_onboarding_view_pager.*

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
            OnboardingFirst(),
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
                    dataBinding.tabLayout.selectedTabPosition
                } else {
                    dataBinding.buttonOnboardingNext.visibility = View.VISIBLE
                    dataBinding.tabLayout.setSelectedTabIndicator(R.drawable.tab_selector_blue)
                }
                super.onPageSelected(position)
            }
        })
        dataBinding.buttonOnboardingNext.setOnClickListener {
            dataBinding.viewPager.currentItem += 1
//            if (dataBinding.viewPager.currentItem == 2) {
//                dataBinding.buttonOnboardingNext.visibility = View.GONE
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (dataBinding.viewPager.currentItem == 2) {
            dataBinding.buttonOnboardingNext.visibility = View.GONE
        }
    }

}