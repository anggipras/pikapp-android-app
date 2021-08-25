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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class OtherSettingViewModel : ViewModel() {

    private var sessionManager = SessionManager()
    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type

    //Profile Setting Variable
    val profileFullName = MutableLiveData<String>()
    val profileDOB = MutableLiveData<String>()
    val profileGender = MutableLiveData<String>()
    val profileEmail = MutableLiveData<String>()
    val profilePhone = MutableLiveData<String>()
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String?>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    //Information Setting Variable
    val _restaurantBanner = MutableLiveData(Uri.EMPTY)
    val _restaurantLogo = MutableLiveData(Uri.EMPTY)

    //Pin Setting Variable
    val _newPin = MutableLiveData<String>()

    //Shop Management Setting Variable
    lateinit var shopManagementAdapter: ShopManagementAdapter
    var _shopStatus = MutableLiveData<String>()

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

    private val mutableisLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = mutableisLoading

    ////////////////////////////////////////////////////////////////////////////////////////////////

    //Profile Setting Method
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
        val gender = when(_genderSelection.value) {
            "Perempuan" -> "FEMALE"
            else -> "MALE"
        }
        sessionManager.setDOBProfile(_birthdaySelection.value)
        sessionManager.setGenderProfile(gender)
        sessionManager.setProfileNum(1)
    }

    fun setDefaultGender() {
        _genderSelection.value = null
    }

    //Pin Setting Method
    fun setNewPin(pin: String?) {
        _newPin.value = pin!!
    }

    //Information Setting Method
    fun setBannerImg(banner: Uri?) {
        _restaurantBanner.value = banner
    }

    fun setLogoImg(logo: Uri?) {
        _restaurantLogo.value = logo
    }

    fun uploadMerchantProfile(banner: File, logo: File, merchName: String?, merchAddress: String?) {
        val timestamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timestamp)
        val token = sessionManager.getUserToken()!!

        //from session
        val gender = sessionManager.getGenderProfile()
        val dob = sessionManager.getDOBProfile()
        val bankAccountNo = sessionManager.getMerchantProfile()?.bankAccountNo
        val bankAccountName = sessionManager.getMerchantProfile()?.bankAccountName
        val bankName = sessionManager.getMerchantProfile()?.bankName

        Log.d("GENDER", gender.toString())
        Log.d("DOB", dob.toString())
        Log.d("BANNER", banner.toString())
        Log.d("LOGO", logo.toString())
        Log.d("NAME", merchName.toString())
        Log.d("ADDRESS", merchAddress.toString())

//        PikappApiService().api.uploadMerchantProfile(getUUID(), timestamp, getClientID(), signature, token,
//                MultipartBody.Part.createFormData("file_01", banner.name, RequestBody.create(MediaType.parse("multipart/form-data"), banner)),
//                MultipartBody.Part.createFormData("file_01", logo.name, RequestBody.create(MediaType.parse("multipart/form-data"), logo)),
//                RequestBody.create(MediaType.parse("multipart/form-data"), merchName),
//                RequestBody.create(MediaType.parse("multipart/form-data"), merchAddress),
//                RequestBody.create(MediaType.parse("multipart/form-data"), gender),
//                RequestBody.create(MediaType.parse("multipart/form-data"), dob),
//                RequestBody.create(MediaType.parse("multipart/form-data"), bankAccountNo),
//                RequestBody.create(MediaType.parse("multipart/form-data"), bankAccountName),
//                RequestBody.create(MediaType.parse("multipart/form-data"), bankName)
//        ).enqueue(object : Callback<BaseResponse> {
//            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
//                Log.d("ONRESPONSE", response.body().toString())
//                Log.d("UPLOAAAADEEED", "it's uploaded")
//                sessionManager.setProfileNum(2)
//            }
//
//            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
//                Log.d("FAAAILEEEEED", t.message.toString())
//                Log.d("FAAAILEEEEED", "it's failed")
//            }
//        })
    }

    //Shop Management Setting Method
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

    fun getMerchantSchedule(baseContext: Context, shopSchedule_recyclerView: RecyclerView, listener: ShopManagementAdapter.OnItemClickListener) {
        val uuid = getUUID()
        val timestamp = getTimestamp()
        val clientId = getClientID()
        val email = sessionManager.getUserData()!!.email!!
        val signature = getSignature(email, timestamp)
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

//        Log.d("uuid", uuid)
//        Log.d("timestamp", timestamp)
//        Log.d("clientId", clientId)
//        Log.d("signature", signature)
//        Log.d("token", token)
//        Log.d("mid", mid)

        PikappApiService().api.getMerchantShopManagement(
                uuid, timestamp, clientId, signature, token, mid
        ).enqueue(object : Callback<MerchantTimeManagement> {
            override fun onFailure(call: Call<MerchantTimeManagement>, t: Throwable) {
                Log.e("getTimeManagementFailed", t.message.toString())
            }

            override fun onResponse(call: Call<MerchantTimeManagement>, response: Response<MerchantTimeManagement>) {
                val timeManagementResult = response.body()?.results?.timeManagement

                shopManagementAdapter = ShopManagementAdapter(baseContext, timeManagementResult as MutableList<ShopSchedule>, listener)
                shopManagementAdapter.notifyDataSetChanged()
                shopSchedule_recyclerView.adapter = shopManagementAdapter
                setShopSchedule(timeManagementResult)
                mutableisLoading.value = false
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

    //RESTART GENDER DIALOG
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
