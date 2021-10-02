package com.tsab.pikapp.view.other.otherSettings.pinSetting

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentConfirmPinBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class ConfirmPinFragment : Fragment() {
    private lateinit var dataBinding: FragmentConfirmPinBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentConfirmPinBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard()

        dataBinding.confirmPinInput.addTextChangedListener {
            val confirmPin = dataBinding.confirmPinInput.text.toString()
            if (confirmPin.length == 6) {
                hideKeyboard()
                if (confirmPin == viewModel._newPin.value) {
                    viewModel.changePin()
                } else {
                    Toast.makeText(requireActivity(), "PIN salah, masukkan PIN yang sama dengan PIN baru Anda", Toast.LENGTH_SHORT).show()
                    dataBinding.confirmPinInput.setText("")
                    showKeyboard()
                }
            }
        }

        dataBinding.backButtonPinThird.setOnClickListener {
            requireActivity().onBackPressed()
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.loadPin.observe(viewLifecycleOwner, Observer { bool ->
            if (bool) {
                dataBinding.loadingViewPin.visibility = View.VISIBLE
            } else {
                dataBinding.loadingViewPin.visibility = View.GONE
            }
        })

        viewModel.pinAlert.observe(viewLifecycleOwner, Observer { alert ->
            if (alert == "Your current pin is not correct") {
                dataBinding.confirmPinInput.setText("")
                view?.let { Navigation.findNavController(it).navigate(R.id.navigateBackTo_currentPinFragment) }
                Toast.makeText(requireActivity(), alert, Toast.LENGTH_LONG).show()
                viewModel.changePinAlert("")
                viewModel.onLoadChangePin(false)
            } else if(alert == "APPROVED/OK") {
                dataBinding.confirmPinInput.setText("")
                view?.let { Navigation.findNavController(it).navigate(R.id.navigateBackTo_settingFragment) }
                Toast.makeText(requireActivity(), "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()
                viewModel.changePinAlert("")
                viewModel.onLoadChangePin(false)
            }
        })
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
        dataBinding.confirmPinInput.requestFocus()
        val imgr =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(dataBinding.confirmPinInput, InputMethodManager.SHOW_IMPLICIT)
    }
}