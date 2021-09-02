package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.Transaction.TransactionListAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

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

    private val mutableCategoryList = MutableLiveData<List<CategoryListResult>>(listOf())
    val categoryListResult: LiveData<List<CategoryListResult>> = mutableCategoryList
    fun setCategoryList(categoryListResult: List<CategoryListResult>) {
        mutableCategoryList.value = categoryListResult
    }

    private val mutableisLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = mutableisLoading

    private val mutableCategoryName = MutableLiveData(" ")
    val categoryName: LiveData<String> get() = mutableCategoryName

    /*fun getMenuCategoryList(baseContext: Context, recyclerview_transaction: RecyclerView, listener: TransactionListAdapter.OnItemClickListener) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        // TODO: Update API call.
        PikappApiService().api.getMenuCategoryList(
                getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
            }

            override fun onResponse(
                    call: Call<MerchantListCategoryResponse>,
                    response: Response<MerchantListCategoryResponse>
            ) {

                val categoryResponse = response.body()
                val categoryResult = response.body()?.results

                mutableSize.value = categoryResponse?.results?.size
                mutableisLoading.value = false

                if (categoryResult != null) {
                    categoryAdapter = TransactionListAdapter(
                            baseContext,
                            categoryResult as MutableList<StoreOrderList>, categoryResult as MutableList<CategoryListResult>, listener)
                    categoryAdapter.notifyDataSetChanged()
                    recyclerview_transaction.adapter = categoryAdapter
                    setCategoryList(categoryResult)
                    mutableisLoading.value = false
                }
            }

        })
    }*/

    fun getStoreOrderList(baseContext: Context, recyclerview_transaction: RecyclerView, listener: TransactionListAdapter.OnItemClickListener) {
        prefHelper.clearStoreOrderList()
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!
        //loading.value = true

        disposable.add(
                pikappService.getTransactionListMerchant(email, token, mid)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<GetStoreOrderListResponse>() {
                            override fun onSuccess(t: GetStoreOrderListResponse) {
                                val transactionList = t.results
                                if (transactionList != null) {
                                    Log.e("jumlah", transactionList.size.toString())
                                    mutableSize.value = transactionList.size
                                }
                                Log.e("list transaction", t.results.toString())
                                Log.e("error msg", t.errCode.toString())
                                Log.e("error code", t.errMessage.toString())
                                categoryAdapter = TransactionListAdapter(
                                        baseContext,
                                        transactionList as MutableList<StoreOrderList>, transactionList as MutableList<CategoryListResult>, listener)
                                categoryAdapter.notifyDataSetChanged()
                                recyclerview_transaction.adapter = categoryAdapter
                                setCategoryList(transactionList)
                                categoryAdapter.notifyDataSetChanged()
                                mutableisLoading.value = false
                            }

                            override fun onError(e: Throwable) {
                                var errorResponse: ErrorResponse
                                Log.e("failed", e.message.toString())
                                /*try {
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
                                Log.d(
                                        "Debug",
                                        "error merchant detail : ${errorResponse.errCode} ${errorResponse.errMessage}"
                                )*/
                            }
                        })
        )
    }

}
