package com.tsab.pikapp.viewmodel.store

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.view.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class StoreHomeViewModel(application: Application) : BaseViewModel(application) {

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()
    val loading = MutableLiveData<Boolean>()
    val logoutResponse = MutableLiveData<LogoutResponseV2>()
    val errorResponse = MutableLiveData<ErrorResponse>()

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    val merchantDetailResponse = MutableLiveData<MerchantDetail>()
    val loadingMerchantDetail = MutableLiveData<Boolean>()
    val merchantLoadError = MutableLiveData<Boolean>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()
    val notificationActive = MutableLiveData<Boolean>()

    private var latitude = ""
    private var longitude = ""

    fun getMid(): String = sessionManager.getUserData()!!.mid!!
    fun getMerchantDetail() {
        val mid = getMid()
        Log.e("Mid", mid)
        loadingMerchantDetail.value = true
        val location = prefHelper.getLatestLocation()
        location.let {
            latitude = it.latitude!!
            longitude = it.longitude!!
        }

        disposable.add(
                pikappService.getMerchantDetail(mid, "109382", "123456")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<MerchantDetailResponse>() {
                        override fun onSuccess(t: MerchantDetailResponse) {
                            t.results?.let { it1 -> merchantDetailRetrieved(it1) }
                        }

                        override fun onError(e: Throwable) {
                            var errorResponse: MerchantListErrorResponse
                            try {
                                Log.d("Debug", "error merchant detail : " + e + " ${e.message}")
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

    fun logout(context: Context) {
        val sessionId = sessionManager.getUserData()?.sessionId!!
        Log.d("debug","sessionid : $sessionId")
        logoutProcess(sessionId)
    }

    private fun logoutProcess(sessionid: String) {
        loading.value = true
        disposable.add(
                pikappService.logoutMerchant(sessionid)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<LogoutResponseV2>() {
                            override fun onSuccess(t: LogoutResponseV2) {
                                logoutSuccess(t)
                            }
                            override fun onError(e: Throwable) {
                                Log.d("Debug", "error : " + e)
                                var errorResponse: ErrorResponse
                                try {
                                    val responseBody = (e as HttpException)
                                    val body = responseBody.response()?.errorBody()?.string()
                                    errorResponse =
                                            Gson().fromJson(body, ErrorResponse::class.java)
                                } catch (err: Throwable) {
                                    errorResponse =
                                            ErrorResponse(
                                                    "503",
                                                    "Service Unavailable"
                                            )
                                }
                                logoutFail(errorResponse)
                            }

                        })
        )
    }

    private fun merchantDetailRetrieved(merchant: MerchantDetail) {
        merchantDetailResponse.value = merchant
        merchantLoadError.value = false
        loadingMerchantDetail.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
        loadingMerchantDetail.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun checkNotification() {
        val notif = prefHelper.getNotificationDetail()
//        prefHelper.deleteNotificationDetail()
        notif?.let {
            it.isMerchant?.let {isMerchant ->
                if (isMerchant) notificationActive.value = true
                prefHelper.deleteNotificationDetail()
            }
        }
    }

    fun goToOrderList(context: Context) {
        val storeOrderListActivity = Intent(context, StoreOrderListActivity::class.java)
        (context as StoreActivity).startActivity(storeOrderListActivity)
    }

    fun goToOrderList(context: Context, status: Int) {
        prefHelper.setOrderListTabSelected(status)
        val storeOrderListActivity = Intent(context, StoreOrderListActivity::class.java)
        (context as StoreActivity).startActivity(storeOrderListActivity)
    }

    private fun logoutSuccess(response: LogoutResponseV2) {
        logoutResponse.value = response
        loading.value = false
    }

    private fun logoutFail(response: ErrorResponse) {
        errorResponse.value = response
        loading.value = false
    }

    fun clearSessionExclusive(context: Context) {
        sessionManager.logout()
        goToOnboarding(context)
    }

    private fun goToOnboarding(context: Context) {
        val onboardingActivity = Intent(context, CarouselActivity::class.java)
        context?.startActivity(onboardingActivity)
        (context as StoreActivity).finish()
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }
}