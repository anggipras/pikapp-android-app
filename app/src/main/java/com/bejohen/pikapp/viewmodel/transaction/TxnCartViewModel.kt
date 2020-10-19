package com.bejohen.pikapp.viewmodel.transaction

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.*
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.TransactionActivity
import com.bejohen.pikapp.view.categoryProduct.AddToCartFragment
import com.bejohen.pikapp.view.transaction.TxnCartChoosePaymentTypeFragment
import com.bejohen.pikapp.view.transaction.TxnCartChooseTypeFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class TxnCartViewModel(application: Application): BaseViewModel(application), TxnCartChooseTypeFragment.DialogDismissInterface, TxnCartChoosePaymentTypeFragment.DialogDismissInterface {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var cartUtil = CartUtil(getApplication())
    private var transactionUtil = TransactionUtil(getApplication())

    val loading = MutableLiveData<Boolean>()
    val cartList = MutableLiveData<List<CartModel>>()
    val merchantDetailResponse = MutableLiveData<MerchantDetail>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    var tmpCart = arrayListOf<CartModel>()
    var tableNo = 0
    val cartType = MutableLiveData<String>()
    val paymentType = MutableLiveData<String>()

    val transactionSucccess = MutableLiveData<TransactionResponseDetail>()

    fun getCart() {
        val cart = cartUtil.getCart()
        cart?.let {
            tmpCart.addAll(cart)
        }
        cartList.value = tmpCart
    }

    fun getPaymentType() {
        paymentType.value = cartUtil.getPaymentType()
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
        val type = cartUtil.getCartType()
        val status: Boolean = cartUtil.getCartIsNew()!!

        if (status) {
            val deeplink = prefHelper.getStoredDeepLink()
            deeplink?.let {
                if (tmpCart[0].merchantID == it.mid) {
                    it.tableNo?.let { no ->
                        if (no == "0") {
                            tableNo = 0
                            cartUtil.setCartType("TAKE_AWAY")
                        } else {
                            tableNo = no.toInt()
                            cartUtil.setCartType("DINE_IN")
                        }
                    }
                }
            }
            cartUtil.setCartIsNew(false)
        } else {
            type?.let {
                if(it == "TAKE_AWAY") {
                    tableNo = 0

                } else if (it == "DINE_IN") {
                    val deeplink = prefHelper.getStoredDeepLink()
                    deeplink?.let { deeplink ->
                        tableNo = if (deeplink.tableNo.isNullOrEmpty()) 0 else deeplink.tableNo.toInt()
                        cartUtil.setCartType("DINE_IN")
                    }
                }
            }
        }

        cartType.value = cartUtil.getCartType()
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

    fun goToSelectType(context: Context) {
        val args = Bundle()
        args.putString(SELECTED_TYPE, cartUtil.getCartType())
        val txnCartChooseTypeFragment = TxnCartChooseTypeFragment(this)
        txnCartChooseTypeFragment.arguments = args
        txnCartChooseTypeFragment.show((context as TransactionActivity).supportFragmentManager, txnCartChooseTypeFragment.tag)
    }

    fun goToPaymentType(context: Context) {
        val args = Bundle()
        args.putString(SELECTED_PAYMENT, cartUtil.getPaymentType())
        val txnCartChoosePaymentTypeFragment = TxnCartChoosePaymentTypeFragment(this)
        txnCartChoosePaymentTypeFragment.arguments = args
        txnCartChoosePaymentTypeFragment.show((context as TransactionActivity).supportFragmentManager, txnCartChoosePaymentTypeFragment.tag)
    }

    override fun onDialogDismiss() {
        cartType.value = cartUtil.getCartType()
    }

    override fun onPaymentDialogDismiss() {
        paymentType.value = cartUtil.getPaymentType()
    }

    fun doPay(totalPrice: String) {
        loading.value = true
        val transactionModel = createTransactionModel(totalPrice)

        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
            pikappService.postTransaction(email, token, transactionModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<TransactionResponse>(){
                    override fun onSuccess(t: TransactionResponse) {
                        t.results?.let { it1 -> transactionSuccess(it1) }
                    }
                    override fun onError(e: Throwable) {

                    }
                })
        )
    }

    private fun createTransactionModel(totalPrice: String): TransactionModel {
        val postCart = arrayListOf<CartTxnModel>()

        for(cart in tmpCart) {
            postCart.add(CartTxnModel(cart.productID, cart.notes, cart.qty))
        }

        return TransactionModel(merchantID = tmpCart[0].merchantID, totalPrices = totalPrice, paymentWith = cartUtil.getPaymentType(), bizType = cartUtil.getCartType(), tableNo = tableNo.toString(), products = postCart)
    }

    private fun transactionSuccess(transactionResponseDetails: List<TransactionResponseDetail>) {
        if(transactionResponseDetails.isNotEmpty()) {
            val txnList = arrayListOf<TransactionResponseDetail>()
            val txn = transactionUtil.getTransactionActive()
            txn?.let {
                txnList.addAll(txn)
            }
            transactionUtil.setTransactionActive(txnList)
            transactionSucccess.value = transactionResponseDetails[0]
        }
    }
}