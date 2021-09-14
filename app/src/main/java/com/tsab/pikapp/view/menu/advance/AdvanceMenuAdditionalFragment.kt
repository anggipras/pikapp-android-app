package com.tsab.pikapp.view.menu.advance

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
import com.tsab.pikapp.databinding.FragmentAdvanceMenuAdditionalBinding
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class AdvanceMenuAdditionalFragment : Fragment() {
    companion object {
        const val ARGUMENT_IS_EDIT = "isEditing"
        const val ARGUMENT_MENU_NAME = "additionalMenuName"
        const val ARGUMENT_MENU_PRICE = "additionalMenuPrice"
    }

    private val viewModel: AdvanceMenuViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentAdvanceMenuAdditionalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_advance_menu_additional,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        fetchArguments()
        observeViewModel()
        attachInputListeners()
    }

    private fun fetchArguments() {
        viewModel.setAdditionalNamaDaftarPilihan(arguments?.getString(ARGUMENT_MENU_NAME) ?: "")
        viewModel.setAdditionalHarga(arguments?.getString(ARGUMENT_MENU_PRICE) ?: "")

        if (arguments?.getBoolean(ARGUMENT_IS_EDIT) == true) {
            dataBinding.headerHeaderText.text = getString(R.string.am_ubah_daftar_pilihan_header)
        }
    }

    private fun observeViewModel() {
        viewModel.additionalNamaDaftarPilihan.observe(
            viewLifecycleOwner,
            Observer { namaDaftarPilihan ->
                dataBinding.namaDaftarPilihanInputText.setText(namaDaftarPilihan)
            })
        viewModel.additionalNamaDaftarPilihanError.observe(
            viewLifecycleOwner,
            Observer { namaDaftarPilihanError ->
                dataBinding.namaDaftarPilihanErrorText.visibility =
                    if (namaDaftarPilihanError.isEmpty()) View.GONE else View.VISIBLE
                dataBinding.namaDaftarPilihanErrorText.text = namaDaftarPilihanError
            })

        viewModel.additionalHarga.observe(viewLifecycleOwner, Observer { harga ->
            dataBinding.hargaInputText.setText(harga)
        })
        viewModel.additionalHargaError.observe(viewLifecycleOwner, Observer { hargaError ->
            dataBinding.hargaErrorText.visibility =
                if (hargaError.isEmpty()) View.GONE else View.VISIBLE
            dataBinding.hargaErrorText.text = hargaError
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigateUp()
        }, view)

        dataBinding.namaDaftarPilihanInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateAdditionalNamaDaftarPilihan(
                    dataBinding.namaDaftarPilihanInputText.text.toString()
                )
            }
            false
        }

        dataBinding.hargaInputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                return@setOnEditorActionListener !viewModel.validateAdditionalHarga(
                    dataBinding.hargaInputText.text.toString()
                )
            }
            false
        }

        dataBinding.saveButton.setOnClickListener {
            hideKeyboard()

            viewModel.validateAdditionalNamaDaftarPilihan(dataBinding.namaDaftarPilihanInputText.text.toString())
            viewModel.validateAdditionalHarga(dataBinding.hargaInputText.text.toString())

            if (!viewModel.validateAdditionalScreen()) return@setOnClickListener
            navController.navigate(R.id.action_advanceMenuAdditionalFragment_to_advanceMenuDetailsFragment, bundleOf(AdvanceMenuDetailsFragment.ARGUMENT_IS_EDIT to false))
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