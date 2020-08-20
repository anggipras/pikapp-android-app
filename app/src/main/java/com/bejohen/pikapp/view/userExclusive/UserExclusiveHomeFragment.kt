package com.bejohen.pikapp.view.userExclusive

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentUserExclusiveHomeBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.UserExclusiveActivity
import com.bejohen.pikapp.viewmodel.userExclusive.UserExclusiveHomeViewModel

class UserExclusiveHomeFragment : Fragment() {

    private lateinit var viewModel : UserExclusiveHomeViewModel
    private lateinit var dataBinding: FragmentUserExclusiveHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_exclusive_home, container, false)
        viewModel = ViewModelProviders.of(this).get(UserExclusiveHomeViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserData()

        dataBinding.buttonProfile.setOnClickListener {
            viewModel.goToProfile(activity as UserExclusiveActivity)
        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.userData.observe(this, Observer { t ->
            dataBinding.textUsername.apply {
                text = t.customerName
            }
        })
    }
}