package com.tsab.pikapp.view.other.otherSettings.bankSetting

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDataBankBinding
import com.tsab.pikapp.databinding.FragmentPinBankBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class PinBankFragment : Fragment() {

    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentPinBankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pin_bank, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.newPinInput.addTextChangedListener {
            val getCurrentPin = dataBinding.newPinInput.text.toString()
            if (getCurrentPin.length == 6) {
                hideKeyboard()
                viewModel.setCurrentPin(getCurrentPin)
                Navigation.findNavController(view).navigate(R.id.action_pinBankFragment_to_editRekFragment)
            }
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
                Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }

}



