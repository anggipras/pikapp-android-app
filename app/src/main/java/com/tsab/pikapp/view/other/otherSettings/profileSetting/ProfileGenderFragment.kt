package com.tsab.pikapp.view.other.otherSettings.profileSetting

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProfileGenderBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class ProfileGenderFragment : BottomSheetDialogFragment() {

    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentProfileGenderBinding
    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentProfileGenderBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = parentFragment?.view?.let { Navigation.findNavController(it) }

        dataBinding.saveGenderButton.setOnClickListener {
            attachInputListeners()
        }

        dataBinding.genderCloseButton.setOnClickListener {
            navController?.navigateUp()
        }

        viewModel._genderConfirmation.observe(viewLifecycleOwner, Observer { gender ->
            gender?.let {
                if (gender) {
                    navController?.navigateUp()
                    viewModel.confirmGender(gender)
                }
            }
        })
    }

    private fun attachInputListeners() {
        val isGenderSelected = when(dataBinding.genderSelection.checkedRadioButtonId) {
            R.id.female_gender -> "Female"
            else -> "Male"
        }
        viewModel.setGender(true, isGenderSelected)
    }
}