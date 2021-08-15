package com.tsab.pikapp.viewmodel.onboarding.signup

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

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

    /* Variables for the third sign up screen */
    private val mutableKtp = MutableLiveData(Uri.EMPTY)
    private val mutableKtpError = MutableLiveData("")
    private val isKtpValid = MutableLiveData(false)
    val ktp: LiveData<Uri> get() = mutableKtp
    val ktpError: LiveData<String> get() = mutableKtpError

    private val mutableLogo = MutableLiveData(Uri.EMPTY)
    private val mutableLogoError = MutableLiveData("")
    private val isLogoValid = MutableLiveData(false)
    val logo: LiveData<Uri> get() = mutableLogo
    val logoError: LiveData<String> get() = mutableLogoError

    private val mutableLatar = MutableLiveData(Uri.EMPTY)
    private val mutableLatarError = MutableLiveData("")
    private val isLatarValid = MutableLiveData(false)
    val latar: LiveData<Uri> get() = mutableLatar
    val latarError: LiveData<String> get() = mutableLatarError

    private val mutableIsUploading = MutableLiveData(false)
    val isUploading: LiveData<Boolean> get() = mutableIsUploading
    private val mutableIsUploadingSuccess = MutableLiveData(false)
    val isUploadingSuccess: LiveData<Boolean> get() = mutableIsUploadingSuccess
    private val mutableIsUploadingFailed = MutableLiveData(false)
    val isUploadingFailed: LiveData<Boolean> get() = mutableIsUploadingFailed

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

    fun validateFirstPage(): Boolean =
        isEmailValid.value!! && isFullNameValid.value!! && isPhoneValid.value!! && isPinValid.value!!

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

    /* Validation functions for the third sign up screen */
    fun validateKtp(ktp: Uri?): Boolean {
        if (ktp == null || ktp == Uri.EMPTY) {
            mutableKtpError.value = "KTP tidak boleh kosong"
            isKtpValid.value = false
        } else {
            mutableKtpError.value = ""
            mutableKtp.value = ktp
            isKtpValid.value = true
        }
        return isKtpValid.value!!
    }

    fun validateLogo(logo: Uri?): Boolean {
        if (logo == null || logo == Uri.EMPTY) {
            mutableLogoError.value = "Logo restoran tidak boleh kosong"
            isLogoValid.value = false
        } else {
            mutableLogoError.value = ""
            mutableLogo.value = logo
            isLogoValid.value = true
        }
        return isLogoValid.value!!
    }

    fun validateLatar(latar: Uri?): Boolean {
        if (latar == null || latar == Uri.EMPTY) {
            mutableLatarError.value = "Gambar latar tidak boleh kosong"
            isLatarValid.value = false
        } else {
            mutableLatarError.value = ""
            mutableLatar.value = latar
            isLatarValid.value = true
        }
        return isLatarValid.value!!
    }

    fun validateThirdPage(): Boolean =
        isKtpValid.value!! && isLogoValid.value!! && isLatarValid.value!!

    fun uploadData(fcmToken: String) {
        mutableIsUploading.value = true
        mutableIsUploadingSuccess.value = false
        mutableIsUploadingFailed.value = false

        val application = getApplication<Application>()

        val ktpParcelFileDescriptor = application.contentResolver.openFileDescriptor(
            ktp.value!!,
            "r", null
        ) ?: return
        val ktpInputStream = FileInputStream(ktpParcelFileDescriptor.fileDescriptor)
        val ktpFile = File(
            application.cacheDir, application.contentResolver.getFileName(
                ktp.value!!
            )
        )
        val ktpOutputStream = FileOutputStream(ktpFile)
        ktpInputStream.copyTo(ktpOutputStream)

        val logoParcelFileDescriptor = application.contentResolver.openFileDescriptor(
            logo.value!!, "r", null
        ) ?: return
        val logoInputStream = FileInputStream(logoParcelFileDescriptor.fileDescriptor)
        val logoFile = File(
            application.cacheDir,
            application.contentResolver.getFileName(logo.value!!)
        )
        val logoOutputStream = FileOutputStream(logoFile)
        logoInputStream.copyTo(logoOutputStream)

        val latarParcelFileDescriptor = application.contentResolver.openFileDescriptor(
            latar.value!!, "r", null
        ) ?: return
        val latarInputStream = FileInputStream(latarParcelFileDescriptor.fileDescriptor)
        val latarFile = File(
            application.cacheDir,
            application.contentResolver.getFileName(latar.value!!)
        )
        val latarOutputStream = FileOutputStream(latarFile)
        latarInputStream.copyTo(latarOutputStream)

        val branch = "${namaRestoran.value} Branch"

        apiService.api.uploadRegister(
            getUUID(), getClientID(), getTimestamp(),

            MultipartBody.Part.createFormData(
                "file_01", ktpFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), ktpFile)
            ),
            MultipartBody.Part.createFormData(
                "file_02", logoFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), logoFile)
            ),
            MultipartBody.Part.createFormData(
                "file_03", latarFile.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), latarFile)
            ),

            RequestBody.create(MediaType.parse("multipart/form-data"), alamat.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
            RequestBody.create(MediaType.parse("multipart/form-data"), namaBank.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), branch),
            RequestBody.create(MediaType.parse("multipart/form-data"), nomorRekening.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), namaRekening.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), email.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), phone.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), namaRestoran.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), fcmToken),
            RequestBody.create(MediaType.parse("multipart/form-data"), pin.value!!),
            RequestBody.create(MediaType.parse("multipart/form-data"), "No Bank"),
            RequestBody.create(MediaType.parse("multipart/form-data"), namaFoodcourt.value!!)
        ).enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                mutableIsUploading.value = false

                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    mutableIsUploadingSuccess.value = true
                } else {
                    mutableIsUploadingFailed.value = true

                    val errorResponse: BaseResponse? = Gson().fromJson(
                        response.errorBody()!!.charStream(),
                        object : TypeToken<BaseResponse>() {}.type
                    )
                    Log.e(
                        "UploadRegisterError", generateResponseMessage(
                            errorResponse?.errCode,
                            errorResponse?.errMessage
                        ) ?: "Unknown error"
                    )
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Log.e("UploadRegisterError", t.message.toString())

                mutableIsUploading.value = false
                mutableIsUploadingFailed.value = true
            }
        })
    }
}