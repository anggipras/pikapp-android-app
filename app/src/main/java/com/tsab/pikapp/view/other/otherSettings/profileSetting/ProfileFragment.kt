package com.tsab.pikapp.view.other.otherSettings.profileSetting

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ProfileFragmentBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.profile_birthday_dialog.view.*
import kotlinx.android.synthetic.main.profile_gender_dialog.view.*
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var dataBinding: ProfileFragmentBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = ProfileFragmentBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding = ProfileFragmentBinding.bind(view)

        dataBinding.profileBirthday.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        dataBinding.profileGender.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        dataBinding.profileGender.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.openDialogTo_profileGenderFragment)
        }

        dataBinding.profileBirthday.setOnClickListener {
            val datePickerFragment = ProfileBirthdayFragment()
            val supportFragmentManager = requireActivity().supportFragmentManager

            supportFragmentManager.setFragmentResultListener(
                "REQUEST_KEY",
                viewLifecycleOwner
            ) {
                    resultKey, bundle ->
                if (resultKey == "REQUEST_KEY") {
                    val date = bundle.getString("SELECTED_DATE")
                    openBirthdayDialog(date)
                }
            }

            datePickerFragment.show(supportFragmentManager, "ProfileBirthdayFragment")
        }

        dataBinding.backButtonProfile.setOnClickListener {
            requireActivity().onBackPressed()
        }

        observeViewModel()
    }

    private fun openBirthdayDialog(date: String?) {
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.profile_birthday_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.dialog_birthday_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_birthday_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_birthday_ok.setOnClickListener {
            mAlertDialog.dismiss()
            viewModel.setBirthday(date)
        }
    }

    private fun openGenderDialog() {
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.profile_gender_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
                .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.dialog_background
                )
        )
        mDialogView.dialog_gender_back.setOnClickListener {
            mAlertDialog.dismiss()
            viewModel.restartFragment()
        }
        mDialogView.dialog_gender_close.setOnClickListener {
            mAlertDialog.dismiss()
            viewModel.restartFragment()
        }
        mDialogView.dialog_gender_ok.setOnClickListener {
            mAlertDialog.dismiss()
            dataBinding.profileGender.visibility = View.GONE
            dataBinding.profileGenderExist.text = viewModel._genderSelection.value
            dataBinding.profileGenderExist.visibility = View.VISIBLE
            viewModel.restartFragment()
        }
    }

    fun observeViewModel() {
        viewModel._genderDialogAlert.observe(viewLifecycleOwner, Observer { gender ->
            gender?.let {
                if (gender) {
                    openGenderDialog()
                } else {
                    dataBinding.profileGender.visibility = View.GONE
                    dataBinding.profileGenderExist.text = viewModel._genderSelection.value
                    dataBinding.profileGenderExist.visibility = View.VISIBLE
                }
            }
        })

        viewModel._birthdaySelection.observe(viewLifecycleOwner, Observer { date ->
            dataBinding.profileBirthday.visibility = View.GONE
            dataBinding.profileBirthdayExist.text = date
            dataBinding.profileBirthdayExist.visibility = View.VISIBLE
        })
    }

}