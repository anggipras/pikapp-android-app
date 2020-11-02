package com.tsab.pikapp.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProfileBinding
import com.tsab.pikapp.util.getInitial
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.viewmodel.home.HomeProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: HomeProfileViewModel
    private lateinit var dataBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProviders.of(this).get(HomeProfileViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserData()

        dataBinding.buttonBelomBayar.setOnClickListener {
            viewModel.goToOrderList(activity as HomeActivity, 0)
        }

        dataBinding.buttonDisiapkan.setOnClickListener {
            viewModel.goToOrderList(activity as HomeActivity, 1)
        }

        dataBinding.buttonSiapDikirim.setOnClickListener {
            viewModel.goToOrderList(activity as HomeActivity, 2)
        }

        dataBinding.buttonBeriPenilaian.setOnClickListener {
            viewModel.goToOrderList(activity as HomeActivity, 3)
        }

        dataBinding.buttonToStore.setOnClickListener {
            viewModel.goToStoreHome(activity as HomeActivity)
        }

        dataBinding.buttonLogout.setOnClickListener {
            viewModel.logout(activity as HomeActivity)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.loading.observe(this, Observer { it ->
            if(it) dataBinding.loadingView.visibility = View.VISIBLE else dataBinding.loadingView.visibility = View.GONE
        })

        viewModel.userData.observe(this, Observer { t ->
            Log.d("Debug", "$t")
            dataBinding.textViewUsername.apply {
                text = t.customerName
            }
            dataBinding.textViewInitial.apply {
                text = getInitial(t.customerName.toString())
            }
        })

        viewModel.logoutResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.clearSession(activity as HomeActivity)
                viewModel.goToOnboardingFromHome(activity as HomeActivity)
            }
        })
    }

}