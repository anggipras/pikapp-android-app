package com.tsab.pikapp.viewmodel.home

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.CartListResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.TransactionActivity
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class HomeActivityViewModel(application: Application): BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val cart = MutableLiveData<Boolean>()
    val cartError = MutableLiveData<CartListResponse>()

    fun resetCart() {
        cartUtil.setCartEmpty()
    }

    fun getCartStatus() {
        cart.value = cartUtil.getCartStatus()
    }

    fun goToCart(context: Context) {
        val transactionActivity = Intent(context, TransactionActivity::class.java)
        context.startActivity(transactionActivity)
    }
}