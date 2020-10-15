package com.bejohen.pikapp.viewmodel.categoryProduct

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.*
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.categoryProduct.AddToCartFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class MerchantDetailViewModel(application: Application) : BaseViewModel(application), AddToCartFragment.DialogDismissInterface {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private var cartUtil = CartUtil(getApplication())
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loadingMerchantDetail = MutableLiveData<Boolean>()
    val loadingProductList = MutableLiveData<Boolean>()
    val merchantDetailResponse = MutableLiveData<MerchantDetail>()
    val productListResponse = MutableLiveData<List<ProductList>>()
    val merchantLoadError = MutableLiveData<Boolean>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()
    val cart = MutableLiveData<Boolean>()

    var latitude = ""
    var longitude = ""

    fun getMerchantDetail(mid: String) {
        prefHelper.deleteDeeplinkUrl()
        loadingMerchantDetail.value = true
        loadingProductList.value = true
        val location = prefHelper.getLatestLocation()
        location.let {
            latitude = it.latitude!!
            longitude = it.longitude!!
        }

        disposable.add(
            pikappService.getMerchantDetail(mid, latitude, longitude)
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
        disposable.add(
            pikappService.getProductList(mid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ProductListResponse>() {
                    override fun onSuccess(t: ProductListResponse) {
                        t.results?.let { it1 -> productListRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: MerchantListErrorResponse
                        try {
                            Log.d("Debug", "error Merchant Product List : " + e)
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
                            "error Merchant Product List : ${errorResponse.message} ${errorResponse.path}"
                        )
                    }
                })
        )
    }

    private fun merchantDetailRetrieved(merchant: MerchantDetail) {
        merchantDetailResponse.value = merchant
        merchantLoadError.value = false
        loadingMerchantDetail.value = false
    }

    private fun productListRetrieved(productList: List<ProductList>) {
        productListResponse.value = productList
        merchantLoadError.value = false
        loadingProductList.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
        loadingMerchantDetail.value = false
        loadingProductList.value = false
    }

    fun onAddProduct(mid: String, pid: String, pName: String, pImage: String, pPrice: String, context: Context) {
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

    fun getCart() {
        Log.d("Debug", "Cart status : ${cartUtil.getCartStatus()}")
        cart.value = cartUtil.getCartStatus()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    override fun onDialogDismiss() {
        getCart()
    }
}