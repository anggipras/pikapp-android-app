package com.bejohen.pikapp.viewmodel.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.CartListResponse
import com.bejohen.pikapp.models.model.CartModel
import com.bejohen.pikapp.models.model.StoreProductActionResponse
import com.bejohen.pikapp.models.model.StoreProductDetailResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.CartUtil
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.TransactionActivity
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

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