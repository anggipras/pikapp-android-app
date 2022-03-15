package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopManagementBinding
import com.tsab.pikapp.util.getHour
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_management.*
import kotlinx.android.synthetic.main.second_alert_dialog.view.*
import java.text.SimpleDateFormat
import java.util.*

class ShopManagementFragment : Fragment(), ShopManagementAdapter.OnItemClickListener {

    private lateinit var dataBinding: FragmentShopManagementBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    private var hour: String = ""
    private var day: String = ""
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentShopManagementBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.sm_header)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.navigateBackToFromShopManagementFragment_settingFragment)
            }
        })

        swipeRefreshLayout = swipeShopManagement

        navController = Navigation.findNavController(view)

        shopSchedule_recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context)
        shopSchedule_recyclerView.layoutManager = linearLayoutManager

        activity?.let { otherSettingViewModel.getMerchantSchedule(it.baseContext, shopSchedule_recyclerView, this) }

        swipeRefreshLayout.setOnRefreshListener {
            otherSettingViewModel.getMerchantSchedule(requireContext(), shopSchedule_recyclerView, this)
        }

        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateBackToFromShopManagementFragment_settingFragment)
        }

        dataBinding.autoTurnToggle.setOnClickListener {
            showPopup(requireActivity())
        }

        hour = getHour()

        day = SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()).toUpperCase()

        observeViewModel()
    }

    private fun observeViewModel(){
        otherSettingViewModel.Loading.observe(viewLifecycleOwner, Observer { loading ->
            if (!loading) {
                otherSettingViewModel.shopScheduleResult.value?.forEach { resSchedule ->
                    if (resSchedule.days == day){
                        if(resSchedule.dailyStatus == "OPEN"){
                            when {
                                hour < resSchedule.openTime.toString() -> {
                                    closedShop()
                                }
                                hour > resSchedule.closeTime.toString() -> {
                                    closedShop()
                                }
                                else -> {
                                    openedShop()
                                }
                            }
                        }else{
                            closedShop()
                        }
                    }
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })

        otherSettingViewModel.autoOnOff.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_on)
                } else {
                    dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_off)
                }
            }
        })
    }

    private fun closedShop(){
        dataBinding.restaurantStatusNow.text = getString(R.string.Tutup).toUpperCase()
        dataBinding.restaurantStatusNow.setTextColor(resources.getColor(R.color.red))
        dataBinding.restaurantStatusDetail.text = getString(R.string.sm_restaurant_status_detail_close)
    }

    private fun openedShop(){
        dataBinding.restaurantStatusNow.text = getString(R.string.open_status_title).toUpperCase()
        dataBinding.restaurantStatusNow.setTextColor(resources.getColor(R.color.green))
        dataBinding.restaurantStatusDetail.text = getString(R.string.sm_restaurant_status_detail_open)
    }

    private fun showPopup(activity: Activity) {
        if (otherSettingViewModel.autoOnOff.value == true) {
            autoTurnOnOffDialog(activity, false)
        } else if (otherSettingViewModel.autoOnOff.value == false) {
            autoTurnOnOffDialog(activity, true)
        }
    }

    private fun autoTurnOnOffDialog(activity: Activity, status: Boolean) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.second_alert_dialog, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )
        if (status) {
            mDialogView.second_dialog_text.text = getString(R.string.sm_close_open_popup)
        } else {
            mDialogView.second_dialog_text.text = getString(R.string.sm_open_close_popup)
        }

        mDialogView.second_dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.second_dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.second_dialog_ok.setOnClickListener {
            mAlertDialog.dismiss()
            otherSettingViewModel.setAutoOnOff(status, requireContext(), dataBinding.loadingOverlay)
        }
    }

    override fun onItemClick(position: Int, shopScheduleDay: String?, closeTime: String?, openTime: String?) {
        otherSettingViewModel.shopManagementAdapter.notifyItemChanged(position)

        val days = otherSettingViewModel.shopManagementAdapter.shopScheduleList[position].days
        otherSettingViewModel.getDays(days.toString())

        val openTime = otherSettingViewModel.shopManagementAdapter.shopScheduleList[position].openTime
        otherSettingViewModel.getOpenTime(openTime.toString())

        val closeTime = otherSettingViewModel.shopManagementAdapter.shopScheduleList[position].closeTime
        otherSettingViewModel.getCLoseTime(closeTime.toString())

        val isForceClose = otherSettingViewModel.shopManagementAdapter.shopScheduleList[position].isForceClose
        otherSettingViewModel.getForceClose(isForceClose!!)

        navController?.navigate(R.id.navigateTo_shopMgmtStatusFragment)
    }
}