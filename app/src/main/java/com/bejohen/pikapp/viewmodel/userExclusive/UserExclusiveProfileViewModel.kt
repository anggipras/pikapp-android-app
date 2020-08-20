package com.bejohen.pikapp.viewmodel.userExclusive

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.ErrorResponse
import com.bejohen.pikapp.models.model.LogoutResponse
import com.bejohen.pikapp.models.model.UserAccess
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.view.UserExclusiveActivity
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class UserExclusiveProfileViewModel(application: Application) : BaseViewModel(application) {

    val logoutResponse = MutableLiveData<LogoutResponse>()
    val errorResponse = MutableLiveData<ErrorResponse>()
    val userData = MutableLiveData<UserAccess>()
    val loading = MutableLiveData<Boolean>()
    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    fun getUserData() {
        userData.value = sessionManager.getUserData()
    }

    fun logout(context: Context) {
        val sessionId = sessionManager.getUserData()?.sessionId!!
        Log.d("debug","sessionid : $sessionId")
//        clearSession(context)
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

    private fun logoutSuccess(response: LogoutResponse) {
        logoutResponse.value = response
        loading.value = false
    }

    private fun logoutFail(response: ErrorResponse) {
        errorResponse.value = response
        loading.value = false
    }

    fun clearSession(context: Context) {
        sessionManager.logout()
        prefHelper.saveUserExclusive(false)
        prefHelper.saveUserExclusiveForm(false)
        prefHelper.saveOnboardingFinised(false)
        goToOnboarding(context)
    }

    private fun goToOnboarding(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context?.startActivity(onboardingActivity)
        (context as UserExclusiveActivity).finish()
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }
}
