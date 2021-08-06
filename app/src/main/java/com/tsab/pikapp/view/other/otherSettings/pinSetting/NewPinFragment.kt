package com.tsab.pikapp.view.other.otherSettings.pinSetting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentNewPinBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class NewPinFragment : Fragment() {

    private lateinit var dataBinding: FragmentNewPinBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentNewPinBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showKeyboard()

        dataBinding.newPinInput.addTextChangedListener {
            val newPin = dataBinding.newPinInput.text.toString()
            if (newPin.length == 6) {
                hideKeyboard()
                viewModel.setNewPin(newPin)
                Navigation.findNavController(view).navigate(R.id.navigateTo_confirmPinFragment)
                dataBinding.newPinInput.setText("")
            }
        }

        dataBinding.backButtonPinSecond.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
            Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

    private fun showKeyboard() {
        dataBinding.newPinInput.requestFocus()
        val imgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(dataBinding.newPinInput, InputMethodManager.SHOW_IMPLICIT)
    }
}