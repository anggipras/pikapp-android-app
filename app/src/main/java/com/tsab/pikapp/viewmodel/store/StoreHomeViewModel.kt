package com.tsab.pikapp.viewmodel.store

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.MerchantDetail
import com.tsab.pikapp.models.model.MerchantDetailResponse
import com.tsab.pikapp.models.model.MerchantListErrorResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.view.StoreOrderListActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class StoreHomeViewModel(application: Application) : BaseViewModel(application) {

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

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
        loadingMerchantDetail.value = true
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
}