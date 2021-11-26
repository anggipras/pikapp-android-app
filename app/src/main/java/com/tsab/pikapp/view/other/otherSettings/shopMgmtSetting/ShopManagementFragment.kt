package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
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
import java.text.SimpleDateFormat
import java.util.*

class ShopManagementFragment : Fragment(), ShopManagementAdapter.OnItemClickListener {

    private lateinit var dataBinding: FragmentShopManagementBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    private var hour: String = ""
    private var day: String = ""
    var autoTurn: Boolean = true
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

        dataBinding.backButtonShop.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateBackToFromShopManagementFragment_settingFragment)
        }

        dataBinding.autoTurnToggle.setOnClickListener {
            showPopup()
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

    private fun showPopup(){
        if (otherSettingViewModel.autoOnOff.value == true) {
            dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_on)
            val inflater: LayoutInflater =
                    activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.open_close_restaurant_popup, null)
            val popupWindow = PopupWindow(
                    view,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 20.0F
            }

            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            val closeBtn = view.findViewById<ImageView>(R.id.closeBtn)
            val buttonContinue = view.findViewById<TextView>(R.id.buttonContinue)
            val buttonBack = view.findViewById<Button>(R.id.buttonBack)

            closeBtn.setOnClickListener {
                otherSettingViewModel.setAutoOnOffTrue(autoTurn)
                popupWindow.dismiss()
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_on)
            }

            buttonContinue.setOnClickListener {
                Toast.makeText(requireView().context, "false", Toast.LENGTH_SHORT).show()
                otherSettingViewModel.setAutoOnOffFalse(autoTurn)
                popupWindow.dismiss()
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_off)
            }

            buttonBack.setOnClickListener {
                otherSettingViewModel.setAutoOnOffTrue(autoTurn)
                popupWindow.dismiss()
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_on)
            }
        } else if (otherSettingViewModel.autoOnOff.value == false) {
            dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.switch_on)
            otherSettingViewModel.setAutoOnOffTrue(autoTurn)
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