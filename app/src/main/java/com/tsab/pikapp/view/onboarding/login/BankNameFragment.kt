package com.tsab.pikapp.view.onboarding.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentBankNameBinding
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_bank_name.*
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_second.*

class BankNameFragment : BottomSheetDialogFragment() {
    private val viewModel: SignupOnboardingViewModelV2 by activityViewModels()
    private var navController: NavController? = null
    private lateinit var dataBinding: FragmentBankNameBinding

    private lateinit var namaBankMap: Map<Int, String>
    private var currentNamaBank: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_bank_name, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = parentFragment?.view?.let { Navigation.findNavController(it) }
        namaBankMap = mapOf(
                R.id.bcaRadioButton to resources.getString(R.string.bank_bca),
                R.id.bniRadioButton to resources.getString(R.string.bank_bni),
                R.id.briRadioButton to resources.getString(R.string.bank_bri),
                R.id.mandiriRadioButton to resources.getString(R.string.bank_mandiri),
                R.id.cimbRadioButton to resources.getString(R.string.bank_cimb)
        )

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        // Take the currently selected value from view model.
        currentNamaBank = viewModel.namaBank.value!!
        checkSelected(currentNamaBank)
    }

    private fun attachInputListeners() {
        dataBinding.bankRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            namaBankMap.entries.forEach { bank ->
                if (checkedId == bank.key) viewModel.validateNamaBank(bank.value)
            }
        }

        dataBinding.closeButton.setOnClickListener {
            viewModel.validateNamaBank(currentNamaBank)
            checkSelected(currentNamaBank)

            navController?.navigateUp()
        }

        dataBinding.okButton.setOnClickListener {
            if (!viewModel.validateNamaBank()) return@setOnClickListener
            navController?.navigateUp()
        }
    }

    private fun checkSelected(namaBank: String) {
        if (namaBank.isNotEmpty()) {
            // Iterate through the namaBank map to get the resource id.
            var selectedId = -1
            namaBankMap.entries.forEach { bank ->
                if (bank.value == namaBank) selectedId = bank.key
            }

            dataBinding.bankRadioGroup.check(selectedId)
        } else {
            dataBinding.bankRadioGroup.clearCheck()
        }
    }
}