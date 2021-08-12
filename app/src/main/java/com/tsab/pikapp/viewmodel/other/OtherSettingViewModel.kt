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

class OtherSettingViewModel : ViewModel() {

    private var sessionManager = SessionManager()

    //Profile Setting
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    //Information Setting
    val _restaurantBanner = MutableLiveData(Uri.EMPTY)
    val _restaurantLogo = MutableLiveData(Uri.EMPTY)
    val _restaurantName = MutableLiveData<String>()
    val _restaurantAddress = MutableLiveData<String>()

    //Pin Setting
    val _newPin = MutableLiveData<String>()

    //Shop Management Setting
    lateinit var shopManagementAdapter: ShopManagementAdapter
    var _shopStatus = MutableLiveData<String>()

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

    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }

    //Information Setting Method
    fun setBannerImg(banner: Uri?) {
        _restaurantBanner.value = banner
    }

    fun setLogoImg(logo: Uri?) {
        _restaurantLogo.value = logo
    }

    fun setNewMerchNameAndAddress(merchName: String?, merchAddress: String?) {
        _restaurantName.value = merchName!!
        _restaurantAddress.value = merchAddress!!
    }

    //Pin Setting Method
    fun setNewPin(pin: String?) {
        _newPin.value = pin!!
    }

    //Shop Management Method
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
}
