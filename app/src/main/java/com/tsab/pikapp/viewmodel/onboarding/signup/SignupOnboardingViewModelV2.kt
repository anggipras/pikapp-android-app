package com.tsab.pikapp.viewmodel.onboarding.signup

import android.app.Application
import android.util.Log
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

    /* Variables for the second sign up screen */
    private val mutableNamaRestoran = MutableLiveData("")
    private val mutableNamaRestoranError = MutableLiveData("")
    private val isNamaRestoranValid = MutableLiveData(false)
    val namaRestoran: LiveData<String> get() = mutableNamaRestoran
    val namaRestoranError: LiveData<String> get() = mutableNamaRestoranError

    private val mutableNamaFoodcourt = MutableLiveData("")
    private val mutableNamaFoodcourtError = MutableLiveData("")
    private val isNamaFoodcourtValid = MutableLiveData(true)
    val namaFoodcourt: LiveData<String> get() = mutableNamaFoodcourt
    val namaFoodcourtError: LiveData<String> get() = mutableNamaFoodcourtError

    private val mutableAlamat = MutableLiveData("")
    private val mutableAlamatError = MutableLiveData("")
    private val isAlamatValid = MutableLiveData(false)
    val alamat: LiveData<String> get() = mutableAlamat
    val alamatError: LiveData<String> get() = mutableAlamatError

    private val mutableNamaBank = MutableLiveData("")
    private val mutableNamaBankError = MutableLiveData("")
    private val isNamaBankValid = MutableLiveData(false)
    val namaBank: LiveData<String> get() = mutableNamaBank
    val namaBankError: LiveData<String> get() = mutableNamaBankError

    private val mutableNomorRekening = MutableLiveData("")
    private val mutableNomorRekeningError = MutableLiveData("")
    private val isNomorRekeningValid = MutableLiveData(false)
    val nomorRekening: LiveData<String> get() = mutableNomorRekening
    val nomorRekeningError: LiveData<String> get() = mutableNomorRekeningError

    private val mutableNamaRekening = MutableLiveData("")
    private val mutableNamaRekeningError = MutableLiveData("")
    private val isNamaRekeningValid = MutableLiveData(false)
    val namaRekening: LiveData<String> get() = mutableNamaRekening
    val namaRekeningError: LiveData<String> get() = mutableNamaRekeningError

    /* Validation functions for the first sign up screen */
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

    /* Validation functions for the second sign up screen */
    fun validateNamaRestoran(namaRestoran: String): Boolean {
        if (namaRestoran.isEmpty() || namaRestoran.isBlank()) {
            mutableNamaRestoranError.value = "Nama restoran tidak boleh kosong"
        } else if (namaRestoran.trim().length <= 3) {
            mutableNamaRestoranError.value = "Nama restoran harus lebih dari 3 karakter"
        } else {
            mutableNamaRestoranError.value = ""
        }

        mutableNamaRestoran.value = namaRestoran
        isNamaRestoranValid.value = mutableNamaRestoranError.value!!.isEmpty()
        return isNamaRestoranValid.value!!
    }

    fun validateNamaFoodcourt(namaFoodcourt: String): Boolean {
        mutableNamaFoodcourt.value = namaFoodcourt
        isNamaFoodcourtValid.value = true
        return true
    }

    fun validateAlamat(alamat: String): Boolean {
        if (alamat.isEmpty() || alamat.isBlank()) {
            mutableAlamatError.value = "Alamat tidak boleh kosong"
        } else if (alamat.trim().length <= 6) {
            mutableAlamatError.value = "Alamat harus lebih dari 6 karakter"
        } else {
            mutableAlamatError.value = ""
        }

        mutableAlamat.value = alamat
        isAlamatValid.value = mutableAlamatError.value!!.isEmpty()
        return isAlamatValid.value!!
    }

    fun validateNamaBank(namaBank: String): Boolean {
        Log.d("Hai", namaBank)
        if (namaBank.isEmpty() || namaBank.isBlank()) {
            mutableNamaBankError.value = "Silakan pilih bank anda"
        } else {
            mutableNamaBankError.value = ""
        }

        mutableNamaBank.value = namaBank
        isNamaBankValid.value = mutableNamaBankError.value!!.isEmpty()
        return isNamaBankValid.value!!
    }

    fun validateNamaBank(): Boolean {
        return isNamaBankValid.value!!
    }

    fun validateNomorRekening(nomorRekening: String): Boolean {
        if (nomorRekening.isEmpty() || nomorRekening.isBlank()) {
            mutableNomorRekeningError.value = "Nomor rekening bank tidak boleh kosong"
        } else if (!nomorRekening.isDigitsOnly()) {
            mutablePhoneError.value = "Nomor rekening bank hanya dapat berisi angka"
        } else if (nomorRekening.trim().length <= 6) {
            mutableNomorRekeningError.value = "Nomor rekening bank harus lebih dari 6 digit angka"
        } else {
            mutableNomorRekeningError.value = ""
        }

        mutableNomorRekening.value = nomorRekening
        isNomorRekeningValid.value = mutableNomorRekeningError.value!!.isEmpty()
        return isNomorRekeningValid.value!!
    }

    fun validateNamaRekening(namaRekening: String): Boolean {
        if (namaRekening.isEmpty() || namaRekening.isBlank()) {
            mutableNamaRekeningError.value = "Nama pemilik rekening tidak boleh kosong"
        } else if (namaRekening.trim().length <= 3) {
            mutableNamaRekeningError.value = "Nama pemilik rekening harus lebih dari 3 karakter"
        } else {
            mutableNamaRekeningError.value = ""
        }

        mutableNamaRekening.value = namaRekening
        isNamaRekeningValid.value = mutableNamaRekeningError.value!!.isEmpty()
        return isNamaRekeningValid.value!!
    }

    fun validateSecondPage(): Boolean = isNamaRestoranValid.value!! && isNamaFoodcourtValid.value!!
            && isAlamatValid.value!! && isNamaBankValid.value!! && isNomorRekeningValid.value!!
            && isNamaRekeningValid.value!!
}