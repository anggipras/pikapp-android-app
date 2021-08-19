package com.tsab.pikapp.viewmodel.other

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting.ShopManagementAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    //Loading
    private val disposable = CompositeDisposable()
    var isLoading = MutableLiveData<Boolean>()
    var isLoadingBackButton = MutableLiveData(false)

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
        val isPositionOfAlert = sessionManager.getProfileNum()
        when(isPositionOfAlert) {
            0 -> sessionManager.setProfileNum(1)
            1-> sessionManager.setProfileNum(2)
        }
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

    //LOADING
    fun loadProcess(bool: Boolean) {
        isLoading.value = bool
    }

    //Load merchant profile
    fun getMerchantProfile() {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()

        disposable.add(
                PikappApiService().api.getMerchantProfile(uuid, timeStamp, clientId, signature, token, mid)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                            override fun onSuccess(t: MerchantProfileResponse) {
                                t.results?.let { res ->
                                    merchantProfileRetrieved(res)
                                }
                            }

                            override fun onError(e: Throwable) {
                                //Should print out error
                            }

                        })
        )

    }

    fun merchantProfileRetrieved(response: MerchantProfileData) {
        sessionManager.setMerchantProfile(response)
        sessionManager.setProfileNum(2)
        loadProcess(false)
        isLoadingBackButton.value = true
    }

    //RESTART GENDER DIALOG
    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }
}
