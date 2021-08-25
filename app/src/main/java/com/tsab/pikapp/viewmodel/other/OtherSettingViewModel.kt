package com.tsab.pikapp.viewmodel.other

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting.ShopManagementAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.*
import retrofit2.Response

class OtherSettingViewModel : ViewModel() {
    private var sessionManager = SessionManager()
    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type
    private val disposable = CompositeDisposable()
    private val apiService = PikappApiService()

    // Profile Setting Variable
    val profileFullName = MutableLiveData<String>()
    val profileDOB = MutableLiveData<String>()
    val profileGender = MutableLiveData<String>()
    val profileEmail = MutableLiveData<String>()
    val profilePhone = MutableLiveData<String>()
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String?>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    // Information Setting Variable
    val _restaurantBanner = MutableLiveData(Uri.EMPTY)
    val _restaurantLogo = MutableLiveData(Uri.EMPTY)

    // Pin Setting Variable
    val currentPin = MutableLiveData<String>()
    val _newPin = MutableLiveData<String>()
    val loadPin = MutableLiveData<Boolean>()
    val pinAlert = MutableLiveData<String>()

    // Shop Management Setting Variable
    lateinit var shopManagementAdapter: ShopManagementAdapter
    private var _shopStatus = MutableLiveData<String>()

    private val mutableDays = MutableLiveData("")
    val days: LiveData<String> get() = mutableDays

    private val mutableOpenTime = MutableLiveData("")
    private val mutableOpenTimeError = MutableLiveData("")
    val isOpenTimeValid = MutableLiveData(false)
    val openTime: LiveData<String> get() = mutableOpenTime
    val openTimeError: LiveData<String> get() = mutableOpenTimeError

    private val mutableCloseTime = MutableLiveData("")
    private val mutableCloseTimeError = MutableLiveData("")
    val isCloseTimeValid = MutableLiveData(false)
    val closeTime: LiveData<String> get() = mutableCloseTime
    val closeTimeError: LiveData<String> get() = mutableOpenTimeError

    private val mutableIsForceClose = MutableLiveData(true)
    val isForceClose: LiveData<Boolean> get() = mutableIsForceClose

    private val mutableAutoOnOff = MutableLiveData(true)
    val autoOnOff: LiveData<Boolean> get() = mutableAutoOnOff

    private val mutableScheduleList = MutableLiveData<List<ShopSchedule>>(listOf())
    val shopScheduleResult: LiveData<List<ShopSchedule>> = mutableScheduleList
    fun setShopSchedule(shopScheduleResult: List<ShopSchedule>) {
        mutableScheduleList.value = shopScheduleResult
    }

    private val mutableLoading = MutableLiveData(true)
    val Loading: LiveData<Boolean> get() = mutableLoading

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Loading
    var isLoading = MutableLiveData<Boolean>()
    var isLoadingBackButton = MutableLiveData(false)

    // Profile Setting Method
    fun getProfileDetail() {
        val fullName = sessionManager.getMerchantProfile()?.fullName!!
        val dateOfBirth = sessionManager.getDOBProfile()!!
        val gender = sessionManager.getGenderProfile()!!
        val email = sessionManager.getMerchantProfile()!!.email!!
        val phone = sessionManager.getMerchantProfile()!!.phoneNumber!!
        profileFullName.value = fullName
        profileDOB.value = dateOfBirth
        profileGender.value = gender
        profileEmail.value = email
        profilePhone.value = phone
    }

    fun setGenderAndDOB() {
        val gender = when (_genderSelection.value) {
            "Perempuan" -> "FEMALE"
            else -> "MALE"
        }

        sessionManager.setDOBProfile(_birthdaySelection.value)
        sessionManager.setGenderProfile(gender)

        when (sessionManager.getProfileNum()) {
            0 -> sessionManager.setProfileNum(1)
            1 -> sessionManager.setProfileNum(1)
            2 -> sessionManager.setProfileNum(2)
        }
    }

    fun setDefaultGender() {
        _genderSelection.value = null
    }

    // Pin Setting Method
    fun setCurrentPin(pin: String?) {
        currentPin.value = pin!!
    }

    fun setNewPin(pin: String?) {
        _newPin.value = pin!!
    }

    fun onLoadChangePin(bool: Boolean) {
        loadPin.value = bool
    }

    fun changePinAlert(alert: String) {
        pinAlert.value = alert
    }

