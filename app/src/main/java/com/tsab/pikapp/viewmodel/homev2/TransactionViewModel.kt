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
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.Transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.Transaction.TransactionListAdapter
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

class TransactionViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName

    var activation: Boolean = true

    lateinit var categoryAdapter: TransactionListAdapter
    lateinit var omniAdapter: OmniTransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    private val mutableSize = MutableLiveData(0)
    val size: LiveData<Int> get() = mutableSize

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

    private val mutableIsLoading = MutableLiveData<Boolean>(true)
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
        listener: TransactionListAdapter.OnItemClickListener
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
            }

            override fun onResponse(
                call: Call<GetStoreOrderListV2Response>,
                response: Response<GetStoreOrderListV2Response>
            ) {
                val gson = Gson()
                val type = object : TypeToken<GetStoreOrderListV2Response>() {}.type
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val totalItemsTrans = response.body()?.total_items
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
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        val timestamp = getTimestamp()

        val transReq = TransactionListRequest(
            page = 0,
            size = totalItems,
            transaction_id = "",
            status = listOf()
        )
        setProcessBadges(0)

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
        lifecycle: Fragment
    ) {
        prefHelper.clearStoreOrderList()

        val mid = sessionManager.getUserData()!!.mid!!
        val page = "0"
        val size = "50"

        PikappApiService().api.getListOrderOmni(
            getUUID(), getTimestamp(), getClientID(), mid, page, size
        ).enqueue(object : Callback<ListOrderOmni> {
            override fun onFailure(call: Call<ListOrderOmni>, t: Throwable) {
                Toast.makeText(baseContext, "Error: $t", Toast.LENGTH_SHORT).show()
                Timber.tag(tag).d("Failed to get omni list: ${t.message.toString()}")
            }

            override fun onResponse(call: Call<ListOrderOmni>, response: Response<ListOrderOmni>) {
                val responseBody = response.body()

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
}
