package com.bejohen.pikapp.view.userExclusive

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentUserExclusiveFormBinding
import com.bejohen.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModel
import com.bejohen.pikapp.viewmodel.userExclusive.UserExclusiveFormViewModel

class UserExclusiveFormFragment : Fragment() {

    private lateinit var viewModel : UserExclusiveFormViewModel
    private lateinit var dataBinding: FragmentUserExclusiveFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_exclusive_form, container, false)
        viewModel = ViewModelProviders.of(this).get(UserExclusiveFormViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.buttonFormSubmit.setOnClickListener {
            viewModel.goToUserExclusiveHome(dataBinding.root)
        }
    }
}