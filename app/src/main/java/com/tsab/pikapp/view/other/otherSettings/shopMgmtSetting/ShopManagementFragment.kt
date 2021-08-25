package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopManagementBinding
import com.tsab.pikapp.util.getDay
import com.tsab.pikapp.util.getHour
import com.tsab.pikapp.util.getTimestamp
import com.tsab.pikapp.view.categoryMenu.CategoryNavigation
import com.tsab.pikapp.view.homev2.menu.MenuNavigation
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_management.*
import kotlinx.android.synthetic.main.menu_fragment.*
import org.w3c.dom.Text

class ShopManagementFragment : Fragment(), ShopManagementAdapter.OnItemClickListener {

    private lateinit var dataBinding: FragmentShopManagementBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    private var hour: String = ""
    private var day: String = ""
    var autoTurn: Boolean = true

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentShopManagementBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        shopSchedule_recyclerView.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(requireView().context)
        shopSchedule_recyclerView.layoutManager = linearLayoutManager

        activity?.let { otherSettingViewModel.getMerchantSchedule(it.baseContext, shopSchedule_recyclerView, this) }

        dataBinding.backButtonShop.setOnClickListener {
            requireActivity().onBackPressed()
        }

        /*dataBinding.autoTurnToggle.setOnCheckedChangeListener { _, isChecked ->
            autoTurn = isChecked
            otherSettingViewModel.getAutoOnOff(autoTurn)
        }*/

        dataBinding.autoTurnToggle.setOnClickListener {
            //showDialog()
            showPopup()
        }

        hour = getHour()
        Log.e("hour", hour)

        day = getDay().toUpperCase()
        Log.e("day", day)

        observeViewModel()
    }

    private fun observeViewModel(){
        otherSettingViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                Log.e("observe result", otherSettingViewModel.shopScheduleResult.value.toString())
                otherSettingViewModel.shopScheduleResult.value?.forEach { resSchedule ->
                    if (resSchedule.days == day){
                        Log.e("bisa", "mantap")
                        if(resSchedule.dailyStatus == "OPEN"){
                            when {
                                hour < resSchedule.openTime.toString() -> {
                                    closedShop()
                                    Log.e("status", "tutup gan tokonya")
                                }
                                hour > resSchedule.closeTime.toString() -> {
                                    closedShop()
                                    Log.e("status", "tutup gan tokonya")
                                }
                                else -> {
                                    openedShop()
                                    Log.e("status", "buka gan tokonya")
                                }
                            }
                        }else{
                            closedShop()
                            Log.e("status", "tutup gan tokonya")
                        }
                    }
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

    private fun showDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.open_close_restaurant_popup, null)
        val popupWindow = PopupWindow(dialogLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val closeBtn = dialogLayout.findViewById<ImageView>(R.id.closeBtn)
        val continueBtn = dialogLayout.findViewById<TextView>(R.id.buttonContinue)
        val backBtn = dialogLayout.findViewById<Button>(R.id.buttonBack)

        with(builder){
            val dialog = builder.create()
            closeBtn.setOnClickListener{
                dialog.dismiss()
                Log.e("msg", "dismiss")
            }

            continueBtn.setOnClickListener {
                dialog.dismiss()
                Log.e("msg", "continue")
            }

            backBtn.setOnClickListener {
                dialog.dismiss()
                Log.e("msg", "dismiss")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun showPopup(){
        if (otherSettingViewModel.autoOnOff.value == true) {
            dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.toggle_on)
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
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.toggle_on)
                Log.e("auto turn", otherSettingViewModel.autoOnOff.value.toString())
            }

            buttonContinue.setOnClickListener {
                Toast.makeText(requireView().context, "false", Toast.LENGTH_SHORT).show()
                otherSettingViewModel.setAutoOnOffFalse(autoTurn)
                popupWindow.dismiss()
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.toggle_off)
                Log.e("auto turn", otherSettingViewModel.autoOnOff.value.toString())
            }

            buttonBack.setOnClickListener {
                otherSettingViewModel.setAutoOnOffTrue(autoTurn)
                popupWindow.dismiss()
                dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.toggle_on)
                Log.e("auto turn", otherSettingViewModel.autoOnOff.value.toString())
            }
        } else if (otherSettingViewModel.autoOnOff.value == false) {
            dataBinding.autoTurnToggle.setBackgroundResource(R.drawable.toggle_on)
            otherSettingViewModel.setAutoOnOffTrue(autoTurn)
            Log.e("auto turn", otherSettingViewModel.autoOnOff.value.toString())
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
//        Toast.makeText(requireActivity(), shopScheduleDay + ", " + closeTime + "-" + openTime, Toast.LENGTH_SHORT).show()
    }
}