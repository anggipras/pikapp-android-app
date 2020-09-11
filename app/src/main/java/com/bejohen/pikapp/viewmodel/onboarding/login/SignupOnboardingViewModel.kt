package com.bejohen.pikapp.viewmodel.onboarding.login

import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.bejohen.pikapp.models.model.ErrorResponse
import com.bejohen.pikapp.models.model.RegisterResponse
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.util.detectSpecialCharacter
import com.bejohen.pikapp.util.isEmailValid
import com.bejohen.pikapp.view.login.SignupFragmentDirections
import com.bejohen.pikapp.view.onboarding.login.SignupOnboardingFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class SignupOnboardingViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    var email = ""

    val registerResponse = MutableLiveData<RegisterResponse>()
    val registerErrorResponse = MutableLiveData<ErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    val emailValid = MutableLiveData<Boolean>()

    val fullNameError = MutableLiveData<String>()
    val phoneError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val passwordConfError = MutableLiveData<String>()

    var isEmailValid = false
    var isFullNameValid = false
    var isPhoneValid = false
    var isPasswordValid = false
    var isConfPasswordValid = false

    fun getOnboardingFinished(): Boolean {
        return prefHelper.isOnboardingFinished() ?: false
    }

    fun emailValidation(mail: String) {
        email = mail
        isEmailValid = mail.isEmailValid()
        emailValid.value = mail.isEmailValid()
    }

    fun register(fullName: String, phone: String, password: String, confPassword: String) {
        checkUserInput(fullName, phone, password, confPassword)
        if (isFullNameValid && isPhoneValid && isPasswordValid && isConfPasswordValid) {
            registerUser(fullName, phone, password)
        }
    }

    private fun checkUserInput(
        fullName: String,
        phone: String,
        password: String,
        confPassword: String
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
        } else if (phone.length < 10) {
            phoneError.value = "Masukan nomor telepon anda dengan benar"
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
        } else if (password.length < 6) {
            passwordError.value = "Minimal password 6 digit (kombinasi angka dan huruf)"
            isPasswordValid = false
        } else {
            passwordError.value = ""
            isPasswordValid = true
        }

        if (confPassword.isEmpty()) {
            passwordConfError.value = "Silakan masukkan password anda"
            isConfPasswordValid = false
        } else if (confPassword != password) {
            passwordConfError.value = "Password tidak sama"
            isConfPasswordValid = false
        } else {
            passwordConfError.value = ""
            isConfPasswordValid = true
        }
    }

    private fun registerUser(fullName: String, phone: String, password: String) {
        loading.value = true
        disposable.add(
            apiService.registerUser(email, password, fullName, phone, "01011970", "MALE")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RegisterResponse>() {
                    override fun onSuccess(t: RegisterResponse) {
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
        loading.value = false
    }

    private fun registerFail(response: ErrorResponse) {
        registerErrorResponse.value = response
        loading.value = false
    }

    fun goToSuccess(view: View) {
        if (getOnboardingFinished()) {
            val action = SignupFragmentDirections.actionToSignupSuccessFragment()
            Navigation.findNavController(view).navigate(action)
        } else {
            val action =
                SignupOnboardingFragmentDirections.actionToSignupOnboardingSuccessFragment()
            Navigation.findNavController(view).navigate(action)
        }

    }

    fun createToast(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}