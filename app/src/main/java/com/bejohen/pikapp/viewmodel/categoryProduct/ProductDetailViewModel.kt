package com.bejohen.pikapp.viewmodel.categoryProduct

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.home.ProfileFragment
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException


class ProductDetailViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loading = MutableLiveData<Boolean>()
    val productDetailResponse = MutableLiveData<ProductDetail>()
    val productLoadError = MutableLiveData<Boolean>()
    val productErrorResponse = MutableLiveData<MerchantListErrorResponse>()

    var latitude = ""
    var longitude = ""

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
                            errorResponse =
                                Gson().fromJson<MerchantListErrorResponse>(body, MerchantListErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse = MerchantListErrorResponse(
                                "now", "503", "Unavailable", "Unavailable", "Unavailable"
                            )
                        }

                        productDetailFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.message} ${errorResponse.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Debug", "error Product Detail : ${errorResponse.message} ${errorResponse.path}")
                    }

                })
        )
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

    fun onAddProduct(pid: String, mid: String, context : Context) {
        val isLoggingIn = sessionManager.isLoggingIn() ?: false
        if (isLoggingIn) {

            val profileFragment = ProfileFragment()
            profileFragment.show((context as HomeActivity).supportFragmentManager, profileFragment.getTag())
        } else {
            val loginActivity = Intent(context, LoginActivity::class.java)
            context.startActivity(loginActivity)
        }
    }
}