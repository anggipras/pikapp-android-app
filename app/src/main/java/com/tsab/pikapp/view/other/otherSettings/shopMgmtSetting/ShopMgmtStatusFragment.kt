package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopMgmtStatusBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_mgmt_status.*


class ShopMgmtStatusFragment : Fragment() {

    private lateinit var dataBinding: FragmentShopMgmtStatusBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentShopMgmtStatusBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shopStatus_selection.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            if (checkedId == dataBinding.openStatus.id) {
                dataBinding.shopStatusOpenHour.visibility = View.VISIBLE
            } else {
                dataBinding.shopStatusOpenHour.visibility = View.GONE
                hideKeyboard()
            }
        })

        dataBinding.backButtonShopStatus.setOnClickListener {
            requireActivity().onBackPressed()
        }

        attachInputListeners()
        observeViewModel()
    }

    private fun attachInputListeners() {
        val isStatusSelected = when(dataBinding.shopStatusSelection.checkedRadioButtonId) {
            R.id.hours24_status -> "24HOURS"
            R.id.close_status -> "CLOSE"
            else -> "OPEN"
        }
        otherSettingViewModel.setShopStatus(isStatusSelected)
    }

    private fun observeViewModel() {
//        otherSettingViewModel._shopStatus.observe(viewLifecycleOwner, Observer { status ->
//            status?.let {
//                if (status == "OPEN") {
////                dataBinding.shopStatusOpenHour.visibility = View.VISIBLE
//                } else {
////                dataBinding.shopStatusOpenHour.visibility = View.GONE
//                }
//            }
//        })
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = activity?.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
        }
    }
}