package com.tsab.pikapp.viewmodel.orderList

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.GetOrderDetailResponse
import com.tsab.pikapp.models.model.OrderDetail
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OrderListDetailViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val orderDetail = MutableLiveData<OrderDetail>()
    val loading = MutableLiveData<Boolean>()

    fun getTransactionDetail() {
        loading.value = true
        val notif = prefHelper.getNotificationDetail()
        notif?.let {
            it.transactionId?.let { txnID ->
                if (txnID.isNotEmpty()) {
                    getTransactionDetail(txnID)
                    prefHelper.deleteNotificationDetail()
                }
            }
        }
    }

    fun getTransactionDetail(txnID: String) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        loading.value = true
        disposable.add(
            pikappService.getTransactionDetail(email, token, txnID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetOrderDetailResponse>() {
                    override fun onSuccess(t: GetOrderDetailResponse) {
                        t.results?.let { it1 -> orderDetailRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        val errorResponse = try {
                            Log.d("Debug", "error order detail : " + e + " ${e.message}")
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            Gson().fromJson(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            ErrorResponse("now", "Unavailable")
                        }

                        orderDetailFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.errCode} ${errorResponse.errMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Debug",
                            "error order detail : ${errorResponse.errCode} ${errorResponse.errMessage}"
                        )
                    }
                })
        )
    }

    private fun orderDetailRetrieved(od: OrderDetail) {
        loading.value = false
        if (od.transactionID!!.isNotEmpty()) {
            orderDetail.value = od
        }
    }

    private fun orderDetailFail(err: ErrorResponse) {

    }
}