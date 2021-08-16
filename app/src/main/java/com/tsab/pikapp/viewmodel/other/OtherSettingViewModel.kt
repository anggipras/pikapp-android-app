package com.tsab.pikapp.viewmodel.other

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.MerchantTimeManagement
import com.tsab.pikapp.models.model.ShopSchedule
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting.ShopManagementAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class OtherSettingViewModel : ViewModel() {

    private var sessionManager = SessionManager()

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
            }

        })
    }

    fun setShopStatus(status: String) {
        _shopStatus.value = status
    }

    //RESTART GENDER DIALOG
    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }
}
