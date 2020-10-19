package com.tsab.pikapp.viewmodel.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.viewmodel.BaseViewModel

class TxnCartChooseTypeViewModel(application: Application) : BaseViewModel(application) {

    private var cartUtil = CartUtil(getApplication())

    val success = MutableLiveData<Boolean>()
    fun setType(selected: String) {
        cartUtil.setCartType(selected)
        success.value = true
    }

}
