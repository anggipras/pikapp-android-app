package com.tsab.pikapp.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProfileBinding
import com.tsab.pikapp.util.getInitial
import com.tsab.pikapp.util.substringPhone
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.viewmodel.home.HomeProfileViewModel

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

        viewModel.validateUser()
        dataBinding.containerView.visibility = View.INVISIBLE

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

        dataBinding.buttonPrivasi.setOnClickListener {
            val url = "https://pikapp.id/kebijakan-privasi/"
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(url)
            (activity as HomeActivity).startActivity(intent)
        }

        dataBinding.buttonLogout.setOnClickListener {
            viewModel.logout(activity as HomeActivity)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve", "ResourceAsColor")
    private fun observeViewModel() {

        viewModel.loading.observe(this, Observer { it ->
            if (it) {
                dataBinding.loadingView.visibility = View.VISIBLE
            } else {
                dataBinding.loadingView.visibility = View.GONE
            }
        })

        viewModel.userSuccess.observe(this, Observer {
            if (it) {
                viewModel.getUserData()
            }
        })

        viewModel.errorResponse.observe(this, Observer {
            if (it.errCode == "EC0021") {
                Toast.makeText(
                    activity as HomeActivity,
                    "Kamu login di perangkat lain. Silakan login kembali",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearSession(activity as HomeActivity)
                viewModel.goToOnboardingFromHome(activity as HomeActivity)
            }
        })

        viewModel.userData.observe(this, Observer { t ->
            Log.d("Debug", "$t")
            dataBinding.textViewUsername.apply {
                text = t.customerName
            }
            dataBinding.textViewPhone.apply {
                text = substringPhone(t.phoneNumber)
            }
            dataBinding.textViewInitial.apply {
                text = getInitial(t.customerName.toString())
            }

            if (t.userLevel == "LEVEL_EXCLUSIVE") {
                dataBinding.textViewGrade.apply {
                    text = "Exclusive User"
                    setBackgroundResource(R.drawable.button_gold)
                }
            }

            dataBinding.containerView.visibility = View.VISIBLE
        })

        viewModel.logoutResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.clearSession(activity as HomeActivity)
                viewModel.goToOnboardingFromHome(activity as HomeActivity)
            }
        })
    }

}