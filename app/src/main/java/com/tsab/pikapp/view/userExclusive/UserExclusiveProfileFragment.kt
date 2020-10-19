package com.tsab.pikapp.view.userExclusive

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentUserExclusiveProfileBinding
import com.tsab.pikapp.util.getInitial
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.viewmodel.userExclusive.UserExclusiveProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UserExclusiveProfileFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: UserExclusiveProfileViewModel
    private lateinit var dataBinding: FragmentUserExclusiveProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_user_exclusive_profile,
                container,
                false
            )
        viewModel = ViewModelProviders.of(this).get(UserExclusiveProfileViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserData()

        dataBinding.buttonUELogout.setOnClickListener {
            viewModel.logout(activity as UserExclusiveActivity)
        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.userData.observe(this, Observer { t ->
            dataBinding.textViewUEUsername.apply {
                text = t.customerName
            }
            dataBinding.textViewEUInitial.apply {
                text = getInitial(t.customerName.toString())
            }
        })

        viewModel.logoutResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.clearSessionExclusive(activity as UserExclusiveActivity)
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingViewUEProfile.visibility = if (it) View.VISIBLE else View.GONE
                dataBinding.buttonUELogout.isClickable = !it
            }
        })

        viewModel.errorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })
    }
}