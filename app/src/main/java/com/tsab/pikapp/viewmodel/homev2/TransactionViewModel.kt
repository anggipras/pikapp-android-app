package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.Transaction.TransactionListAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody

class TransactionViewModel(application: Application) : BaseViewModel(application) {

    var activation: Boolean = true

    lateinit var categoryAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()

    private val mutableSize = MutableLiveData(0)
    val size: LiveData<Int> get() = mutableSize

    private val mutableProses = MutableLiveData(0)
    val proses: LiveData<Int> get() = mutableProses

    private val mutableBatal = MutableLiveData(0)
    val batal: LiveData<Int> get() = mutableBatal

    private val mutableDone = MutableLiveData(0)
    val done: LiveData<Int> get() = mutableDone

    private val mutableCategoryList = MutableLiveData<List<CategoryListResult>>(listOf())

    private val mutableIsLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val mutableCategoryName = MutableLiveData(" ")
    val categoryName: LiveData<String> get() = mutableCategoryName



    fun getStoreOrderList(baseContext: Context, recyclerview_transaction: RecyclerView, status: String, support: FragmentManager, empty: ConstraintLayout) {
        setLoading(true)
        prefHelper.clearStoreOrderList()
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        disposable.add(
                pikappService.getTransactionListMerchant(email, token, mid)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<GetStoreOrderListResponse>() {
                            override fun onSuccess(t: GetStoreOrderListResponse) {
                                val transactionList = t.results
                                var prosesList = ArrayList<StoreOrderList>()
                                var batalList = ArrayList<StoreOrderList>()
                                var doneList = ArrayList<StoreOrderList>()
                                var menuList = ArrayList<ArrayList<OrderDetailDetail>>()
                                var menuList1 = ArrayList<ArrayList<OrderDetailDetail>>()
                                var menuList2 = ArrayList<ArrayList<OrderDetailDetail>>()
                                if (transactionList != null) {
                                    for(transaction in transactionList){
                                        if(transaction.status == "OPEN" || transaction.status == "PAID" || transaction.status == "ON_PROCESS"){
                                            prosesList.add(transaction)
                                            transaction.detailProduct?.let { menuList.add(it as ArrayList<OrderDetailDetail>) }
                                        }else if(transaction.status == "FAILED" || transaction.status == "ERROR"){
                                            batalList.add(transaction)
                                            transaction.detailProduct?.let { menuList1.add(it as ArrayList<OrderDetailDetail>) }
                                        }else{
                                            doneList.add(transaction)
                                            transaction.detailProduct?.let { menuList2.add(it as ArrayList<OrderDetailDetail>) }
                                        }
                                    }
                                }
                                mutableProses.value = prosesList.size
                                mutableBatal.value = batalList.size
                                mutableDone.value = doneList.size
                                if(status == "Proses"){
                                    empty.isVisible = prosesList.isEmpty()
                                    categoryAdapter = TransactionListAdapter(
                                            baseContext,
                                            prosesList as MutableList<StoreOrderList>, menuList as MutableList<List<OrderDetailDetail>>, sessionManager, support, prefHelper, recyclerview_transaction)
                                    categoryAdapter.notifyDataSetChanged()
                                    recyclerview_transaction.adapter = categoryAdapter
                                    categoryAdapter.notifyDataSetChanged()
                                    //setLoading(false)
                                }
                                if(status == "Batal"){
                                    empty.isVisible = batalList.isEmpty()
                                    categoryAdapter = TransactionListAdapter(
                                            baseContext,
                                            batalList as MutableList<StoreOrderList>, menuList1 as MutableList<List<OrderDetailDetail>>, sessionManager, support, prefHelper, recyclerview_transaction)
                                    categoryAdapter.notifyDataSetChanged()
                                    recyclerview_transaction.adapter = categoryAdapter
                                    categoryAdapter.notifyDataSetChanged()
                                    //setLoading(false)
                                }
                                if(status == "Done"){
                                    empty.isVisible = doneList.isEmpty()
                                    categoryAdapter = TransactionListAdapter(
                                            baseContext,
                                            doneList as MutableList<StoreOrderList>, menuList2 as MutableList<List<OrderDetailDetail>>, sessionManager, support, prefHelper, recyclerview_transaction)
                                    categoryAdapter.notifyDataSetChanged()
                                    recyclerview_transaction.adapter = categoryAdapter
                                    categoryAdapter.notifyDataSetChanged()
                                    //setLoading(false)
                                }

                                setLoading(false)

                            }

                            override fun onError(e: Throwable) {
                                var errorResponse: ErrorResponse
                                Log.e("failed", e.message.toString())
                                setLoading(false)
                            }
                        })
        )
    }

    fun postUpdate(id: String, status: String){
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
                pikappService.postUpdateOrderStatus(email, token,
                        RequestBody.create(MediaType.parse("multipart/form-data"), id)
                        , RequestBody.create(MediaType.parse("multipart/form-data"), status))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>(){
                            override fun onSuccess(t: UpdateStatusResponse) {
                                Log.e("Berhasil", "Sukses")
                            }

                            override fun onError(e: Throwable) {
                                Log.e("Gagal", "Fail")
                            }
                        })
        )
    }

}
