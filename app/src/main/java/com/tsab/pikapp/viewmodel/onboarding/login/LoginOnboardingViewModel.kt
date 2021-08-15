package com.tsab.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.LoginResponse
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.decodeJWT
import com.tsab.pikapp.util.isEmailValid
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class LoginOnboardingViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var sessionManager = SessionManager(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    val loginResponse = MutableLiveData<LoginResponse>()
    val loginErrorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    val emailError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()

    private var isEmailValid = false
    private var isPasswordValid = false

    fun login(email: String, password: String) {
        checkUserInput(email, password)
        if (isEmailValid && isPasswordValid) {
            loginProcess(email, password)
        }
    }

    private fun checkUserInput(email: String, password: String) {
        if (email.isEmpty()) {
            emailError.value = "Silakan masukkan email anda"
            isEmailValid = false
        } else if (!email.isEmailValid()) {
            emailError.value = "Silakan masukkan email anda secara valid"
            isEmailValid = false
        } else {
            emailError.value = ""
            isEmailValid = true
        }
        if (password.isEmpty()) {
            passwordError.value = "Silakan masukkan password anda"
            isPasswordValid = false
        } else if (password.length < 6) {
            passwordError.value = "Minimal password 6 digit (kombinasi angka dan huruf)"
            isPasswordValid = false
        } else {
            passwordError.value = ""
            isPasswordValid = true
        }
    }

    private fun loginProcess(email: String, password: String) {
        loading.value = true
        disposable.add(
            apiService.loginUser(email, password, prefHelper.getFcmToken().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<LoginResponse>() {
                    override fun onSuccess(t: LoginResponse) {
                        loginSuccess(t)
//                        Toast.makeText(getApplication(), "token: ${t.token}", Toast.LENGTH_SHORT).show()
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
                        loginFail(errorResponse)
                        createToastShort(getApplication(), "error: ${errorResponse.errMessage}")
//                        Toast.makeText(getApplication(), "error: ${errorResponse.errMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }

    private fun loginSuccess(response: LoginResponse) {
        loginResponse.value = response
        loading.value = false

        response.newEvent?.let { prefHelper.saveUserExclusive(response.newEvent) }
        response.recommendationStatus?.let { prefHelper.saveUserExclusiveForm(response.recommendationStatus) }
        response.token?.let {
            val userData: UserAccess = decodeJWT(response.token)
            sessionManager.setUserSession(response.token, System.nanoTime(), userData)
        }

        prefHelper.saveOnboardingFinised(true)
    }

    private fun loginFail(response: ErrorResponse) {
        loginErrorResponse.value = response
        loading.value = false
    }

    fun goToHome(context: Context) {
        val homeActivity = Intent(context, HomeActivity::class.java)
        context.startActivity(homeActivity)
        (context as OnboardingActivity).finish()
    }

    fun goToUserExclusive(context: Context) {
        val userExclusiveActivity = Intent(context, UserExclusiveActivity::class.java)
        context.startActivity(userExclusiveActivity)
        (context as OnboardingActivity).finish()
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
