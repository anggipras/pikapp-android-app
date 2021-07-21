package com.tsab.pikapp.view.onboarding.login

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSignupV2FirstBinding
import com.tsab.pikapp.viewmodel.onboarding.login.SignupOnboardingViewModelV2

class SignupV2First : Fragment() {
    private val viewModel: SignupOnboardingViewModelV2 by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentSignupV2FirstBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_v2_first,
                container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        observeViewModel()

        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.emailError.observe(viewLifecycleOwner, Observer { emailError ->
            dataBinding.emailErrorText.text = if (emailError.isEmpty()) "" else emailError
        })

        viewModel.fullNameError.observe(viewLifecycleOwner, Observer { fullNameError ->
            dataBinding.fullNameErrorText.text = if (fullNameError.isEmpty()) "" else fullNameError
        })

        viewModel.phoneError.observe(viewLifecycleOwner, Observer { phoneError ->
            dataBinding.phoneErrorText.text = if (phoneError.isEmpty()) "" else phoneError
        })

        viewModel.pinError.observe(viewLifecycleOwner, Observer { pinError ->
            dataBinding.pinErrorText.text = if (pinError.isEmpty()) "" else pinError
            hideKeyboard()
        })
    }

    private fun attachInputListeners() {
        dataBinding.nextButton.setOnClickListener {
            hideKeyboard()

            viewModel.validateEmail(dataBinding.emailInputText.text.toString())
            viewModel.validateFullName(dataBinding.fullNameInputText.text.toString())
            viewModel.validatePhone(dataBinding.phoneInputText.text.toString())
            viewModel.validatePin(dataBinding.pinInputText.text.toString())

            if (!viewModel.validateFirstPage()) return@setOnClickListener

            val bundle = bundleOf(
                    "email" to dataBinding.emailInputText.text.toString(),
                    "name" to dataBinding.fullNameInputText.text.toString(),
                    "phone" to dataBinding.phoneInputText.text.toString(),
                    "pin" to dataBinding.pinInputText.text.toString())
            navController.navigate(R.id.action_signupV2First_to_signupV2Second, bundle)
        }

        dataBinding.emailInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateEmail(
                        dataBinding.emailInputText.text.toString())
            }
            false
        }

        dataBinding.fullNameInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateFullName(
                        dataBinding.fullNameInputText.text.toString())
            }
            false
        }

        dataBinding.phoneInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validatePhone(
                        dataBinding.phoneInputText.text.toString())
            }
            false
        }

        dataBinding.pinInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.validatePin(dataBinding.pinInputText.text.toString())
            }
            true
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}