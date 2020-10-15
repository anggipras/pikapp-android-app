package com.bejohen.pikapp.viewmodel.transaction

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.CartUtil
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class TxnCartViewModel(application: Application): BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var cartUtil = CartUtil(getApplication())

    val loading = MutableLiveData<Boolean>()
    val cartList = MutableLiveData<List<CartModel>>()
    val merchantDetailResponse = MutableLiveData<MerchantDetail>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    var tmpCart = arrayListOf<CartModel>()
    var tableNo = 0
    val tableNumber = MutableLiveData<Int>()

    fun getCart() {
        val cart = cartUtil.getCart()
        cart?.let {
            tmpCart.addAll(cart)
        }
        cartList.value = tmpCart
    }

    fun getMerchant() {
        loading.value = true
        val merchantID = tmpCart[0].merchantID
        val location = prefHelper.getLatestLocation()
        disposable.add(
            pikappService.getMerchantDetail(merchantID!!, location.latitude!!, location.longitude!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantDetailResponse>() {
                    override fun onSuccess(t: MerchantDetailResponse) {
                        t.results?.let { it1 -> merchantDetailRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: MerchantListErrorResponse
                        try {
                            Log.d("Debug", "error merchant detail : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<MerchantListErrorResponse>(
                                    body,
                                    MerchantListErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = MerchantListErrorResponse(
                                "now", "503", "Unavailable", "Unavailable", "Unavailable"
                            )
                        }

                        merchantFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.message} ${errorResponse.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Debug",
                            "error merchant detail : ${errorResponse.message} ${errorResponse.path}"
                        )
                    }
                })
        )
    }

    private fun merchantDetailRetrieved(merchant: MerchantDetail) {
        merchantDetailResponse.value = merchant
        loading.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
    }

    fun checkType() {
        val deepLink = prefHelper.getStoredDeepLink()
        deepLink?.let {
            if(it.mid == tmpCart[0].merchantID) {
                it.tableNo?.let {
                    if(it.isEmpty()) {
                        tableNo = -1
                    } else if (it == "0") {
                        tableNo = 0
                    }
                    else {
                        tableNo = it.toInt()
                    }
                    tableNumber.value = tableNo
                }
            }
        }

    }

    fun increaseQty(product: CartModel) {
        val sameProduct : CartModel? = tmpCart.find { p ->
            p.productID == product.productID
        }

        sameProduct?.let {
            it.qty = it.qty!!.plus(1)
            it.totalPrice = it.qty!! * it.price!!
        }
        cartUtil.setCart(tmpCart)
        cartList.postValue(tmpCart)
    }

    fun decreaseQty(product: CartModel) {
        val sameProduct : CartModel? = tmpCart.find { p ->
            p.productID == product.productID
        }

        sameProduct?.let {
            it.qty = it.qty!!.minus(1)
            it.totalPrice = it.qty!! * it.price!!
        }
        cartUtil.setCart(tmpCart)
        cartList.postValue(tmpCart)
    }

    fun deleteProduct(product: CartModel) {
        val sameProduct : CartModel? = tmpCart.find { p ->
            p.productID == product.productID
        }

        sameProduct?.let {
            tmpCart.remove(it)
        }
        cartUtil.setCart(tmpCart)
        cartList.postValue(tmpCart)
    }

    fun setCartEmpty() {
        cartUtil.setCartEmpty()
    }


}