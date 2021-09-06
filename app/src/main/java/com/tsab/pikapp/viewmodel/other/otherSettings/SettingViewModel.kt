package com.tsab.pikapp.viewmodel.other.otherSettings

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.LogoutResponseV2
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SettingViewModel(application: Application) : BaseViewModel(application) {
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()
    val loading = MutableLiveData<Boolean>()
    val logoutResponse = MutableLiveData<LogoutResponseV2>()
    val errorResponse = MutableLiveData<ErrorResponse>()
    val activityToStart = MutableLiveData<Int>()

    private var sessionManager = SessionManager(getApplication())

    fun logout() {
        val sessionId = sessionManager.getUserData()?.sessionId!!
        sessionManager.setHomeNav(0)
        Log.d("debug", "sessionid : $sessionId")
        logoutProcess(sessionId)
    }

    private fun logoutProcess(sessionid: String) {
        loading.value = true
        disposable.add(
            pikappService.logoutMerchant(sessionid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LogoutResponseV2>() {
                    override fun onSuccess(t: LogoutResponseV2) {
                        logoutSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        Log.d("Debug", "error : " + e)
                        var errorResponse: ErrorResponse
                        try {
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse =
                                ErrorResponse(
                                    "503",
                                    "Service Unavailable"
                                )
                        }
                        logoutFail(errorResponse)
                    }

                })
        )
    }

    private fun logoutSuccess(response: LogoutResponseV2) {
        logoutResponse.value = response
        loading.value = false
    }

    private fun logoutFail(response: ErrorResponse) {
        errorResponse.value = response
        loading.value = false
    }

    fun clearSessionExclusive() {
        sessionManager.logout()
        activityToStart.value = 1
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_LONG).show()
    }

    fun setUserNotif() {
        val userNotif = sessionManager.getProfileNum()
        when(userNotif) {
            0 -> createToast("Mohon isi tanggal lahir dan jenis kelamin")
            1 -> createToast("Mohon upload ulang banner dan logo restoran")
            else -> Log.d("DONEUPDATE", "DONE ALL FIRST UPDATE")
        }
    }

    fun getUserNotif(): Int{
        val userNotif = sessionManager.getProfileNum()
        return if (userNotif != null) {
            userNotif.toInt()
        }else{
            10
        }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}