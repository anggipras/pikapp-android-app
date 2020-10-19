package com.tsab.pikapp.view.home

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
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentHomeBinding
import com.tsab.pikapp.util.LOCATION_REQUEST
import com.tsab.pikapp.util.getSignature
import com.tsab.pikapp.util.getTimestamp
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.viewmodel.home.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home_view_pager.view.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var dataBinding: FragmentHomeBinding

    private var isFirstTime = true

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

        val timestamp = getTimestamp()
        val signature = getSignature("superindo@mailinator.com", timestamp)

        Log.d("Debug", "timestamp : $timestamp")
        Log.d("Debug", "signature : $signature")

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
            viewModel.goToProfile(activity as HomeActivity)
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
                viewModel.getUserLocation(activity as HomeActivity)
//                dataBinding.tabLayout.visibility = View.VISIBLE
                dataBinding.layoutHomeContainer.visibility = View.VISIBLE
                dataBinding.layoutLocationPermission.visibility = View.GONE
            } else {
                dataBinding.tabLayout.visibility = View.GONE
                dataBinding.layoutHomeContainer.visibility = View.GONE
                dataBinding.layoutLocationPermission.visibility = View.VISIBLE
            }
        })

        viewModel.isLocationRetrieved.observe(this, Observer {
            if(it) {
                viewModel.checkDeeplink()
                viewModel.checkUserMerchantStatus()
            }
        })

        viewModel.isDeeplinkEnabled.observe(this, Observer {
            if (it) {
                viewModel.goToMerchant(dataBinding.root)
            } else {
            }

        })

        viewModel.isUserMerchant.observe(this, Observer {
            if (it && isFirstTime) {
                isFirstTime = false
                viewModel.goToStoreHome(activity as HomeActivity)
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

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            viewModel.setStatusLocation(false)
            requestPermission()
        } else {
            viewModel.setStatusLocation(true)
        }
    }
    fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST
        )
    }
}