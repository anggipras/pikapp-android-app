package com.bejohen.pikapp.viewmodel.categoryProduct

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.viewmodel.BaseViewModel

class AddToCartViewModel(application: Application) : BaseViewModel(application) {

    val quantity = MutableLiveData<Int>()

    fun decreaseQty(qty: Int) {
        var q = qty - 1
        if (qty <= 1) {
            q = 1
        }
        quantity.value = q
    }

    fun increaseQty(qty: Int) {
        val q = qty + 1
        quantity.value = q
    }
}