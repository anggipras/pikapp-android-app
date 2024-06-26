package com.tsab.pikapp.viewmodel.categoryProduct

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.MerchantListErrorResponse
import com.tsab.pikapp.models.model.ProductDetail
import com.tsab.pikapp.models.model.ProductDetailResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.LoginActivity
import com.tsab.pikapp.view.categoryProduct.AddToCartFragment
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class ProductDetailViewModel(application: Application) : BaseViewModel(application),
    AddToCartFragment.DialogDismissInterface {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loading = MutableLiveData<Boolean>()
    val productDetailResponse = MutableLiveData<ProductDetail>()
    val productLoadError = MutableLiveData<Boolean>()
    val productErrorResponse = MutableLiveData<MerchantListErrorResponse>()

    var latitude = ""
    var longitude = ""
    var cart = MutableLiveData<Boolean>()

    fun getProductDetail(pid: String) {
        loading.value = true
        val location = prefHelper.getLatestLocation()
        location.let {
            latitude = it.latitude!!
            longitude = it.longitude!!
        }
        disposable.add(
            pikappService.getProductDetail(pid, latitude, longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ProductDetailResponse>() {
                    override fun onSuccess(t: ProductDetailResponse) {
                        t.results?.let { it1 -> productDetailRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: MerchantListErrorResponse
                        try {
                            Log.d("Debug", "error Product Detail : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse = Gson().fromJson<MerchantListErrorResponse>(
                                body,
                                MerchantListErrorResponse::class.java
                            )
                        } catch (err: Throwable) {
                            errorResponse = MerchantListErrorResponse(
                                "now",
                                "503",
                                "Unavailable",
                                "Unavailable",
                                "Unavailable"
                            )
                        }

                        productDetailFail(errorResponse)
//                        createToastShort("${errorResponse.message} ${errorResponse.path}")
                        Log.d(
                            "Debug",
                            "error Product Detail : ${errorResponse.message} ${errorResponse.path}"
                        )
                    }
                })
        )
    }

    fun getCart() {
        cart.value = cartUtil.getCartStatus()
    }

    private fun productDetailRetrieved(productDetail: ProductDetail) {
        productDetailResponse.value = productDetail
        productLoadError.value = false
        loading.value = false
    }

    private fun productDetailFail(response: MerchantListErrorResponse) {
        productErrorResponse.value = response
        productLoadError.value = true
        loading.value = false
    }

    fun onAddProduct(
        pid: String,
        mid: String,
        pName: String,
        pImage: String,
        pPrice: String,
        context: Context
    ) {
        val isLoggingIn = sessionManager.isLoggingIn() ?: false
        if (isLoggingIn) {
            val args = Bundle()
            args.putString(MERCHANT_ID, mid)
            args.putString(PRODUCT_ID, pid)
            args.putString(PRODUCT_NAME, pName)
            args.putString(PRODUCT_IMAGE, pImage)
            args.putString(PRODUCT_PRICE, pPrice)
            val addToCartFragment = AddToCartFragment(this)
            addToCartFragment.arguments = args
            addToCartFragment.show(
                (context as HomeActivity).supportFragmentManager,
                addToCartFragment.tag
            )
        } else {
            val loginActivity = Intent(context, LoginActivity::class.java)
            context.startActivity(loginActivity)
        }
    }

    override fun onDialogDismiss() {
        getCart()
    }
}