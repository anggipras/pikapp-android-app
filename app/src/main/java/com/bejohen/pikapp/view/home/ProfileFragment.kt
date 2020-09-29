package com.bejohen.pikapp.view.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentProfileBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.StoreActivity
import com.bejohen.pikapp.view.UserExclusiveActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileFragment : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.buttonToStore.setOnClickListener {
            val storeActivity = Intent(context, StoreActivity::class.java)
            (activity as HomeActivity).startActivity(storeActivity)
        }
        dataBinding.buttonLogout.setOnClickListener {

        }
    }

}