package com.tsab.pikapp.viewmodel.storeOrderList

import android.app.Application
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel

class StoreOrderListStatusViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())

    fun getSelectedTab(): Int {
        val x = prefHelper.getOrderListTabSelected()
        prefHelper.resetOrderListTabSelected()
        return x!!
    }

    fun deleteNotificationDetail() {
        prefHelper.deleteNotificationDetail()
    }
}