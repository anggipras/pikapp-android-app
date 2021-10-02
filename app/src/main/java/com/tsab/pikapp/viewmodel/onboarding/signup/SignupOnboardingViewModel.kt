package com.tsab.pikapp.viewmodel.onboarding.signup

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.tsab.pikapp.models.model.ErrorResponse
import com.tsab.pikapp.models.model.LoginResponse
import com.tsab.pikapp.models.model.RegisterResponse
import com.tsab.pikapp.models.model.UserAccess
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.view.onboarding.login.SignupOnboardingFragmentDirections
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SignupOnboardingViewModel(application: Application) : BaseViewModel(application) {

    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    var email = ""
    var password = ""

    val registerResponse = MutableLiveData<RegisterResponse>()
    val registerErrorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()
    val loginResponse = MutableLiveData<LoginResponse>()
    val loginErrorResponse = MutableLiveData<ErrorResponse>()
    val emailValid = MutableLiveData<Boolean>()

    val fullNameError = MutableLiveData<String>()
    val phoneError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()

    var isEmailValid = false
    var isFullNameValid = false
    var isPhoneValid = false
    var isPasswordValid = false

    fun getOnboardingFinished(): Boolean {
        return prefHelper.isOnboardingFinished() ?: false
    }

    fun emailValidation(mail: String) {
        email = mail
        isEmailValid = mail.isEmailValid()
        emailValid.value = mail.isEmailValid()
    }

    fun register(fullName: String, phone: String, password: String) {
        checkUserInput(fullName, phone, password)
        if (isFullNameValid && isPhoneValid && isPasswordValid) {
            registerUser(fullName, phone, password)
        }
    }

    private fun checkUserInput(
        fullName: String,
        phone: String,
        password: String
    ) {
        if (fullName.isEmpty()) {
            fullNameError.value = "Silakan masukkan nama anda"
            isFullNameValid = false
        } else if (detectSpecialCharacter(fullName)) {
            fullNameError.value = "Karakter spesial tidak diperbolehkan"
            isFullNameValid = false
        } else if (fullName.length < 5) {
            fullNameError.value = "Masukan nama anda 5 karakter atau lebih"
            isFullNameValid = false
        } else {
            fullNameError.value = ""
            isFullNameValid = true
        }

        if (phone.isEmpty()) {
            phoneError.value = "Silakan masukkan nomor telepon anda"
            isPhoneValid = false
        } else if (detectSpecialCharacter(phone)) {
            phoneError.value = "Karakter spesial tidak diperbolehkan"
            isPhoneValid = false
        } else if (phone.length < 10 || phone.length > 15) {
            phoneError.value = "Masukan nomor telepon anda dengan benar"
            isPhoneValid = false
        } else if (!isPhoneValid(phone)) {
            phoneError.value = "Gunakan format 08xxxxx"
            isPhoneValid = false
        } else {
            phoneError.value = ""
            isPhoneValid = true
        }

        if (password.isEmpty()) {
            passwordError.value = "Silakan masukkan password anda"
            isPasswordValid = false
        } else if (detectSpecialCharacter(password)) {
            passwordError.value = "Karakter spesial tidak diperbolehkan"
            isPasswordValid = false
        } else if (password.length < 8) {
            passwordError.value = "Minimal password 8 digit (kombinasi angka dan huruf)"
            isPasswordValid = false
        } else {
            passwordError.value = ""
            isPasswordValid = true
        }

//        if (confPassword.isEmpty()) {
//            passwordConfError.value = "Silakan masukkan password anda"
//            isConfPasswordValid = false
//        } else if (confPassword != password) {
//            passwordConfError.value = "Password tidak sama"
//            isConfPasswordValid = false
//        } else {
//            passwordConfError.value = ""
//            isConfPasswordValid = true
//        }
    }

    private fun registerUser(fullName: String, phone: String, pword: String) {
        loading.value = true
        disposable.add(
            apiService.registerUser(email, pword, fullName, phone, "01011970", "MALE")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RegisterResponse>() {
                    override fun onSuccess(t: RegisterResponse) {
                        password = pword
                        registerSuccess(t)
                    }

                    override fun onError(e: Throwable) {
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

                        registerFail(errorResponse)
                    }
                })
        )
    }

    private fun registerSuccess(response: RegisterResponse) {
        registerResponse.value = response
    }

    fun loginProcess() {
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
//                        createToastShort(errorResponse.errMessage!!)
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

    private fun registerFail(response: ErrorResponse) {
        registerErrorResponse.value = response
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

    fun goToLogin(view: View) {
        val action =
            SignupOnboardingFragmentDirections.actionFromSignupOnboardingFragmentToLoginOnboardingFragment()
        Navigation.findNavController(view).navigate(action)
    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}