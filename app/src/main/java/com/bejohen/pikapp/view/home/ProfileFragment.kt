package com.bejohen.pikapp.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentProfileBinding
import com.bejohen.pikapp.util.getInitial
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.StoreActivity
import com.bejohen.pikapp.viewmodel.home.HomeProfileViewModel
import com.bejohen.pikapp.viewmodel.userExclusive.UserExclusiveProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: HomeProfileViewModel
    private lateinit var dataBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = ViewModelProviders.of(this).get(HomeProfileViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserData()

        dataBinding.buttonToStore.setOnClickListener {
            val storeActivity = Intent(context, StoreActivity::class.java)
            (activity as HomeActivity).startActivity(storeActivity)
        }

        dataBinding.buttonLogout.setOnClickListener {
            viewModel.logout(activity as HomeActivity)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
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
            }
        })
    }

}