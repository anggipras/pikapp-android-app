package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopMgmtStatusBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_management.*
import kotlinx.android.synthetic.main.fragment_shop_mgmt_status.*


class ShopMgmtStatusFragment : Fragment() {

    private lateinit var dataBinding: FragmentShopMgmtStatusBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private var navController: NavController? = null
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

        Log.e("auto turn", otherSettingViewModel.autoOnOff.value.toString())

        dataBinding.openStatusInput.append(otherSettingViewModel.openTime.value)
        dataBinding.closeStatusInput.append(otherSettingViewModel.closeTime.value)

        shopStatus_selection.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                dataBinding.openStatus.id -> {
                    dataBinding.shopStatusOpenHour.visibility = View.VISIBLE
                    dataBinding.saveBtn.setOnClickListener {
                        open = dataBinding.openStatusInput.text.toString()
                        close = dataBinding.closeStatusInput.text.toString()
                        when {
                            open.isEmpty() -> {
                                dataBinding.openStatusInput.backgroundTintList = resources.getColorStateList(R.color.red)
                            }
                            close.isEmpty() -> {
                                dataBinding.closeStatusInput.backgroundTintList = resources.getColorStateList(R.color.red)
                            }
                            else -> {
                                otherSettingViewModel.getOpenTime(open)
                                otherSettingViewModel.getCLoseTime(close)
                                dataBinding.openStatusInput.backgroundTintList = resources.getColorStateList(R.color.editTextGray)
                                dataBinding.closeStatusInput.backgroundTintList = resources.getColorStateList(R.color.editTextGray)
                                activity?.let { otherSettingViewModel.updateShopStatus(it.baseContext) }
                                requireActivity().onBackPressed()
                            }
                        }
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
                        activity?.let { otherSettingViewModel.updateShopStatus(it.baseContext) }
                        requireActivity().onBackPressed()
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
                        activity?.let { otherSettingViewModel.updateShopStatus(it.baseContext) }
                        requireActivity().onBackPressed()
                    }
                }
            }
        })

        dataBinding.backButtonShopStatus.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.shopStatusOpenHour.visibility = View.GONE
        attachInputListeners()
        hideKeyboard()
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