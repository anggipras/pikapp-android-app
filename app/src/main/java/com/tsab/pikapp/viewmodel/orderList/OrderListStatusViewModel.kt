package com.tsab.pikapp.viewmodel.orderList

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.OrderListActivity
import com.tsab.pikapp.view.orderList.orderListDetail.OrderListDetailFragment
import com.tsab.pikapp.viewmodel.BaseViewModel

class OrderListStatusViewModel(application: Application) : BaseViewModel(application) {

    val notificationActive = MutableLiveData<String>()

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getSelectedTab(): Int {
        val x = prefHelper.getOrderListTabSelected()
        prefHelper.resetOrderListTabSelected()
        return x!!
    }

    fun checkNotificationOrder() {
        val notif = prefHelper.getNotificationDetail()
        notif?.let {
            it.transactionId?.let { transactionID ->
                if (transactionID.isNotEmpty()) notificationActive.value = transactionID
            }
        }
    }

    fun goToOrderListDetail(context: Context) {
        val orderListDetailFragment = OrderListDetailFragment()
        orderListDetailFragment.show(
            (context as OrderListActivity).supportFragmentManager,
            orderListDetailFragment.tag
        )
    }


}