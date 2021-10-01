package com.tsab.pikapp.viewmodel.omni.integration

import android.app.Application
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.IntegrationObjectResponse
import com.tsab.pikapp.models.model.Omnichannel
import com.tsab.pikapp.models.model.OmnichannelType
import com.tsab.pikapp.models.model.ShopCategory
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.isEmailValid
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class TokopediaIntegrationViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName

    private var sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    private val mutableIsLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val _isSetForAlarm = MutableLiveData<Boolean>()
    val isSetForAlarm: LiveData<Boolean> = _isSetForAlarm
    fun setForAlarm(isLoading: Boolean) {
        _isSetForAlarm.value = isLoading
    }

    private val mutableIsSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = mutableIsSuccess
    fun setSuccess(isSuccess: Boolean) {
        mutableIsSuccess.value = isSuccess
    }

    private val mutableSuccessResult = MutableLiveData<Omnichannel>()
    val successResult: LiveData<Omnichannel> = mutableSuccessResult
    fun setSuccessResult(successResult: Omnichannel) {
        mutableSuccessResult.value = successResult
    }

    private val mutableEmail = MutableLiveData("")
    private val mutableEmailError = MutableLiveData("")
    private val isEmailValid = MutableLiveData(false)
    val email: LiveData<String> = mutableEmail
    val emailError: LiveData<String> = mutableEmailError

    private val mutableTelepon = MutableLiveData("")
    private val mutableTeleponError = MutableLiveData("")
    private val isTeleponValid = MutableLiveData(false)
    val telepon: LiveData<String> = mutableTelepon
    val teleponError: LiveData<String> = mutableTeleponError

    private val mutableNamaToko = MutableLiveData("")
    private val mutableNamaTokoError = MutableLiveData("")
    private val isNamaTokoValid = MutableLiveData(false)
    val namaToko: LiveData<String> = mutableNamaToko
    val namaTokoError: LiveData<String> = mutableNamaTokoError

    private val mutableDomainToko = MutableLiveData("")
    private val mutableDomainTokoError = MutableLiveData("")
    private val isDomainTokoValid = MutableLiveData(false)
    val domainToko: LiveData<String> = mutableDomainToko
    val domainTokoError: LiveData<String> = mutableDomainTokoError

    private val mutableShopCategory = MutableLiveData<ShopCategory>(ShopCategory.OFFICIAL)
    private val isShopCategoryValid = MutableLiveData(false)
    val shopCategory: LiveData<ShopCategory> = mutableShopCategory

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

    fun validateTelepon(telepon: String): Boolean {
        if (telepon.isEmpty() || telepon.isBlank()) {
            mutableTeleponError.value = "Nomor telepon tidak boleh kosong"
        } else if (!telepon.isDigitsOnly()) {
            mutableTeleponError.value = "Nomor telepon hanya dapat berisi angka"
        } else if (telepon.trim().length <= 8) {
            mutableTeleponError.value = "Nomor telepon harus lebih dari 8 digit angka"
        } else {
            mutableTeleponError.value = ""
        }

        mutableTelepon.value = telepon
        isTeleponValid.value = mutableTeleponError.value!!.isEmpty()
        return isTeleponValid.value!!
    }

    fun validateNamaToko(namaToko: String): Boolean {
        if (namaToko.isEmpty() || namaToko.isBlank()) {
            mutableNamaTokoError.value = "Nama toko tidak boleh kosong"
        } else {
            mutableNamaTokoError.value = ""
        }

        mutableNamaToko.value = namaToko
        isNamaTokoValid.value = mutableNamaTokoError.value!!.isEmpty()
        return isNamaTokoValid.value!!
    }

    fun validateDomainToko(domainToko: String): Boolean {
        if (domainToko.isEmpty() || domainToko.isBlank()) {
            mutableDomainTokoError.value = "Domain toko tidak boleh kosong"
        } else {
            mutableDomainTokoError.value = ""
        }

        mutableDomainToko.value = domainToko
        isDomainTokoValid.value = mutableDomainTokoError.value!!.isEmpty()
        return isDomainTokoValid.value!!
    }

    fun validateShopCategory(shopCategory: ShopCategory) {
        mutableShopCategory.value = shopCategory
        isShopCategoryValid.value = true
    }

    fun validateAll(): Boolean =
        isEmailValid.value!! && isTeleponValid.value!! && isNamaTokoValid.value!! && isDomainTokoValid.value!! && isShopCategoryValid.value!!

    fun connectIntegration() {
        setLoading(true)
        disposable.add(
            apiService.connectIntegration(
                merchantId = sessionManager.getMerchantProfile()?.mid ?: "",
                email = email.value ?: "",
                phoneNumber = telepon.value ?: "",
                shopName = namaToko.value ?: "",
                shopDomain = domainToko.value ?: "",
                channelType = OmnichannelType.TOKOPEDIA,
                shopCategory = shopCategory.value ?: ShopCategory.OFFICIAL
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<IntegrationObjectResponse>() {
                    override fun onSuccess(response: IntegrationObjectResponse) {
                        setLoading(false)
                        setForAlarm(true)

                        setSuccessResult(
                            Omnichannel(
                                id = response.result.id,
                                name = response.result.name,
                                status = response.result.status,
                                channelId = response.result.channelId,
                                createdAt = response.result.createdAt,
                                updatedAt = response.result.updatedAt
                            )
                        )
                        setSuccess(true)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(tag, e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }
}