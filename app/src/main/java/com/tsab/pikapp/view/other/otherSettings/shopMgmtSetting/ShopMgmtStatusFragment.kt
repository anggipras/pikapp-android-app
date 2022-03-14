package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopMgmtStatusBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_mgmt_status.*
import java.text.SimpleDateFormat

class ShopMgmtStatusFragment : Fragment() {
    private lateinit var dataBinding: FragmentShopMgmtStatusBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private var open: String = ""
    private var close: String = ""

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentShopMgmtStatusBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.sm_status_header)

        dataBinding.openStatusInput.append(otherSettingViewModel.openTime.value)
        dataBinding.closeStatusInput.append(otherSettingViewModel.closeTime.value)

        if (otherSettingViewModel.openTime.value == "00:00" && otherSettingViewModel.closeTime.value == "00:00") {
            dataBinding.closeStatus.isChecked = true
        } else if (otherSettingViewModel.openTime.value == "00:00" && otherSettingViewModel.closeTime.value == "00:00") {
            dataBinding.hours24Status.isChecked = true
        } else {
            dataBinding.openStatus.isChecked = true
            dataBinding.shopStatusOpenHour.isVisible = true
            dataBinding.saveBtn.setOnClickListener {
                saveStoreTime()
            }
        }

        dataBinding.openStatusInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
            }
            true
        }

        dataBinding.closeStatusInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || event == null || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
            }
            true
        }

        shopStatus_selection.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                dataBinding.openStatus.id -> {
                    dataBinding.shopStatusOpenHour.visibility = View.VISIBLE
                    dataBinding.saveBtn.setOnClickListener {
                        saveStoreTime()
                    }
                }
                dataBinding.closeStatus.id -> {
                    dataBinding.shopStatusOpenHour.visibility = View.GONE
                    hideKeyboard()
                    dataBinding.saveBtn.setOnClickListener {
                        open = "00:00"
                        close = "00:00"
                        otherSettingViewModel.getOpenTime(open)
                        otherSettingViewModel.getCLoseTime(close)
                        activity?.let { otherSettingViewModel.updateShopStatus(
                            it.baseContext,
                            view,
                            dataBinding.loadingOverlay
                        ) }
                    }
                }
                dataBinding.hours24Status.id -> {
                    dataBinding.shopStatusOpenHour.visibility = View.GONE
                    hideKeyboard()
                    dataBinding.saveBtn.setOnClickListener {
                        open = "00:00"
                        close = "23:59"
                        otherSettingViewModel.getOpenTime(open)
                        otherSettingViewModel.getCLoseTime(close)
                        activity?.let { otherSettingViewModel.updateShopStatus(
                            it.baseContext,
                            view,
                            dataBinding.loadingOverlay
                        ) }
                    }
                }
            }
        }

        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateToFromShopMgmtStatusFragment_shopManagementFragment)
        }

        attachInputListeners()
        hideKeyboard()
    }

    private fun saveStoreTime() {
        val dateFormat = SimpleDateFormat("HH:mm")
        open = dataBinding.openStatusInput.text.toString()
        close = dataBinding.closeStatusInput.text.toString()

        val validationTime: Boolean
        if (open.get(2).toString() == ":" && close.get(2).toString() == ":") {
            val getOpenTime = dateFormat.parse(open)
            val getCloseTime = dateFormat.parse(close)
            val openTimeResult = dateFormat.format(getOpenTime)
            val closeTimeResult = dateFormat.format(getCloseTime)
            validationTime = openTimeResult == open && closeTimeResult == close
        } else {
            validationTime = false
        }

        if (validationTime) {
            when {
                open.isEmpty() -> {
                    dataBinding.openStatusInput.backgroundTintList =
                        resources.getColorStateList(R.color.red)
                }
                close.isEmpty() -> {
                    dataBinding.closeStatusInput.backgroundTintList =
                        resources.getColorStateList(R.color.red)
                }
                else -> {
                    otherSettingViewModel.getOpenTime(open)
                    otherSettingViewModel.getCLoseTime(close)
                    dataBinding.openStatusInput.backgroundTintList =
                        resources.getColorStateList(R.color.editTextGray)
                    dataBinding.closeStatusInput.backgroundTintList =
                        resources.getColorStateList(R.color.editTextGray)
                    activity?.let { view?.let { it1 ->
                        otherSettingViewModel.updateShopStatus(it.baseContext,
                            it1, dataBinding.loadingOverlay
                        )
                    } }
                }
            }
        } else {
            Toast.makeText(
                requireActivity(),
                "Penulisan format waktu salah",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun attachInputListeners() {
        val isStatusSelected = when(dataBinding.shopStatusSelection.checkedRadioButtonId) {
            R.id.hours24_status -> "24HOURS"
            R.id.close_status -> "CLOSE"
            else -> "OPEN"
        }
        otherSettingViewModel.setShopStatus(isStatusSelected)
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}