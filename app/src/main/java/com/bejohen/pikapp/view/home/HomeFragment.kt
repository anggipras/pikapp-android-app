package com.bejohen.pikapp.view.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentHomeBinding
import com.bejohen.pikapp.util.LOCATION_REQUEST
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

        checkLocationPermission()

        dataBinding.tabLayout.visibility = View.GONE
        dataBinding.layoutHomeContainer.visibility = View.GONE
        TabLayoutMediator(dataBinding.tabLayout, view.homeViewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Home"
                tab.setIcon(R.drawable.ic_home)
            } else {
                tab.text = "Live Chat"
                tab.setIcon(R.drawable.ic_chat)
            }
            view.homeViewPager.setCurrentItem(tab.position, true)
        }.attach()

        dataBinding.buttonProfile.setOnClickListener {
            viewModel.checkUserLogin(activity as HomeActivity)
        }

        dataBinding.buttonAccessLocation.setOnClickListener {
            requestPermission()
        }

        observeViewModel(view)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel(view: View) {
        viewModel.isLocationEnabled.observe(this, Observer { it ->
            if (it) {
                dataBinding.layoutLocationPermission.visibility = View.GONE
                viewModel.getUserLocation(activity as HomeActivity)
                dataBinding.tabLayout.visibility = View.VISIBLE
                dataBinding.layoutHomeContainer.visibility = View.VISIBLE
                viewModel.checkDeeplink()
            } else {
                dataBinding.tabLayout.visibility = View.GONE
                dataBinding.layoutHomeContainer.visibility = View.GONE
                dataBinding.layoutLocationPermission.visibility = View.VISIBLE
            }
        })

        viewModel.isDeeplinkEnabled.observe(this, Observer { it ->
            if (it) {
                viewModel.goToMerchant(dataBinding.root)
            } else {
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                viewModel.setStatusLocation(false)
            } else {
                viewModel.setStatusLocation(true)
            }
        }
    }

    fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.setStatusLocation(false)
            requestPermission()
        } else {
            viewModel.setStatusLocation(true)
        }
    }
    fun requestPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_REQUEST
        )
    }
}