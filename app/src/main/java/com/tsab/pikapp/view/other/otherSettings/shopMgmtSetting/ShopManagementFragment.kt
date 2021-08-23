package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShopManagementBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.fragment_shop_management.*

class ShopManagementFragment : Fragment(), ShopManagementAdapter.OnItemClickListener {

    private lateinit var dataBinding: FragmentShopManagementBinding
    private val otherSettingViewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
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

        dataBinding.autoTurnToggle.setOnCheckedChangeListener { _, isChecked ->
            autoTurn = isChecked
            otherSettingViewModel.getAutoOnOff(autoTurn)
            Log.e("autoTurn", autoTurn.toString())
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