    fun changePin() {
        onLoadChangePin(true)
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()

        val pinModel = createPinModel(mid)

        PikappApiService().api.changePinMerchant("application/json", uuid, timeStamp, clientId, signature, token, pinModel)
            .enqueue(object : Callback<OtherBaseResponse> {
                override fun onResponse(call: Call<OtherBaseResponse>, response: Response<OtherBaseResponse>) {
                    if (response.code() == 200) {
                        changePinAlert("APPROVED/OK")
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<OtherBaseResponse>() {}.type


                        var errorResponse: OtherBaseResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)

                        if (errorResponse != null) {
                            changePinAlert(errorResponse.errMessage!!)
                        }
                    }
                }

                override fun onFailure(call: Call<OtherBaseResponse>, t: Throwable) {
                    Log.d("ONFAILURE", t.toString())
                }

            })
    }

    private fun createPinModel(mid: String) : pinMerchant {
        return pinMerchant(
            oldPin = currentPin.value,
            mid = mid,
            pin = _newPin.value
        )
    }

    // Information Setting Method
    fun setBannerImg(banner: Uri?) {
        _restaurantBanner.value = banner
    }

    fun setLogoImg(logo: Uri?) {
        _restaurantLogo.value = logo
    }

    // Shop Management Setting Method
    fun setGender(bool: Boolean, gender: String) {
        _genderConfirmation.value = bool
        _genderSelection.value = gender
    }

    fun setBirthday(date: String?) {
        _birthdaySelection.value = date!!
    }

    fun confirmGender(bool: Boolean) {
        _genderDialogAlert.value = bool
    }

    fun getMerchantSchedule(
        baseContext: Context,
        shopSchedule_recyclerView: RecyclerView,
        listener: ShopManagementAdapter.OnItemClickListener
    ) {
        val uuid = getUUID()
        val timestamp = getTimestamp()
        val clientId = getClientID()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timestamp)
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMerchantShopManagement(
            uuid, timestamp, clientId, signature, token, mid
        ).enqueue(object : Callback<MerchantTimeManagement> {
            override fun onFailure(call: Call<MerchantTimeManagement>, t: Throwable) {
                Log.e("getTimeManagementFailed", t.message.toString())
            }

            override fun onResponse(
                call: Call<MerchantTimeManagement>,
                response: Response<MerchantTimeManagement>
            ) {
                val timeManagementResult = response.body()?.results?.timeManagement
                shopManagementAdapter = ShopManagementAdapter(
                    baseContext,
                    timeManagementResult as MutableList<ShopSchedule>,
                    listener
                )

                shopManagementAdapter.notifyDataSetChanged()
                shopSchedule_recyclerView.adapter = shopManagementAdapter
                setShopSchedule(timeManagementResult)
                mutableLoading.value = false
                Log.e("schedule vm", shopScheduleResult.value.toString())
                Log.e("schdule", mutableScheduleList.value.toString())
            }

        })
    }

    fun updateShopStatus(baseContext: Context){
        //var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        var shopStatusReq = ShopManagementUpdateRequest()
        shopStatusReq.days = days.value
        shopStatusReq.closeTime = closeTime.value
        shopStatusReq.openTime = openTime.value
        shopStatusReq.isForceClose = isForceClose.value
        shopStatusReq.autoOnOff = autoOnOff.value

        Log.e("token", token)
        Log.e("timestamp", timestamp)
        Log.e("signature", signature)
        Log.e("mid", mid)

        Log.e("status days", shopStatusReq.days)
        Log.e("status close time", shopStatusReq.closeTime)
        Log.e("status open time", shopStatusReq.openTime)
        Log.e("status force close", shopStatusReq.isForceClose.toString())
        Log.e("status auto on off", shopStatusReq.autoOnOff.toString())

        PikappApiService().api.updateShopManagement(
                getUUID(), timestamp, getClientID(), signature, token, mid, shopStatusReq
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "changes saved", Toast.LENGTH_SHORT).show()
                } else {
                    var errorResponse: BaseResponse? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(
                            baseContext,
                            generateResponseMessage(
                                    errorResponse?.errCode,
                                    errorResponse?.errMessage
                            ).toString(),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun validateOpenTime(openTIme: String): Boolean {
        if (openTIme.isEmpty() || openTIme.isBlank()) {
            mutableOpenTimeError.value = "Jam Buka tidak boleh kosong"
        } else {
            mutableOpenTimeError.value = ""
        }
        mutableOpenTime.value = openTIme
        isOpenTimeValid.value = mutableOpenTimeError.value!!.isEmpty()
        return isOpenTimeValid.value!!
    }

    fun validateCloseTime(closeTime: String): Boolean {
        if (closeTime.isEmpty() || closeTime.isBlank()) {
            mutableCloseTimeError.value = "Jam Tutup tidak boleh kosong"
        } else {
            mutableCloseTimeError.value = ""
        }
        mutableCloseTime.value = closeTime
        isCloseTimeValid.value = mutableCloseTime.value!!.isEmpty()
        return isCloseTimeValid.value!!
    }

    fun setShopStatus(status: String) {
        _shopStatus.value = status
    }

    // Loading
    fun loadProcess(bool: Boolean) {
        isLoading.value = bool
    }

    // Load merchant profile
    fun getMerchantProfile() {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()

        disposable.add(
            PikappApiService().api.getMerchantProfile(
                uuid, timeStamp, clientId, signature, token, mid
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                    override fun onSuccess(t: MerchantProfileResponse) {
                        t.results?.let { res ->
                            merchantProfileRetrieved(res)
                        }
                    }

                    override fun onError(e: Throwable) {}
                })
        )
    }

    fun merchantProfileRetrieved(response: MerchantProfileData) {
        sessionManager.setMerchantProfile(response)
        sessionManager.setProfileNum(2)

        loadProcess(false)
        isLoadingBackButton.value = true
    }

    // Restart gender dialog
    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }

    fun getDays(days: String) {
        mutableDays.value = days
    }

    fun getOpenTime(openTime: String) {
        mutableOpenTime.value = openTime
    }

    fun getCLoseTime(closeTime: String) {
        mutableCloseTime.value = closeTime
    }

    fun getForceClose(isForceClose: Boolean) {
        mutableIsForceClose.value = isForceClose
    }

    fun setAutoOnOffTrue(autoOnOff: Boolean){
        mutableAutoOnOff.value = true
    }

    fun setAutoOnOffFalse(autoOnOff: Boolean){
        mutableAutoOnOff.value = false
    }
}
