package com.tsab.pikapp.viewmodel.orderList

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.GetOrderListResponse
import com.tsab.pikapp.models.model.OrderList
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.CONST_TRANSACTION_ID
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.OrderListActivity
import com.tsab.pikapp.view.orderList.orderListDetail.OrderListDetailFragment
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OrderListCohortViewModel(application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    val loading = MutableLiveData<Boolean>()
    val orderListRetrieve = MutableLiveData<Boolean>()

    val unpaid = MutableLiveData<List<OrderList>>()
    val prepared = MutableLiveData<List<OrderList>>()
    val ready = MutableLiveData<List<OrderList>>()
    val finish = MutableLiveData<List<OrderList>>()

    val errorResponse = MutableLiveData<ErrorResponse>()

    fun getOrderList() {
        prefHelper.clearOrderList()
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        loading.value = true
        disposable.add(
            pikappService.getTransactionList(email, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetOrderListResponse>() {
                    override fun onSuccess(t: GetOrderListResponse) {
                        t.results?.let { it1 -> orderListRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug", "error merchant detail : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson(
                                    body,
                                    ErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse("503", "Unavailable")
                        }

                        orderListFail(errorResponse)
                        Log.d(
                            "Debug",
                            "error merchant detail : ${errorResponse.errCode} ${errorResponse.errMessage}"
                        )
                    }
                })
        )
    }

    private fun orderListRetrieved(orderList: List<OrderList>) {
        if (orderList.isEmpty()) {
            orderListRetrieve.value = false
            loading.value = false
        } else {
            prefHelper.setOrderList(orderList)
            orderListRetrieve.value = true
        }
    }

    private fun orderListFail(err: ErrorResponse) {
        loading.value = false
        errorResponse.value = err
    }

    fun setUnpaid() {
        val orderList = prefHelper.getOrderList()
        val bufferOrderList = arrayListOf<OrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for (order in it) {
                    if (order.status == "OPEN") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        unpaid.value = bufferOrderList
    }

    fun setPrepared() {
        val orderList = prefHelper.getOrderList()
        val bufferOrderList = arrayListOf<OrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for (order in it) {
                    if (order.status == "PAID" || order.status == "MERCHANT_CONFIRM") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        prepared.value = bufferOrderList
    }

    fun setReady() {
        val orderList = prefHelper.getOrderList()
        val bufferOrderList = arrayListOf<OrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for (order in it) {
                    if (order.status == "FINALIZE") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        ready.value = bufferOrderList
    }

    fun setFinish() {
        val orderList = prefHelper.getOrderList()
        val bufferOrderList = arrayListOf<OrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for (order in it) {
                    if (order.status == "CLOSE") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        finish.value = bufferOrderList
    }

    fun goToOrderListDetail(transactionID: String, context: Context) {
        val args = Bundle()
        args.putString(CONST_TRANSACTION_ID, transactionID)
        val orderListDetailFragment = OrderListDetailFragment()
        orderListDetailFragment.arguments = args
        orderListDetailFragment.show(
            (context as OrderListActivity).supportFragmentManager,
            orderListDetailFragment.tag
        )
    }

}