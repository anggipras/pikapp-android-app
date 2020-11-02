package com.tsab.pikapp.viewmodel.storeOrderList

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.CONST_TABLE_NO
import com.tsab.pikapp.util.CONST_TRANSACTION_ID
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.StoreOrderListActivity
import com.tsab.pikapp.view.storeOrderList.StoreOrderListDetailFragment
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException

class StoreOrderListCohortViewModel(application: Application) : BaseViewModel(application), StoreOrderListDetailFragment.StoreOrderListDetailInterface {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    val loading = MutableLiveData<Boolean>()
    val storeOrderListRetrieve = MutableLiveData<Boolean>()

    val prepared = MutableLiveData<List<StoreOrderList>>()
    val ready = MutableLiveData<List<StoreOrderList>>()
    val finish = MutableLiveData<List<StoreOrderList>>()

    val errorResponse = MutableLiveData<ErrorResponse>()

    val updateStatus = MutableLiveData<Boolean>()

    fun getStoreOrderList() {
        prefHelper.clearStoreOrderList()
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        loading.value = true

        disposable.add(
            pikappService.getTransactionListMerchant(email, token, mid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetStoreOrderListResponse>() {
                    override fun onSuccess(t: GetStoreOrderListResponse) {
                        t.results?.let { it1 -> storeOrderListRetrieved(it1) }
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

                        storeOrderListFail(errorResponse)
                        Log.d("Debug", "error merchant detail : ${errorResponse.errCode} ${errorResponse.errMessage}")
                    }
                })
        )
    }

    private fun storeOrderListRetrieved(orderList: List<StoreOrderList>) {
        if (orderList.isEmpty()) {
            storeOrderListRetrieve.value = false
            loading.value = false
        } else {
            val bufferOrderList =  arrayListOf<StoreOrderList>()
            for(order in orderList) {
                if(order.status != "OPEN") {
                    bufferOrderList.add(order)
                }
            }
            if (bufferOrderList.isNotEmpty()) {
                prefHelper.setStoreOrderList(orderList)
                storeOrderListRetrieve.value = true
            } else {
                storeOrderListRetrieve.value = false
                loading.value = false
            }
        }
    }

    private fun storeOrderListFail(err: ErrorResponse) {
        loading.value = false
        errorResponse.value = err
    }

    fun setPrepared() {
        val orderList = prefHelper.getStoreOrderList()
        val bufferOrderList =  arrayListOf<StoreOrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for(order in it) {
                    if(order.status == "PAID" || order.status == "MERCHANT_CONFIRM") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }

        loading.value = false
        prepared.value = bufferOrderList
    }

    fun setReady() {
        val orderList = prefHelper.getStoreOrderList()
        val bufferOrderList =  arrayListOf<StoreOrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for(order in it) {
                    if(order.status == "FINALIZE") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        ready.value = bufferOrderList
    }

    fun setFinish() {
        val orderList = prefHelper.getStoreOrderList()
        val bufferOrderList =  arrayListOf<StoreOrderList>()
        orderList?.let {
            if (it.isNotEmpty()) {
                for(order in it) {
                    if(order.status == "CLOSE") {
                        bufferOrderList.add(order)
                    }
                }
            }
        }
        loading.value = false
        finish.value = bufferOrderList
    }

    fun updateOrderStatus(transactionID: String, status: String) {
        if(status == "PAID") {
            updateStatus(transactionID, "MERCHANT_CONFIRM")
        }
    }

    fun goToStoreOrderListDetail(transactionID: String, tableNo: String, context: Context) {
        val args = Bundle()
        args.putString(CONST_TRANSACTION_ID, transactionID)
        args.putString(CONST_TABLE_NO, tableNo)

        Log.d("Debug", "txn id : ${transactionID}, table no: ${tableNo}")
        val storeOrderListDetailFragment = StoreOrderListDetailFragment(this)
        storeOrderListDetailFragment.arguments = args
        storeOrderListDetailFragment.show((context as StoreOrderListActivity).supportFragmentManager, storeOrderListDetailFragment.tag)
    }

    private fun updateStatus(transactionID: String, newStatus: String) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        val txnID: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), transactionID)
        val status: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), newStatus)
        disposable.add(
            pikappService.postUpdateOrderStatus(email, token, txnID, status)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>() {
                    override fun onSuccess(t: UpdateStatusResponse) {
                        updateSuccess()
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

                        storeOrderListFail(errorResponse)
                        Log.d("Debug", "error merchant detail : ${errorResponse.errCode} ${errorResponse.errMessage}")
                    }
                })
        )
    }

    private fun updateSuccess() {
        updateStatus.value = true
    }

    override fun changeOrderStatus(txnID: String, stts: String) {
        if (stts == "MERCHANT_CONFIRM" ) {
            updateStatus(txnID, "FINALIZE")
        } else if (stts == "FINALIZE") {
            updateStatus(txnID, "CLOSE")
        }
    }

    override fun onDialogDismiss() {
        getStoreOrderList()
    }
}