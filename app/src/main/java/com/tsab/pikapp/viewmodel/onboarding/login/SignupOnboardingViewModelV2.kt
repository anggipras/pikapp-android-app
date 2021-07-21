package com.tsab.pikapp.viewmodel.onboarding.login

import android.app.Application
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.util.isEmailValid
import com.tsab.pikapp.util.isPinValid
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class SignupOnboardingViewModelV2(application: Application) : BaseViewModel(application) {
    private var sessionManager = SessionManager(getApplication())
    private var prefHelper = SharedPreferencesUtil(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    /* Variables for the first sign up screen */
    private val mutableEmail = MutableLiveData("")
    private val mutableEmailError = MutableLiveData("")
    private val isEmailValid = MutableLiveData(false)
    val email: LiveData<String> get() = mutableEmail
    val emailError: LiveData<String> get() = mutableEmailError

    private val mutableFullName = MutableLiveData("")
    private val mutableFullNameError = MutableLiveData("")
    private val isFullNameValid = MutableLiveData(false)
    val fullName: LiveData<String> get() = mutableFullName
    val fullNameError: LiveData<String> get() = mutableFullNameError

    private val mutablePhone = MutableLiveData("")
    private val mutablePhoneError = MutableLiveData("")
    private val isPhoneValid = MutableLiveData(false)
    val phone: LiveData<String> get() = mutablePhone
    val phoneError: LiveData<String> get() = mutablePhoneError

    private val mutablePin = MutableLiveData("")
    private val mutablePinError = MutableLiveData("")
    private val isPinValid = MutableLiveData(false)
    val pin: LiveData<String> get() = mutablePin
    val pinError: LiveData<String> get() = mutablePinError

    fun validateEmail(email: String): Boolean {
        if (email.isEmpty() || email.isBlank()) {
            mutableEmailError.value = "Email tidak boleh kosong"
        } else if (!email.isEmailValid()) {
            mutableEmailError.value = "Email harus valid"
        } else {
            mutableEmailError.value = ""
        }

        mutableEmail.value = email
        isEmailValid.value = mutableEmailError.value!!.isEmpty()
        return isEmailValid.value!!
    }

    fun validateFullName(fullName: String): Boolean {
        if (fullName.isEmpty() || fullName.isBlank()) {
            mutableFullNameError.value = "Nama lengkap tidak boleh kosong"
        } else if (fullName.trim().length <= 3) {
            mutableFullNameError.value = "Nama lengkap harus lebih dari 3 karakter"
        } else {
            mutableFullNameError.value = ""
        }

        mutableFullName.value = fullName
        isFullNameValid.value = mutableFullNameError.value!!.isEmpty()
        return isFullNameValid.value!!
    }

    fun validatePhone(phone: String): Boolean {
        if (phone.isEmpty() || phone.isBlank()) {
            mutablePhoneError.value = "Nomor HP tidak boleh kosong"
        } else if (!phone.isDigitsOnly()) {
            mutablePhoneError.value = "Nomor HP hanya dapat berisi angka"
        } else if (phone.trim().length <= 8) {
            mutablePhoneError.value = "Nomor telepon harus lebih dari 8 digit angka"
        } else {
            mutablePhoneError.value = ""
        }

        mutablePhone.value = phone
        isPhoneValid.value = mutablePhoneError.value!!.isEmpty()
        return isPhoneValid.value!!
    }

    fun validatePin(pin: String): Boolean {
        if (pin.isEmpty() || pin.isBlank()) {
            mutablePinError.value = "PIN tidak boleh kosong"
        } else if (!pin.isDigitsOnly()) {
            mutablePinError.value = "PIN hanya dapat berisi angka"
        } else if (pin.trim().length != 6) {
            mutablePinError.value = "PIN harus terdiri dari 6 digit angka"
        } else if (!pin.isPinValid()) {
            mutablePinError.value = "PIN harus memiliki digit yang berulang"
        } else {
            mutablePinError.value = ""
        }

        mutablePin.value = pin
        isPinValid.value = mutablePinError.value!!.isEmpty()
        return isPinValid.value!!
    }

    fun validateFirstPage(): Boolean = isEmailValid.value!! && isFullNameValid.value!! && isPhoneValid.value!! && isPinValid.value!!
}