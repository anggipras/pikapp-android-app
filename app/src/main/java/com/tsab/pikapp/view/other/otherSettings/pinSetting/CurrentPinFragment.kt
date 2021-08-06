package com.tsab.pikapp.view.other.otherSettings.pinSetting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCurrentPinBinding


class CurrentPinFragment : Fragment() {

    private lateinit var dataBinding: FragmentCurrentPinBinding
    private val currentMockedPin = 123456

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentCurrentPinBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showKeyboard()

        dataBinding.currentPinInput.addTextChangedListener {
            val getCurrentPin = dataBinding.currentPinInput.text.toString()
            if (getCurrentPin.length == 6) {
                hideKeyboard()
                if (getCurrentPin == currentMockedPin.toString()) {
                    Navigation.findNavController(view).navigate(R.id.navigateTo_newPinFragment)
                    dataBinding.currentPinInput.setText("")
                } else {
                    dataBinding.currentPinInput.setText("")
                    showKeyboard()
                }
            }
        }

        dataBinding.backButtonPinFirst.setOnClickListener {
            requireActivity().onBackPressed()
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

    private fun showKeyboard() {
        dataBinding.currentPinInput.requestFocus()
        val imgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(dataBinding.currentPinInput, InputMethodManager.SHOW_IMPLICIT)
    }
}