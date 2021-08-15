package com.tsab.pikapp.view.onboarding.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_bank_name.*
import kotlinx.android.synthetic.main.fragment_login_v2_first.*
import kotlinx.android.synthetic.main.fragment_signup_v2_second.*

class BankNameFragment : BottomSheetDialogFragment() {
    private val viewModel: SignupOnboardingViewModelV2 by activityViewModels()
    private var navController: NavController? = null

    private var namaBankArray: Array<Map<Int, String>> = arrayOf(
        mapOf(R.id.BCA to "BANK CENTRAL ASIA (BCA)"),
        mapOf(R.id.BNI to "BANK NEGARA INDONESIA (BNI)"),
        mapOf(R.id.BRI to "BANK RAKYAT INDONESIA (BRI)"),
        mapOf(R.id.Mandiri to "BANK MANDIRI"),
        mapOf(R.id.CIMB to "BANK CIMB NIAGA")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bank_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = parentFragment?.view?.let { Navigation.findNavController(it) }

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        // Take the available value from view model
        val currentSelected = viewModel.namaBank.value!!
        if (currentSelected.isNotEmpty()) {
            var selectedId = -1

            // Iterate through the namaBank array to get the resource R.id
            namaBankArray.forEachIndexed { index, bank ->
                if (bank.values.contains(currentSelected)) {
                    selectedId = namaBankArray[index].keys.iterator().next()
                }
            }
            bankname.check(selectedId)
        }
    }

    private fun attachInputListeners() {
        bankname.setOnCheckedChangeListener { _, checkedId ->
            namaBankArray.forEach { bank ->
                val bankInfo = bank.entries.iterator().next()
                if (checkedId == bankInfo.key) {
                    viewModel.validateNamaBank(bankInfo.value)
                }
            }
        }

        closeBtn.setOnClickListener {
            navController?.navigateUp()
        }

        btnoke.setOnClickListener {
            if (!viewModel.validateNamaBank()) return@setOnClickListener
            navController?.navigateUp()
        }
    }
}