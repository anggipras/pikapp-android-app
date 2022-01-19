package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.CustomProgressDialog
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.lang.Exception

class TransactionViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    private val onlineService = OnlineService()
    private var sessionManager = SessionManager(getApplication())
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    val mutablePikappFilter = MutableLiveData<Boolean>(false)
    val pikappFilter: LiveData<Boolean> get() = mutablePikappFilter

    val mutableGrabFilter = MutableLiveData<Boolean>(false)
    val grabFilter: LiveData<Boolean> get() = mutableGrabFilter

    val mutableWhatsappFilter = MutableLiveData<Boolean>(false)
    val whatsappFilter: LiveData<Boolean> get() = mutableWhatsappFilter

    val mutableTelpFilter = MutableLiveData<Boolean>(false)
    val telpFilter: LiveData<Boolean> get() = mutableTelpFilter

    val mutableTokpedFilter = MutableLiveData<Boolean>(false)
    val tokpedFilter: LiveData<Boolean> get() = mutableTokpedFilter

    val mutableShopeeFilter = MutableLiveData<Boolean>(false)
    val shopeeFilter: LiveData<Boolean> get() = mutableShopeeFilter

    val mutableAllFilterColor = MutableLiveData(false)
    val allFilterColor: LiveData<Boolean> get() = mutableAllFilterColor
    fun setAllFiterColor(bool: Boolean) {
        mutableAllFilterColor.value = bool
    }

    private val mutableErrorLoading = MutableLiveData(false)
    val errorLoading: LiveData<Boolean> = mutableErrorLoading
    fun setErrorLoading(isLoading: Boolean) {
        mutableErrorLoading.value = isLoading
    }

    private val mutableErrCode = MutableLiveData("")
    val errCode: LiveData<String> get() = mutableErrCode

    /* TRANSACTION LIST V2 */
    private var liveDataTransListV2Process: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataTransListV2Done: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataTransListV2Cancel: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()

    private var liveDataTransListV2ProcessFilter: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataFilterStatus: MutableLiveData<List<FilterMockUp>> = MutableLiveData()

    private val progressDialog = CustomProgressDialog()

    fun setProgressDialog(action: Boolean, context: Context) {
        if (action) {
            progressDialog.show(context)
        } else {
            progressDialog.dialog.dismiss()
        }
    }

    private val mutableProgressLoading = MutableLiveData(false)
    val progressLoading: LiveData<Boolean> = mutableProgressLoading
    fun setProgressLoading(isLoading: Boolean) {
        mutableProgressLoading.value = isLoading
    }

    private val mutableSizeFilter = MutableLiveData(0)
    val sizeFilter: LiveData<Int> = mutableSizeFilter
    private fun setSizeOfFilter(size: Int) {
        mutableSizeFilter.value = size
    }

    private val mutableProcessSize = MutableLiveData(0)
    val processSize: LiveData<Int> get() = mutableProcessSize
    private val mutableDoneSize = MutableLiveData(0)
    val doneSize: LiveData<Int> get() = mutableDoneSize
    private val mutableCancelSize = MutableLiveData(0)
    val cancelSize: LiveData<Int> get() = mutableCancelSize

    private val mutableEmptyState = MutableLiveData<Boolean>()
    val emptyState: LiveData<Boolean> get() = mutableEmptyState

    fun getLiveDataTransListV2ProcessObserver(): MutableLiveData<List<TransactionListV2Data>> {
        return liveDataTransListV2Process
    }

    fun getLiveDataTransListV2DoneObserver(): MutableLiveData<List<TransactionListV2Data>> {
        return liveDataTransListV2Done
    }

    fun getLiveDataTransListV2CancelObserver(): MutableLiveData<List<TransactionListV2Data>> {
        return liveDataTransListV2Cancel
    }

    fun getLiveDataTransListV2ProcessFilterObserver(): MutableLiveData<List<TransactionListV2Data>> {
        return liveDataTransListV2ProcessFilter
    }

    fun getTransactionV2List(context: Context, getOrUpdate: Boolean) {
        if (getOrUpdate) {
            setProgressLoading(true)
            setProgressDialog(true, context)
        }
        var mid = sessionManager.getUserData()?.mid
        var size = 100
        var page = 0

        viewModelScope.launch {
            try {
                PikappApiService().api.getTransactionListV2(mid, size, page).enqueue(object : Callback<TransactionListV2RespAPI> {
                    override fun onResponse(
                        call: Call<TransactionListV2RespAPI>,
                        response: Response<TransactionListV2RespAPI>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            if (response.body()!!.results?.isNotEmpty() == true) {
                                val theTransactionListV2 = response.body()!!.results

                                val processList: MutableList<TransactionListV2Data> = ArrayList()
                                val doneList: MutableList<TransactionListV2Data> = ArrayList()
                                val cancelList: MutableList<TransactionListV2Data> = ArrayList()

                                theTransactionListV2!!.forEach {
                                    if (it.txn_type == "TXN") { // PIKAPP DINE IN
                                        if (it.order_status == "OPEN" || it.order_status == "PAID" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(0, it))
                                        } else if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(0, it))
                                        } else if (it.order_status == "FAILED" || it.order_status == "ERROR" ) {
                                            cancelList.add(addTransactionData(0, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else if (it.txn_type == "CHANNEL") { // PIKAPP OMNICHANNEL
                                        if (it.order_status == "PAYMENT_CONFIRMATION"
                                            || it.order_status == "PAYMENT_VERIFIED"
                                            || it.order_status == "SELLER_ACCEPT_ORDER"
                                            || it.order_status == "WAITING_FOR_PICKUP"
                                            || it.order_status == "DRIVER_ALLOCATED"
                                            || it.order_status == "DRIVER_ARRIVED") {
                                            processList.add(addTransactionData(1, it))
                                        } else if (it.order_status == "ORDER_DELIVERED"
                                            || it.order_status == "ORDER_FINISHED"
                                            || it.order_status == "ORDER_SHIPMENT"
                                            || it.order_status == "DELIVERED_TO_PICKUP_POINT"
                                            || it.order_status == "DELIVERED"
                                            || it.order_status == "COLLECTED") {
                                            doneList.add(addTransactionData(1, it))
                                        } else if (it.order_status == "SELLER_CANCEL_ORDER"
                                            || it.order_status == "ORDER_REJECTED_BY_SELLER"
                                            || it.order_status == "CANCELLED"
                                            || it.order_status == "FAILED") {
                                            cancelList.add(addTransactionData(1, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else { // PIKAPP DELIVERY
                                        if (it.order_status == "OPEN" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(2, it))
                                        } else if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(2, it))
                                        } else if (it.order_status == "CANCELLED" || it.order_status == "CANCELLED") {
                                            cancelList.add(addTransactionData(2, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    }
                                }
                                mutableProcessSize.value = processList.size
                                mutableDoneSize.value = doneList.size
                                mutableCancelSize.value = cancelList.size

                                liveDataTransListV2Process.postValue(processList)
                                liveDataTransListV2Done.postValue(doneList)
                                liveDataTransListV2Cancel.postValue(cancelList)
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TransactionListV2RespAPI>, t: Throwable) {
                        setProgressLoading(false)
                        setProgressDialog(false, context)
                        setErrorLoading(true)
                    }

                })
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    private fun addTransactionData(viewType: Int, it: TransactionListV2Response) : TransactionListV2Data {
        return TransactionListV2Data(
            viewType = viewType,
            txn_type = it.txn_type,
            order_id = it.order_id,
            merchant_name = it.merchant_name,
            shop_id = it.shop_id,
            table_no = it.table_no,
            channel = it.channel,
            mid = it.mid,
            biz_type = it.biz_type,
            order_platform = it.order_platform,
            payment_method = it.payment_method,
            order_status = it.order_status,
            payment_status = it.payment_status,
            total_product_price = it.total_product_price,
            total_discount = it.total_discount,
            total_payment = it.total_payment,
            total_insurance_cost = it.total_insurance_cost,
            voucher_type = it.voucher_type,
            voucher_code = it.voucher_code,
            transaction_id = it.transaction_id,
            transaction_time = it.transaction_time,
            shipping = it.shipping,
            products = it.products,
            customer = it.customer
        )
    }

    fun filterOnProcess(txnType: String, orderPlatform: String, action: Boolean) {
        when(action) {
            true -> {
                var addFilterList: MutableList<FilterMockUp> = ArrayList()
                if (!liveDataFilterStatus.value.isNullOrEmpty()) {
                    liveDataFilterStatus.value!!.forEach {
                        addFilterList.add(it)
                    }
                }
                addFilterList.add(FilterMockUp(txnType = txnType, orderPlatform = orderPlatform))
                liveDataFilterStatus.value = addFilterList
            }
            else -> {
                var removeFilterList: List<FilterMockUp>?
                val filteredData = liveDataFilterStatus.value?.filter {
                    !(txnType == it.txnType && orderPlatform == it.orderPlatform)
                }
                removeFilterList = filteredData
                liveDataFilterStatus.value = removeFilterList
            }
        }
        filterTransactionV2ListProcess()
    }

    fun filterOnBottomSheet(txnType: String, orderPlatform: String, action: Boolean) {
        when(action) {
            true -> {
                var addFilterList: MutableList<FilterMockUp> = ArrayList()
                if (!liveDataFilterStatus.value.isNullOrEmpty()) {
                    liveDataFilterStatus.value!!.forEach {
                        addFilterList.add(it)
                    }
                }
                addFilterList.add(FilterMockUp(txnType = txnType, orderPlatform = orderPlatform))
                liveDataFilterStatus.value = addFilterList
            }
            else -> {
                var removeFilterList: List<FilterMockUp>?
                val filteredData = liveDataFilterStatus.value?.filter {
                    !(txnType == it.txnType && orderPlatform == it.orderPlatform)
                }
                removeFilterList = filteredData
                liveDataFilterStatus.value = removeFilterList
            }
        }
        filterProcessBottomSheet()
    }

    private fun filterProcessBottomSheet() {
        if (liveDataFilterStatus.value.isNullOrEmpty()) {
            liveDataTransListV2Process.value?.let {
                setSizeOfFilter(it.size)
            }
        } else {
            var filteredList: MutableList<TransactionListV2Data> = ArrayList()

            liveDataTransListV2Process.value?.forEach {
                liveDataFilterStatus.value?.forEach { mockUp ->
                    if (it.order_status == "OPEN"
                        || it.order_status == "PAID"
                        || it.order_status == "ON_PROCESS"
                        || it.order_status == "PAYMENT_CONFIRMATION"
                        || it.order_status == "PAYMENT_VERIFIED"
                        || it.order_status == "SELLER_ACCEPT_ORDER"
                        || it.order_status == "WAITING_FOR_PICKUP"
                        || it.order_status == "DRIVER_ALLOCATED"
                        || it.order_status == "DRIVER_ARRIVED") {
                        if (it.txn_type == mockUp.txnType) {
                            if (it.txn_type == "TXN") {
                                filteredList.add(it)
                            } else if (it.order_platform == mockUp.orderPlatform) {
                                filteredList.add(it)
                            }
                        }
                    }
                }
            }
            setSizeOfFilter(filteredList.size)
        }
    }

    fun filterTransactionV2ListProcess() {
        if (liveDataFilterStatus.value.isNullOrEmpty()) {
            liveDataTransListV2Process.postValue(liveDataTransListV2Process.value)
        } else {
            var filteredList: MutableList<TransactionListV2Data> = ArrayList()

            liveDataTransListV2Process.value?.forEach {
                liveDataFilterStatus.value?.forEach { mockUp ->
                    if (it.order_status == "OPEN"
                        || it.order_status == "PAID"
                        || it.order_status == "ON_PROCESS"
                        || it.order_status == "PAYMENT_CONFIRMATION"
                        || it.order_status == "PAYMENT_VERIFIED"
                        || it.order_status == "SELLER_ACCEPT_ORDER"
                        || it.order_status == "WAITING_FOR_PICKUP"
                        || it.order_status == "DRIVER_ALLOCATED"
                        || it.order_status == "DRIVER_ARRIVED") {
                        if (it.txn_type == mockUp.txnType) {
                            if (it.txn_type == "TXN") {
                                filteredList.add(it)
                            } else if (it.order_platform == mockUp.orderPlatform) {
                                filteredList.add(it)
                            }
                        }
                    }
                }
            }
            liveDataTransListV2ProcessFilter.postValue(filteredList)
        }
    }

    fun transactionTxnUpdate(
        id: String,
        status: String,
        context: Context
    ) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
            pikappService.postUpdateOrderStatus(
                email,
                token,
                RequestBody.create(MediaType.parse("multipart/form-data"), id),
                RequestBody.create(MediaType.parse("multipart/form-data"), status)
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>() {
                    override fun onSuccess(t: UpdateStatusResponse) {
                        Toast.makeText(context, "Transaksi Berhasil Di Update", Toast.LENGTH_SHORT).show()
                        getProcessTransactionV2List(
                            context,
                            false
                        )
                        getDoneTransactionV2List(
                            context,
                            false
                        )
                        getCancelTransactionV2List(
                            context,
                            false
                        )
                    }

                    override fun onError(e: Throwable) {
                        Log.e("TXNUPDATE", "Gagal update txn")
                    }
                })
        )
    }

    fun transactionChannelUpdate(
        channel: String,
        orderId: String,
        context: Context
    ) {
        Log.e("channel", channel)
        Log.e("orderId", orderId)
        val mid = sessionManager.getUserData()!!.mid!!
        var acceptOrderReq = AcceptOrderTokopediaRequest()
        acceptOrderReq.channel = channel
        acceptOrderReq.order_id = orderId
        acceptOrderReq.mid = mid

        PikappApiService().api.acceptOrderTokopedia(
            getUUID(), getTimestamp(), getClientID(), acceptOrderReq
        ).enqueue(object : Callback<AcceptOrderTokopediaResponse> {
            override fun onFailure(call: Call<AcceptOrderTokopediaResponse>, t: Throwable) {
                Toast.makeText(context, "fail: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<AcceptOrderTokopediaResponse>,
                response: Response<AcceptOrderTokopediaResponse>
            ) {
                Toast.makeText(context, "Transaksi Berhasil Di Update", Toast.LENGTH_SHORT).show()
                getProcessTransactionV2List(
                    context,
                    false
                )
                getDoneTransactionV2List(
                    context,
                    false
                )
                getCancelTransactionV2List(
                    context,
                    false
                )
            }
        })
    }

    fun transactionPosUpdate(
        reqBodyStatus: UpdateStatusManualTxnRequest,
        context: Context
    ) {
        PikappApiService().api.postUpdateManualTransaction(reqBodyStatus).enqueue(object : Callback<UpdateStatusManualResponse>{
            override fun onResponse(
                call: Call<UpdateStatusManualResponse>,
                response: Response<UpdateStatusManualResponse>
            ) {
                Toast.makeText(context, "Transaksi Berhasil Di Update", Toast.LENGTH_SHORT).show()
                getProcessTransactionV2List(
                    context,
                    false
                )
                getDoneTransactionV2List(
                    context,
                    false
                )
                getCancelTransactionV2List(
                    context,
                    false
                )
            }

            override fun onFailure(call: Call<UpdateStatusManualResponse>, t: Throwable) {
                Log.e("POSUPDATE", "Gagal update pos")
            }
        })
    }

    fun getBadgesTransactionV2List(context: Context) {
        var mid = sessionManager.getUserData()?.mid
        var size = 100
        var page = 0

        viewModelScope.launch {
            try {
                PikappApiService().api.getTransactionListV2(mid, size, page).enqueue(object : Callback<TransactionListV2RespAPI> {
                    override fun onResponse(
                        call: Call<TransactionListV2RespAPI>,
                        response: Response<TransactionListV2RespAPI>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            if (response.body()!!.results?.isNotEmpty() == true) {
                                val theTransactionListV2 = response.body()!!.results
                                val processList: MutableList<TransactionListV2Data> = ArrayList()
                                val doneList: MutableList<TransactionListV2Data> = ArrayList()
                                val cancelList: MutableList<TransactionListV2Data> = ArrayList()

                                theTransactionListV2!!.forEach {
                                    if (it.txn_type == "TXN") { // PIKAPP DINE IN
                                        if (it.order_status == "OPEN" || it.order_status == "PAID" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(0, it))
                                        } else if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(0, it))
                                        } else if (it.order_status == "FAILED" || it.order_status == "ERROR" ) {
                                            cancelList.add(addTransactionData(0, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else if (it.txn_type == "CHANNEL") { // PIKAPP OMNICHANNEL
                                        if (it.order_status == "PAYMENT_CONFIRMATION"
                                            || it.order_status == "PAYMENT_VERIFIED"
                                            || it.order_status == "SELLER_ACCEPT_ORDER"
                                            || it.order_status == "WAITING_FOR_PICKUP"
                                            || it.order_status == "DRIVER_ALLOCATED"
                                            || it.order_status == "DRIVER_ARRIVED") {
                                            processList.add(addTransactionData(1, it))
                                        } else if (it.order_status == "ORDER_DELIVERED"
                                            || it.order_status == "ORDER_FINISHED"
                                            || it.order_status == "ORDER_SHIPMENT"
                                            || it.order_status == "DELIVERED_TO_PICKUP_POINT"
                                            || it.order_status == "DELIVERED"
                                            || it.order_status == "COLLECTED") {
                                            doneList.add(addTransactionData(1, it))
                                        } else if (it.order_status == "SELLER_CANCEL_ORDER"
                                            || it.order_status == "ORDER_REJECTED_BY_SELLER"
                                            || it.order_status == "CANCELLED"
                                            || it.order_status == "FAILED") {
                                            cancelList.add(addTransactionData(1, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else { // PIKAPP DELIVERY
                                        if (it.order_status == "OPEN" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(2, it))
                                        } else if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(2, it))
                                        } else if (it.order_status == "CANCELLED" || it.order_status == "CANCELLED") {
                                            cancelList.add(addTransactionData(2, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    }
                                }
                                mutableProcessSize.value = processList.size
                                mutableDoneSize.value = doneList.size
                                mutableCancelSize.value = cancelList.size
                            }
                        }
                    }

                    override fun onFailure(call: Call<TransactionListV2RespAPI>, t: Throwable) {
                        Log.e(tag, "FAILURE ON GET LIST DATA")
                    }

                })
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun getProcessTransactionV2List(
        context: Context,
        getOrUpdate: Boolean
    ) {
        if (getOrUpdate) {
            setProgressLoading(true)
            setProgressDialog(true, context)
        }
        mutableEmptyState.value = false
        val mid = sessionManager.getUserData()?.mid
        val size = 100
        val page = 0

        viewModelScope.launch {
            try {
                PikappApiService().api.getTransactionListV2(mid, size, page).enqueue(object : Callback<TransactionListV2RespAPI> {
                    override fun onResponse(
                        call: Call<TransactionListV2RespAPI>,
                        response: Response<TransactionListV2RespAPI>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            if (response.body()!!.results?.isNotEmpty() == true) {
                                val theTransactionListV2 = response.body()!!.results
                                val processList: MutableList<TransactionListV2Data> = ArrayList()

                                theTransactionListV2!!.forEach {
                                    if (it.txn_type == "TXN") { // PIKAPP DINE IN
                                        if (it.order_status == "OPEN" || it.order_status == "PAID" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(0, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else if (it.txn_type == "CHANNEL") { // PIKAPP OMNICHANNEL
                                        if (it.order_status == "PAYMENT_CONFIRMATION"
                                            || it.order_status == "PAYMENT_VERIFIED"
                                            || it.order_status == "SELLER_ACCEPT_ORDER"
                                            || it.order_status == "WAITING_FOR_PICKUP"
                                            || it.order_status == "DRIVER_ALLOCATED"
                                            || it.order_status == "DRIVER_ARRIVED") {
                                            processList.add(addTransactionData(1, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else { // PIKAPP DELIVERY
                                        if (it.order_status == "OPEN" || it.order_status == "ON_PROCESS") {
                                            processList.add(addTransactionData(2, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    }
                                }
                                mutableProcessSize.value = processList.size
                                liveDataTransListV2Process.postValue(processList)
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                            } else {
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                                mutableEmptyState.value = true
                            }
                        }
                    }

                    override fun onFailure(call: Call<TransactionListV2RespAPI>, t: Throwable) {
                        setProgressLoading(false)
                        setProgressDialog(false, context)
                        setErrorLoading(true)
                    }

                })
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun getDoneTransactionV2List(
        context: Context,
        getOrUpdate: Boolean
    ) {
        if (getOrUpdate) {
            setProgressLoading(true)
            setProgressDialog(true, context)
        }
        mutableEmptyState.value = false
        val mid = sessionManager.getUserData()?.mid
        val size = 100
        val page = 0

        viewModelScope.launch {
            try {
                PikappApiService().api.getTransactionListV2(mid, size, page).enqueue(object : Callback<TransactionListV2RespAPI> {
                    override fun onResponse(
                        call: Call<TransactionListV2RespAPI>,
                        response: Response<TransactionListV2RespAPI>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            if (response.body()!!.results?.isNotEmpty() == true) {
                                val theTransactionListV2 = response.body()!!.results
                                val doneList: MutableList<TransactionListV2Data> = ArrayList()

                                theTransactionListV2!!.forEach {
                                    if (it.txn_type == "TXN") { // PIKAPP DINE IN
                                        if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(0, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else if (it.txn_type == "CHANNEL") { // PIKAPP OMNICHANNEL
                                        if (it.order_status == "ORDER_DELIVERED"
                                            || it.order_status == "ORDER_FINISHED"
                                            || it.order_status == "ORDER_SHIPMENT"
                                            || it.order_status == "DELIVERED_TO_PICKUP_POINT"
                                            || it.order_status == "DELIVERED"
                                            || it.order_status == "COLLECTED") {
                                            doneList.add(addTransactionData(1, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else { // PIKAPP DELIVERY
                                        if (it.order_status == "DELIVER" || it.order_status == "CLOSE" || it.order_status == "FINALIZE") {
                                            doneList.add(addTransactionData(2, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    }
                                }
                                mutableDoneSize.value = doneList.size
                                liveDataTransListV2Done.postValue(doneList)
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                            } else {
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                                mutableEmptyState.value = true
                            }
                        }
                    }

                    override fun onFailure(call: Call<TransactionListV2RespAPI>, t: Throwable) {
                        setProgressLoading(false)
                        setProgressDialog(false, context)
                        setErrorLoading(true)
                    }

                })
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun getCancelTransactionV2List(
        context: Context,
        getOrUpdate: Boolean
    ) {
        if (getOrUpdate) {
            setProgressLoading(true)
            setProgressDialog(true, context)
        }
        mutableEmptyState.value = false
        val mid = sessionManager.getUserData()?.mid
        val size = 100
        val page = 0

        viewModelScope.launch {
            try {
                PikappApiService().api.getTransactionListV2(mid, size, page).enqueue(object : Callback<TransactionListV2RespAPI> {
                    override fun onResponse(
                        call: Call<TransactionListV2RespAPI>,
                        response: Response<TransactionListV2RespAPI>
                    ) {
                        if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                            if (response.body()!!.results?.isNotEmpty() == true) {
                                val theTransactionListV2 = response.body()!!.results
                                val cancelList: MutableList<TransactionListV2Data> = ArrayList()

                                theTransactionListV2!!.forEach {
                                    if (it.txn_type == "TXN") { // PIKAPP DINE IN
                                        if (it.order_status == "FAILED" || it.order_status == "ERROR" ) {
                                            cancelList.add(addTransactionData(0, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else if (it.txn_type == "CHANNEL") { // PIKAPP OMNICHANNEL
                                        if (it.order_status == "SELLER_CANCEL_ORDER"
                                            || it.order_status == "ORDER_REJECTED_BY_SELLER"
                                            || it.order_status == "CANCELLED"
                                            || it.order_status == "FAILED") {
                                            cancelList.add(addTransactionData(1, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    } else { // PIKAPP DELIVERY
                                        if (it.order_status == "CANCELLED" || it.order_status == "CANCELLED") {
                                            cancelList.add(addTransactionData(2, it))
                                        } else {
                                            Timber.tag(tag).d("Invalid transaction")
                                        }
                                    }
                                }
                                mutableCancelSize.value = cancelList.size
                                liveDataTransListV2Cancel.postValue(cancelList)
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                            } else {
                                setProgressLoading(false)
                                setProgressDialog(false, context)
                                mutableEmptyState.value = true
                            }
                        }
                    }

                    override fun onFailure(call: Call<TransactionListV2RespAPI>, t: Throwable) {
                        setProgressLoading(false)
                        setProgressDialog(false, context)
                        setErrorLoading(true)
                    }

                })
            } catch (e: Exception) {
                Log.e("ERROR", e.message.toString())
            }
        }
    }

    fun restartFragment() {
        mutablePikappFilter.value = false
        mutableTokpedFilter.value = false
        mutableGrabFilter.value = false
        mutableShopeeFilter.value = false
        mutableWhatsappFilter.value = false
        mutableTelpFilter.value = false
    }

    /* ADDITION FOR DUMMY TESTING */
    private fun readJson(context: Context, fileName: String): String? {
        var jsonString: String

        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    fun mapListData(context: Context, fileName: String) {
        val theJson = readJson(context, fileName)
        val gson = Gson()
        val listTransac = object : TypeToken<List<TransactionListV2Response>>() {}.type
        var theTransactionListV2: List<TransactionListV2Response> = gson.fromJson(theJson, listTransac)
    }
}
