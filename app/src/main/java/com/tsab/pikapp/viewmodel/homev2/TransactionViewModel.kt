package com.tsab.pikapp.viewmodel.homev2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.transaction.FilterFragment
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.TransactionListAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

class TransactionViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName

    var activation: Boolean = true
    private val onlineService = OnlineService()

    lateinit var categoryAdapter: TransactionListAdapter
    lateinit var omniAdapter: OmniTransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    private val mutableSize = MutableLiveData(0)
    val size: LiveData<Int> get() = mutableSize

    private val mutableManualTrans = MutableLiveData<List<ManualTransactionResult>>()
    val manualTransList: LiveData<List<ManualTransactionResult>> get() = mutableManualTrans

    private val mutableManualPage = MutableLiveData(0)
    val manualPage: LiveData<Int> get() = mutableManualPage

    private val mutableProsesOmni = MutableLiveData(0)
    val prosesOmni: LiveData<Int> get() = mutableProsesOmni

    private val mutableBatalOmni = MutableLiveData(0)
    val batalOmni: LiveData<Int> get() = mutableBatalOmni

    private val mutableDoneOmni = MutableLiveData(0)
    val doneOmni: LiveData<Int> get() = mutableDoneOmni

    private val mutableProses = MutableLiveData(0)
    val proses: LiveData<Int> get() = mutableProses

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

    private val mutableFilter = MutableLiveData<RecyclerView>()
    val filter: LiveData<RecyclerView> get() = mutableFilter

    private val mutableString = MutableLiveData<TextView>()
    val string: LiveData<TextView> get() = mutableString

    private val mutableEmpty = MutableLiveData<ImageView>()
    val empty: LiveData<ImageView> get() = mutableEmpty

    private val mutableFilter1 = MutableLiveData<RecyclerView>()
    val filter1: LiveData<RecyclerView> get() = mutableFilter1

    private val mutableResult = MutableLiveData<ArrayList<String>>()
    val result: LiveData<ArrayList<String>> get() = mutableResult

    private val mutablePikapp = MutableLiveData<Button>()
    val pikapp: LiveData<Button> get() = mutablePikapp

    private val mutableTokped = MutableLiveData<Button>()
    val tokopedia: LiveData<Button> get() = mutableTokped

    private val mutableGrab = MutableLiveData<Button>()
    val Grab: LiveData<Button> get() = mutableGrab

    private val mutableShopee = MutableLiveData<Button>()
    val shopee: LiveData<Button> get() = mutableShopee

    private val mutableBatal = MutableLiveData(0)
    val batal: LiveData<Int> get() = mutableBatal

    val mutableCountTxn = MutableLiveData(0)
    val countTxn: LiveData<Int> get() = mutableCountTxn

    private val mutableDone = MutableLiveData(0)
    val done: LiveData<Int> get() = mutableDone

    private val mutableCategoryList = MutableLiveData<List<CategoryListResult>>(listOf())

    private val mutableIsLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val mutableAmountOfTransaction = MutableLiveData(0)
    val amountOfTransaction: LiveData<Int> = mutableAmountOfTransaction
    fun setAmountOfTrans(isAmount: Int) {
        mutableAmountOfTransaction.value = isAmount
    }

    private val mutableProcessBadges = MutableLiveData<Int?>()
    val processBadges: LiveData<Int?> = mutableProcessBadges
    fun setProcessBadges(badge: Int?) {
        mutableProcessBadges.value = badge
    }

    private val mutableDecreaseBadge = MutableLiveData<Int>()
    val decreaseBadge: LiveData<Int> = mutableDecreaseBadge
    fun setTotalProcessBadge(badge: Int) {
        mutableDecreaseBadge.value = badge
    }

    fun setDecreaseBadge(badge: Int?) {
        val totalProcessBadges = decreaseBadge.value
        if (totalProcessBadges != null) {
            mutableDecreaseBadge.value = totalProcessBadges - badge!!
        }
    }

    private val mutableCategoryName = MutableLiveData(" ")
    val categoryName: LiveData<String> get() = mutableCategoryName

    private val mutableErrCode = MutableLiveData("")
    val errCode: LiveData<String> get() = mutableErrCode

    fun editList(
        list: RecyclerView,
        list1: RecyclerView,
        pikapp: Button,
        tokped: Button,
        grab: Button,
        shopee: Button,
        empty: ImageView,
        string: TextView
    ) {
        mutableEmpty.value = empty
        mutableFilter.value = list
        mutableFilter1.value = list1
        mutablePikapp.value = pikapp
        mutableGrab.value = grab
        mutableString.value = string
        mutableTokped.value = tokped
        mutableShopee.value = shopee
    }

    var logisticList = ArrayList<LogisticsDetailOmni>()
    var logisticListDone = ArrayList<LogisticsDetailOmni>()
    var logisticListBatal = ArrayList<LogisticsDetailOmni>()
    var arrayResultList = ArrayList<OrderDetailDetailOmni>()

    fun filterOn(
        pikappStatus: Boolean,
        tokpedStatus: Boolean,
        grabStatus: Boolean,
        shopeeStatus: Boolean,
        size: Int
    ) {
        if (!pikappStatus && !tokpedStatus && !grabStatus && !shopeeStatus) {
            mutableCountTxn.value = 0
        } else {
            mutableCountTxn.value = size
        }

        mutablePikappFilter.value = pikappStatus
        mutableTokpedFilter.value = tokpedStatus
        mutableGrabFilter.value = grabStatus
        mutableShopeeFilter.value = shopeeStatus

        if (size == 0) {
            mutableEmpty.value!!.isVisible
            mutableString.value!!.isVisible
            mutableEmpty.value!!.visibility = View.VISIBLE
            mutableString.value!!.visibility = View.VISIBLE
        }

        if (pikappStatus) {
            mutableFilter.value?.visibility = View.VISIBLE
            mutablePikapp.value?.setBackgroundResource(R.drawable.button_green_square)
            mutablePikapp.value?.setTextColor(Color.parseColor("#ffffff"))
        } else if (!pikappStatus) {
            if (!tokpedStatus && !grabStatus && !shopeeStatus) {
                mutableFilter.value?.visibility = View.VISIBLE
                mutableFilter1.value?.visibility = View.VISIBLE
            } else {
                mutableFilter.value?.visibility = View.GONE
            }

            mutablePikapp.value?.setBackgroundResource(R.drawable.gray_square_btn)
            mutablePikapp.value?.setTextColor(Color.parseColor("#aaaaaa"))
        }

        if (tokpedStatus) {
            mutableFilter1.value?.visibility = View.VISIBLE
            mutableTokped.value?.setBackgroundResource(R.drawable.button_green_square)
            mutableTokped.value?.setTextColor(Color.parseColor("#ffffff"))
        } else if (!tokpedStatus) {
            if (!pikappStatus && !grabStatus && !shopeeStatus) {
                mutableFilter.value?.visibility = View.VISIBLE
                mutableFilter1.value?.visibility = View.VISIBLE
            } else {
                mutableFilter1.value?.visibility = View.GONE
            }

            mutableTokped.value?.setBackgroundResource(R.drawable.gray_square_btn)
            mutableTokped.value?.setTextColor(Color.parseColor("#aaaaaa"))
        }

        if (grabStatus) {
            mutableGrab.value?.setBackgroundResource(R.drawable.button_green_square)
            mutableGrab.value?.setTextColor(Color.parseColor("#ffffff"))
        } else if (!grabStatus) {
            if (!pikappStatus && !tokpedStatus && !shopeeStatus) {
                mutableFilter.value?.visibility = View.VISIBLE
                mutableFilter1.value?.visibility = View.VISIBLE
            }

            mutableGrab.value?.setBackgroundResource(R.drawable.gray_square_btn)
            mutableGrab.value?.setTextColor(Color.parseColor("#aaaaaa"))
        }

        if (shopeeStatus) {
            mutableShopee.value?.setBackgroundResource(R.drawable.button_green_square)
            mutableShopee.value?.setTextColor(Color.parseColor("#ffffff"))
        } else if (!shopeeStatus) {
            if (!pikappStatus && !grabStatus && !tokpedStatus) {
                mutableFilter.value?.visibility = View.VISIBLE
                mutableFilter1.value?.visibility = View.VISIBLE
            }

            mutableShopee.value?.setBackgroundResource(R.drawable.gray_square_btn)
            mutableShopee.value?.setTextColor(Color.parseColor("#aaaaaa"))
        }
    }

    fun getStoreOrderList(
        baseContext: Context,
        recyclerview_transaction: RecyclerView,
        status: String,
        support: FragmentManager,
        empty: ConstraintLayout,
        listener: TransactionListAdapter.OnItemClickListener,
        activity: Activity,
        general_error: View
    ) {
        setLoading(true)
        prefHelper.clearStoreOrderList()

        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        val timestamp = getTimestamp()

        val transReq =
            TransactionListRequest(page = 0, size = 1, transaction_id = "", status = listOf())

        PikappApiService().api.getTransactionListV2Merchant(
            getUUID(),
            timestamp,
            getClientID(),
            getSignature(email, timestamp),
            token,
            mid,
            transReq
        ).enqueue(object : Callback<GetStoreOrderListV2Response> {
            override fun onFailure(call: Call<GetStoreOrderListV2Response>, t: Throwable) {
                Timber.tag(tag).d("Failed to get total: ${t.message.toString()}")
                setLoading(false)
                onlineService.serviceDialog(activity)
                general_error.isVisible = true
            }

            override fun onResponse(
                call: Call<GetStoreOrderListV2Response>,
                response: Response<GetStoreOrderListV2Response>
            ) {
                val gson = Gson()
                val type = object : TypeToken<GetStoreOrderListV2Response>() {}.type
                general_error.isVisible = false
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val totalItemsTrans = response.body()!!.total_items
                    if (totalItemsTrans != 0) {
                        getStoreOrderAllList(
                            baseContext,
                            recyclerview_transaction,
                            status,
                            support,
                            empty,
                            totalItemsTrans,
                            listener
                        )
                    } else {
                        setLoading(false)
                    }
                } else {
                    val errorResponse: GetStoreOrderListV2Response? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Timber.tag(tag).d(errorResponse?.errCode)

                    Toast.makeText(
                        baseContext,
                        "Your account has been logged in to another device",
                        Toast.LENGTH_SHORT
                    ).show()

                    Timber.tag(tag).d("Logged out")
                    mutableErrCode.value = errorResponse?.errCode
                    setLoading(false)
                }
            }

        })
    }

    fun getStoreOrderAllList(
        baseContext: Context,
        recyclerview_transaction: RecyclerView,
        status: String,
        support: FragmentManager,
        empty: ConstraintLayout,
        totalItems: Int?,
        listener: TransactionListAdapter.OnItemClickListener
    ) {
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        var mid = sessionManager.getUserData()?.mid
        var timestamp = getTimestamp()
        var uuid = getUUID()
        var clientId = getClientID()
        var signature = getSignature(email, timestamp)

        val transReq = TransactionListRequest(
            page = 0,
            size = 18,
            transaction_id = "",
            status = listOf()
        )
        setProcessBadges(0)

        PikappApiService().api.getTransactionListV2Merchant(
            uuid,
            timestamp,
            clientId,
            signature,
            token,
            mid,
            transReq
        ).enqueue(object : Callback<GetStoreOrderListV2Response> {
            override fun onFailure(call: Call<GetStoreOrderListV2Response>, t: Throwable) {
                Timber.tag(tag).d("Failed to get list transaction: ${t.message.toString()}")
                setLoading(false)
            }

            override fun onResponse(
                call: Call<GetStoreOrderListV2Response>,
                response: Response<GetStoreOrderListV2Response>
            ) {
                val gson = Gson()
                val type = object : TypeToken<GetStoreOrderListV2Response>() {}.type

                val result = response.body()
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val transactionList = result?.results

                    val prosesList = ArrayList<StoreOrderList>()
                    val batalList = ArrayList<StoreOrderList>()
                    val doneList = ArrayList<StoreOrderList>()
                    val menuList = ArrayList<ArrayList<OrderDetailDetail>>()
                    val menuList1 = ArrayList<ArrayList<OrderDetailDetail>>()
                    val menuList2 = ArrayList<ArrayList<OrderDetailDetail>>()

                    if (transactionList != null) {
                        for (transaction in transactionList) {
                            if (transaction.status == "OPEN" || transaction.status == "PAID" || transaction.status == "ON_PROCESS") {
                                prosesList.add(transaction)
                                transaction.detailProduct?.let { menuList.add(it as ArrayList<OrderDetailDetail>) }
                            } else if (transaction.status == "FAILED" || transaction.status == "ERROR") {
                                batalList.add(transaction)
                                transaction.detailProduct?.let { menuList1.add(it as ArrayList<OrderDetailDetail>) }
                            } else if (transaction.status == "DELIVER" || transaction.status == "CLOSE" || transaction.status == "FINALIZE") {
                                doneList.add(transaction)
                                transaction.detailProduct?.let { menuList2.add(it as ArrayList<OrderDetailDetail>) }
                            } else {
                                Timber.tag(tag).d("Invalid transaction")
                            }
                        }
                    }

                    mutableProses.value = prosesList.size
                    mutableBatal.value = batalList.size
                    mutableDone.value = doneList.size

                    if (status == "Proses") {
                        categoryAdapter = TransactionListAdapter(
                            baseContext,
                            prosesList as MutableList<StoreOrderList>,
                            menuList as MutableList<List<OrderDetailDetail>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            listener
                        )
                        categoryAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = categoryAdapter
                    }

                    if (status == "Batal") {
                        empty.isVisible = batalList.isEmpty()
                        categoryAdapter = TransactionListAdapter(
                            baseContext,
                            batalList as MutableList<StoreOrderList>,
                            menuList1 as MutableList<List<OrderDetailDetail>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            listener
                        )
                        categoryAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = categoryAdapter
                    }

                    if (status == "Done") {
                        empty.isVisible = doneList.isEmpty()
                        categoryAdapter = TransactionListAdapter(
                            baseContext,
                            doneList as MutableList<StoreOrderList>,
                            menuList2 as MutableList<List<OrderDetailDetail>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            listener
                        )
                        categoryAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = categoryAdapter
                    }

                    setLoading(false)
                    val processSize = processBadges.value?.plus(prosesList.size)
                    if (processSize != null) {
                        setProcessBadges(processSize)
                    }

                    val countTrans = amountOfTransaction.value?.plus(1)
                    if (countTrans != null) {
                        setAmountOfTrans(countTrans)
                    }
                } else {
                    val errorResponse: GetStoreOrderListV2Response? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Timber.tag(tag).d("Error code: ${errorResponse?.errCode}")

                    Toast.makeText(
                        baseContext,
                        "Your account has been logged in to another device",
                        Toast.LENGTH_SHORT
                    ).show()

                    Timber.tag(tag).d("Logged out")
                    mutableErrCode.value = errorResponse?.errCode
                }
            }
        })
    }

    fun getListOmni(
        baseContext: Context,
        recyclerview_transaction: RecyclerView,
        support: FragmentManager,
        activity: Activity,
        status: String,
        empty: ConstraintLayout,
        lifecycle: Fragment,
        general_error: View
    ) {
        prefHelper.clearStoreOrderList()

        val mid = sessionManager.getUserData()!!.mid!!
        val page = "0"
        val size = "100"

        PikappApiService().api.getListOrderOmni(
            getUUID(), getTimestamp(), getClientID(), mid, page, size
        ).enqueue(object : Callback<ListOrderOmni> {
            override fun onFailure(call: Call<ListOrderOmni>, t: Throwable) {
                Toast.makeText(baseContext, "Error: $t", Toast.LENGTH_SHORT).show()
                Timber.tag(tag).d("Failed to get omni list: ${t.message.toString()}")
                onlineService.serviceDialog(activity)
                general_error.isVisible = true
            }

            override fun onResponse(call: Call<ListOrderOmni>, response: Response<ListOrderOmni>) {
                val responseBody = response.body()
                var mid = sessionManager.getUserData()?.mid
                var timestamp = getTimestamp()
                val email = sessionManager.getUserData()?.email
                var uuid = getUUID()
                val token = sessionManager.getUserToken()!!
                var clientId = getClientID()
                var signature = getSignature(email, timestamp)

                Log.e("UUId", uuid)
                Log.e("time", timestamp)
                Log.e("cid", clientId)
                Log.e("token", token)
                Log.e("sig", signature)
                Log.e("mid", mid.toString())

                val resultList = responseBody?.results
                val prosesList = ArrayList<OrderDetailOmni>()
                val batalList = ArrayList<OrderDetailOmni>()
                val doneList = ArrayList<OrderDetailOmni>()
                val productList = ArrayList<ArrayList<ProductDetailOmni>>()
                val productList1 = ArrayList<ArrayList<ProductDetailOmni>>()
                val productList2 = ArrayList<ArrayList<ProductDetailOmni>>()

                Timber.tag(tag).d("Response: ${responseBody?.errCode.toString()}")

                if (resultList != null) {
                    for (result in resultList) {
                        getOrderDetailOmni(result.orderId.toString())
                        if (result.status == "PAYMENT_CONFIRMATION"
                            || result.status == "PAYMENT_VERIFIED"
                            || result.status == "SELLER_ACCEPT_ORDER"
                            || result.status == "WAITING_FOR_PICKUP"
                            || result.status == "DRIVER_ALLOCATED"
                            || result.status == "DRIVER_ARRIVED"
                        ) {
                            prosesList.add(result)
                            result.producDetails.let { productList.add(it as ArrayList<ProductDetailOmni>) }
                        } else if (result.status == "SELLER_CANCEL_ORDER"
                            || result.status == "ORDER_REJECTED_BY_SELLER"
                            || result.status == "CANCELLED"
                            || result.status == "FAILED"
                        ) {
                            batalList.add(result)
                            result.producDetails?.let { productList1.add(it as ArrayList<ProductDetailOmni>) }
                        } else if (result.status == "ORDER_DELIVERED"
                                || result.status == "ORDER_FINISHED"
                                || result.status == "ORDER_SHIPMENT"
                                || result.status == "DELIVERED_TO_PICKUP_POINT"
                                || result.status == "DELIVERED"
                                || result.status == "COLLECTED"
                        ) {
                            doneList.add(result)
                            result.producDetails?.let { productList2.add(it as ArrayList<ProductDetailOmni>) }
                        } else {
                            Timber.tag(tag).d("Invalid transaction")
                        }
                    }

                    mutableProsesOmni.value = prosesList.size
                    val processSize = processBadges.value?.plus(prosesList.size)
                    if (processSize != null) {
                        setProcessBadges(processSize)
                    }

                    mutableBatalOmni.value = batalList.size
                    mutableDoneOmni.value = doneList.size
                } else {
                    Timber.tag(tag).d("Result is null")
                    general_error.isVisible = false
                }

                Handler().postDelayed({
                    when (status) {
                        "Proses" -> {
                            omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                prosesList as MutableList<OrderDetailOmni>,
                                productList as MutableList<List<ProductDetailOmni>>, arrayResultList as MutableList<OrderDetailDetailOmni>,
                                sessionManager,
                                support,
                                prefHelper,
                                recyclerview_transaction,
                                activity,
                                logisticList as MutableList<LogisticsDetailOmni>,
                                empty,
                                lifecycle
                            )
                            omniAdapter.notifyDataSetChanged()
                            recyclerview_transaction.adapter = omniAdapter
                        }
                        "Batal" -> {
                            empty.isVisible = batalList.isEmpty()
                            omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                batalList as MutableList<OrderDetailOmni>,
                                productList1 as MutableList<List<ProductDetailOmni>>, arrayResultList as MutableList<OrderDetailDetailOmni>,
                                sessionManager,
                                support,
                                prefHelper,
                                recyclerview_transaction,
                                activity,
                                logisticListBatal as MutableList<LogisticsDetailOmni>,
                                empty,
                                lifecycle
                            )
                            omniAdapter.notifyDataSetChanged()
                            recyclerview_transaction.adapter = omniAdapter
                        }
                        "Done" -> {
                            empty.isVisible = doneList.isEmpty()
                            omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                doneList as MutableList<OrderDetailOmni>,
                                productList2 as MutableList<List<ProductDetailOmni>>, arrayResultList as MutableList<OrderDetailDetailOmni>,
                                sessionManager,
                                support,
                                prefHelper,
                                recyclerview_transaction,
                                activity,
                                logisticListDone as MutableList<LogisticsDetailOmni>,
                                empty,
                                lifecycle
                            )
                            omniAdapter.notifyDataSetChanged()
                            recyclerview_transaction.adapter = omniAdapter
                        }
                    }

                    val countTrans = amountOfTransaction.value?.plus(1)
                    if (countTrans != null) {
                        setAmountOfTrans(countTrans)
                    }
                }, 1500)
            }
        })
    }

    fun getOrderDetailOmni(orderId: String) {
        PikappApiService().api.getListOrderDetailOmni(
            getUUID(), getTimestamp(), getClientID(), orderId
        ).enqueue(object : Callback<ListOrderDetailOmni> {
            override fun onFailure(call: Call<ListOrderDetailOmni>, t: Throwable) {
                Timber.tag(tag).d("Failed to get omni detail: ${t.message.toString()}")
            }

            override fun onResponse(
                call: Call<ListOrderDetailOmni>,
                response: Response<ListOrderDetailOmni>
            ) {
                val orderResponse = response.body()
                val resultList = orderResponse?.results
                logisticList.clear()
                logisticListBatal.clear()
                logisticListDone.clear()

                if (resultList != null) {
                    arrayResultList.add(resultList)
                    for (result in arrayResultList) {
                        if (result.logistics != null) {
                            if (result.status == "PAYMENT_CONFIRMATION"
                                || result.status == "PAYMENT_VERIFIED"
                                || result.status == "SELLER_ACCEPT_ORDER"
                                || result.status == "WAITING_FOR_PICKUP"
                                || result.status == "DRIVER_ALLOCATED"
                                || result.status == "DRIVER_ARRIVED"
                            ) {
                                logisticList.add(result.logistics)
                            } else if (result.status == "SELLER_CANCEL_ORDER"
                                || result.status == "ORDER_REJECTED_BY_SELLER"
                                || result.status == "CANCELLED"
                                || result.status == "FAILED"
                            ) {
                                logisticListBatal.add(result.logistics)
                            } else if (result.status == "ORDER_DELIVERED"
                                    || result.status == "ORDER_FINISHED"
                                    || result.status == "ORDER_SHIPMENT"
                                    || result.status == "DELIVERED_TO_PICKUP_POINT"
                                    || result.status == "DELIVERED"
                                    || result.status == "COLLECTED"
                            ){
                                logisticListDone.add(result.logistics)
                            } else {
                                Timber.tag(tag).d("Invalid transaction")
                            }
                        } else {
                            Timber.tag(tag).d("Invalid transaction")
                        }
                    }
                }
            }
        })
    }

    fun postUpdate(id: String, status: String) {
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
                        Timber.tag(tag).d("Berhasil")
                    }

                    override fun onError(e: Throwable) {
                        Timber.tag(tag).d("Gagal")
                    }
                })
        )
    }

    /* TRANSACTION LIST V2 */
    private var liveDataTransListV2Process: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataTransListV2Done: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataTransListV2Cancel: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()

    private var liveDataTransListV2ProcessFilter: MutableLiveData<List<TransactionListV2Data>> = MutableLiveData()
    private var liveDataFilterStatus: MutableLiveData<List<FilterMockUp>> = MutableLiveData()

    private val mutableProgressLoading = MutableLiveData(false)
    val progressLoading: LiveData<Boolean> = mutableProgressLoading
    fun setProgressLoading(isLoading: Boolean) {
        mutableProgressLoading.value = isLoading
    }

    private val mutableSizeFilter = MutableLiveData(0)
    val sizeFilter: LiveData<Int> = mutableSizeFilter
    fun setSizeOfFilter(size: Int) {
        mutableSizeFilter.value = size
    }

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

    fun getTransactionV2List(context: Context, fileName: String, action: Boolean) {
        setLoading(true)
        /* Get response from incoming api */
        val theJson = readJson(context, fileName)
        val gson = Gson()
        val listTransac = object : TypeToken<List<TransactionListV2Response>>() {}.type

        var theTransactionListV2: List<TransactionListV2Response> = gson.fromJson(theJson, listTransac)

        val processList: MutableList<TransactionListV2Data> = ArrayList()
        val doneList: MutableList<TransactionListV2Data> = ArrayList()
        val cancelList: MutableList<TransactionListV2Data> = ArrayList()

        theTransactionListV2.forEach {
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
        Handler().postDelayed({
            setProgressLoading(action)
            setLoading(false)

            liveDataTransListV2Process.postValue(processList)
            liveDataTransListV2Done.postValue(doneList)
            liveDataTransListV2Cancel.postValue(cancelList)
        }, 3000)
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

    fun transactionTxnUpdateDummy(id: String, status: String, context: Context) {
        getTransactionV2List(context, "sample_response_txn_updated.json", true)
    }

    fun transactionChannelUpdateDummy(channel: String, orderId: String, context: Context) {
        getTransactionV2List(context, "sample_response_txn_updated.json", true)
    }

    fun transactionPosUpdateDummy(reqBody: UpdateStatusManualTxnRequest, context: Context) {
        getTransactionV2List(context, "sample_response_txn_updated.json", true)
    }

    fun postUpdateTxn(id: String, status: String) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

//        disposable.add(
//            pikappService.postUpdateOrderStatus(
//                email,
//                token,
//                RequestBody.create(MediaType.parse("multipart/form-data"), id),
//                RequestBody.create(MediaType.parse("multipart/form-data"), status)
//            )
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>() {
//                    override fun onSuccess(t: UpdateStatusResponse) {
//                        Timber.tag(tag).d("Berhasil")
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Timber.tag(tag).d("Gagal")
//                    }
//                })
//        )
    }

    private fun postUpdateChannel(channel: String, orderId: String, context: Context) {
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
            }
        })
    }

    fun postUpdatePos(status: UpdateStatusManualTxnRequest){
        PikappApiService().api.postUpdateManualTransaction(status).enqueue(object : Callback<UpdateStatusManualResponse>{
            override fun onResponse(
                call: Call<UpdateStatusManualResponse>,
                response: Response<UpdateStatusManualResponse>
            ) {
                if (response.code() == 200){
                    /* GET POS API */
                }
            }

            override fun onFailure(call: Call<UpdateStatusManualResponse>, t: Throwable) {
                Log.e("Fail", t.message.toString())
            }
        })
    }

    fun restartFragment() {
        mutablePikappFilter.value = false
        mutableTokpedFilter.value = false
        mutableGrabFilter.value = false
        mutableShopeeFilter.value = false
        mutableWhatsappFilter.value = false
        mutableTelpFilter.value = false

        liveDataTransListV2Process.value = arrayListOf()
        liveDataTransListV2ProcessFilter.value = arrayListOf()
        liveDataTransListV2Done.value = arrayListOf()
        liveDataTransListV2Cancel.value = arrayListOf()
    }
}
