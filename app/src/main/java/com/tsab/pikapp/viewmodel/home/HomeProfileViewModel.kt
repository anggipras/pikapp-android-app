package com.tsab.pikapp.viewmodel.home

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.GetOrderListResponse
import com.tsab.pikapp.models.model.LogoutResponse
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.OrderListActivity
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class HomeProfileViewModel(application: Application) : BaseViewModel(application) {
    val logoutResponse = MutableLiveData<LogoutResponse>()
    val errorResponse = MutableLiveData<ErrorResponse>()

    val userData = MutableLiveData<UserAccess>()
    val loading = MutableLiveData<Boolean>()
    val userSuccess = MutableLiveData<Boolean>()
    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    fun goToOrderList(context: Context, status: Int) {
        prefHelper.setOrderListTabSelected(status)
        val orderListActivity = Intent(context, OrderListActivity::class.java)
        (context as HomeActivity).startActivity(orderListActivity)
    }

    fun goToStoreHome(context: Context) {
        val user = sessionManager.getUserData()
        user?.let {
            if (it.isMerchant!!) {
                val storeActivity = Intent(context, StoreActivity::class.java)
                (context as HomeActivity).startActivity(storeActivity)
            } else {
                createToastShort(
                    getApplication(),
                    "Anda belum terdaftar sebagai merchant. Silakan hubungi CS kami"
                )
            }
        }
    }

    fun getUserData() {
        userData.value = sessionManager.getUserData()
        loading.value = false
    }

    fun validateUser() {
        loading.value = true
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
            apiService.getTransactionList(email, token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetOrderListResponse>() {
                    override fun onSuccess(t: GetOrderListResponse) {
                        validateUserSuccess()
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug", "error merchant detail : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse = Gson().fromJson(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse("503", "Unavailable")
                        }

                        validateUserFail(errorResponse)
                    }
                })
        )
    }

    private fun validateUserSuccess() {
        userSuccess.value = true
    }

    private fun validateUserFail(err: ErrorResponse) {
        errorResponse.value = err
        loading.value = false
    }

    fun logout(context: Context) {
        val sessionId = sessionManager.getUserData()?.sessionId!!
        Log.d("debug", "sessionid : $sessionId")
        logoutProcess(sessionId)
    }

    private fun logoutProcess(sessionid: String) {
        loading.value = true
        disposable.add(
            apiService.logoutUser(sessionid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LogoutResponse>() {
                    override fun onSuccess(t: LogoutResponse) {
                        logoutSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("Debug", "error : " + e)
                        var errorResponse: ErrorResponse
                        try {
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse = Gson().fromJson(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse = ErrorResponse("503", "Service Unavailable")
                        }
                        logoutFail(errorResponse)
                    }
                })
        )
    }

    private fun logoutSuccess(response: LogoutResponse) {
        logoutResponse.value = response
        loading.value = false
    }

    private fun logoutFail(response: ErrorResponse) {
        errorResponse.value = response
        loading.value = false
    }
}