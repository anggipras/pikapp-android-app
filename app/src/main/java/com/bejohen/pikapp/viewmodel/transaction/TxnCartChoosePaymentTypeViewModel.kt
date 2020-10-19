package com.bejohen.pikapp.viewmodel.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.util.CartUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel

class TxnCartChoosePaymentTypeViewModel(application: Application) : BaseViewModel(application) {

    private var cartUtil = CartUtil(getApplication())

    val success = MutableLiveData<Boolean>()

    fun setPaymentType(selected: String) {
        cartUtil.setPaymentType(selected)
        success.value = true
    }
}
