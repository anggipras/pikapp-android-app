package com.tsab.pikapp.view.onboarding.login

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSignupV2SecondBinding
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_signup_v2_second.*

class SignupV2Second : Fragment() {
    private val viewModel: SignupOnboardingViewModelV2 by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentSignupV2SecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup_v2_second,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        dataBinding.namaRestoranInputText.setText(viewModel.namaRestoran.value)
        viewModel.namaRestoranError.observe(viewLifecycleOwner, Observer { namaRestoranError ->
            dataBinding.namaRestoranErrorText.text = namaRestoranError
        })

        dataBinding.namaFoodcourtInputText.setText(viewModel.namaFoodcourt.value)
        viewModel.namaFoodcourtError.observe(viewLifecycleOwner, Observer { namaFoodcourtError ->
            dataBinding.namaFoodcourtErrorText.text = namaFoodcourtError
        })

        dataBinding.alamatInputText.setText(viewModel.alamat.value)
        viewModel.alamatError.observe(viewLifecycleOwner, Observer { alamatError ->
            dataBinding.alamatErrorText.text = alamatError
        })

        viewModel.namaBank.observe(viewLifecycleOwner, Observer { namaBank ->
            dataBinding.namaBankInputText.setText(namaBank)
        })
        viewModel.namaBankError.observe(viewLifecycleOwner, Observer { namaBankError ->
            dataBinding.namaBankErrorText.text = namaBankError
        })

        dataBinding.nomorRekeningInputText.setText(viewModel.nomorRekening.value)
        viewModel.nomorRekeningError.observe(viewLifecycleOwner, Observer { nomorRekeningError ->
            dataBinding.nomorRekeningErrorText.text = nomorRekeningError
        })

        dataBinding.namaRekeningInputText.setText(viewModel.namaRekening.value)
        viewModel.namaRekeningError.observe(viewLifecycleOwner, Observer { namaRekeningError ->
            dataBinding.namaRekeningErrorText.text = namaRekeningError
            hideKeyboard()
        })
    }

    private fun attachInputListeners() {
        dataBinding.nextButton.setOnClickListener {
            hideKeyboard()

            viewModel.validateNamaRestoran(namaRestoranInputText.text.toString())
            viewModel.validateNamaFoodcourt(namaFoodcourtInputText.text.toString())
            viewModel.validateAlamat(alamatInputText.text.toString())
            viewModel.validateNamaBank(namaBankInputText.text.toString())
            viewModel.validateNomorRekening(nomorRekeningInputText.text.toString())
            viewModel.validateNamaRekening(namaRekeningInputText.text.toString())

            if (!viewModel.validateSecondPage()) return@setOnClickListener
            navController.navigate(R.id.action_signupV2Second_to_signupV2ThirdFragment)
        }

        dataBinding.namaBankInputText.isFocusable = false
        dataBinding.namaBankInputText.isFocusableInTouchMode = false
        dataBinding.namaBankInputText.setOnClickListener {
            hideKeyboard()
            navController.navigate(R.id.action_signupV2Second_to_bankNameFragment2)
        }

        dataBinding.namaRestoranInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateNamaRestoran(
                    dataBinding.namaRestoranInputText.text.toString()
                )
            }
            false
        }

        dataBinding.namaFoodcourtInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateNamaFoodcourt(
                    dataBinding.namaFoodcourtInputText.text.toString()
                )
            }
            false
        }

        dataBinding.alamatInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateAlamat(
                    dataBinding.alamatInputText.text.toString()
                )
            }
            false
        }

        dataBinding.namaBankInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateNamaBank(
                    dataBinding.namaBankInputText.text.toString()
                )
            }
            false
        }

        dataBinding.nomorRekeningInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateNomorRekening(
                    dataBinding.nomorRekeningInputText.text.toString()
                )
            }
            false
        }

        dataBinding.namaRekeningInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.validateNamaRekening(dataBinding.namaRekeningInputText.text.toString())
            }
            true
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
