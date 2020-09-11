package com.bejohen.pikapp.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentHomeBinding
import com.bejohen.pikapp.databinding.FragmentHomeHomeBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.viewmodel.home.HomeViewModel
import com.bejohen.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home_view_pager.view.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var dataBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        viewModel.saveUserLocation("106.634157", "-6.234916")
        TabLayoutMediator(dataBinding.tabLayout, view.homeViewPager) { tab, position ->
            if(position == 0) {
                tab.text = "Home"
                tab.setIcon(R.drawable.ic_home)
            } else {
                tab.text = "Live Chat"
                tab.setIcon(R.drawable.ic_chat)
            }
            view.homeViewPager.setCurrentItem(tab.position, true)
        }.attach()

        dataBinding.buttonProfile.setOnClickListener{

            viewModel.checkUserLogin(activity as HomeActivity)

        }

    }
}