package com.tsab.pikapp.viewmodel.store

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class StoreProductListViewModel(application: Application) : BaseViewModel(application) {

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    private var productListArray: ArrayList<StoreProductList> = arrayListOf()

    private var sessionManager = SessionManager(getApplication())
    val storeProductListResponse = MutableLiveData<List<StoreProductList>>()
    val loadingProduct = MutableLiveData<Boolean>()
    val productLoadError = MutableLiveData<Boolean>()
    val productErrorResponse = MutableLiveData<ErrorResponse>()

    val loadingDelete = MutableLiveData<Boolean>()
    val deleteProductErrorResponse = MutableLiveData<ErrorResponse>()

    fun getProductListAvailable(status : Boolean) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        disposable.add(
            pikappService.getStoreProductList(email, token, mid, status)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoreProductListResponse>() {
                    override fun onSuccess(t: StoreProductListResponse) {
                        t.results?.let { it1 -> productListRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug", "error store product list : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<ErrorResponse>(
                                    body,
                                    ErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse(
                                "now", "Unavailable"
                            )
                        }

                        productListFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.errMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Debug",
                            "error store product list : ${errorResponse.errMessage}"
                        )
                    }
                })
        )
    }

    private fun productListRetrieved(productList: List<StoreProductList>) {
        productListArray.clear()
        productListArray.addAll(productList)
        storeProductListResponse.value = productList
        productLoadError.value = false
        loadingProduct.value = false
    }

    private fun productListFail(response: ErrorResponse) {
        productErrorResponse.value = response
        loadingProduct.value = false
    }

    fun setOnOffProduct(pid: String, position: Int, status: Boolean) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        disposable.add(
            pikappService.postStoreOnOffProduct(email, token, mid, pid, status)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoreProductActionResponse>() {
                    override fun onSuccess(t: StoreProductActionResponse) {
                        onOffProductSuccess(position)
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug", "error store product list : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<ErrorResponse>(
                                    body,
                                    ErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse(
                                "now", "Unavailable"
                            )
                        }
                        deleteProductFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.errMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Debug",
                            "error store product list : ${errorResponse.errMessage}"
                        )
                    }
                }
                ))

    }

    fun deleteProduct(pid: String, position: Int) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        disposable.add(
            pikappService.postStoreDeleteProduct(email, token, mid, pid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StoreProductActionResponse>() {
                    override fun onSuccess(t: StoreProductActionResponse) {
                        deleteProductSuccess(position)
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug", "error store product list : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<ErrorResponse>(
                                    body,
                                    ErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse(
                                "now", "Unavailable"
                            )
                        }
                        deleteProductFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.errMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "Debug",
                            "error store product list : ${errorResponse.errMessage}"
                        )
                    }
                }
                ))

    }

    private fun onOffProductSuccess(position: Int) {
        productListArray.removeAt(position)
        storeProductListResponse.value = productListArray
    }

    private fun deleteProductSuccess(position: Int) {
        productListArray.removeAt(position)
        storeProductListResponse.value = productListArray
    }

    private fun deleteProductFail(err: ErrorResponse) {
        deleteProductErrorResponse.value = err
        loadingDelete.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}