package com.tsab.pikapp.viewmodel.homev2

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OtherViewModel : ViewModel() {
    private val disposable = CompositeDisposable()
    private var sessionManager = SessionManager()
    private val onlineService = OnlineService()

    val merchantResult = MutableLiveData<MerchantProfileData>()
    val merchantShopStatus = MutableLiveData<ShopSchedule>()

    private val mutableErrCode = MutableLiveData("")
    val errCode: LiveData<String> get() = mutableErrCode

    fun getMerchantProfile(
        context: Context,
        requireActivity: FragmentActivity,
        general_error_other: View
    ) {
        val timeStamp = getTimestamp()
        val email: String?
        val mid: String?
        val userDomain = sessionManager.getUserDomain()
        if (sessionManager.getUserData() != null) {
            email = sessionManager.getUserData()?.email
            mid = sessionManager.getUserData()?.mid
        } else {
            email = "johndoe@gmail.com"
            mid = "M00000008"
        }

        val token = if (sessionManager.getUserToken() != null) {
            sessionManager.getUserToken()!!
        } else {
            "1a2b3c4d5e6f7g8h9i0j"
        }

        val signature = getSignature(email, timeStamp)
        val uuid = getUUID()
        val clientId = getClientID()

        disposable.add(
            PikappApiService().api.getMerchantProfile(
                uuid, timeStamp, clientId, signature, token, userDomain
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                    override fun onSuccess(t: MerchantProfileResponse) {
                        general_error_other.isVisible = false
                        t.results?.let { res ->
                            Log.e("THEREST", res.domain)
                            merchantProfileRetrieved(res)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                        onlineService.serviceDialog(requireActivity)
                        general_error_other.isVisible = true
                    }
                })
        )
    }

    fun merchantProfileRetrieved(response: MerchantProfileData) {
        merchantResult.value = response
        sessionManager.setMerchantProfile(response)
    }

    fun getMerchantShopStatus(
        context: Context,
        requireActivity: FragmentActivity,
        general_error_other: View
    ) {
        val sdf = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)

        val uuid = getUUID()
        val timestamp = getTimestamp()
        val clientId = getClientID()
        val email: String?
        val mid: String?
        if (sessionManager.getUserData() != null) {
            email = sessionManager.getUserData()?.email
            mid = sessionManager.getUserData()?.mid
        } else {
            email = "johndoe@gmail.com"
            mid = "M00000008"
        }

        val token = if (sessionManager.getUserToken() != null) {
            sessionManager.getUserToken()!!
        } else {
            "1a2b3c4d5e6f7g8h9i0j"
        }
        val signature = getSignature(email, timestamp)

        PikappApiService().api.getMerchantShopManagement(
            uuid, timestamp, clientId, signature, token, mid
        ).enqueue(object : Callback<MerchantTimeManagement> {
            override fun onFailure(call: Call<MerchantTimeManagement>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                onlineService.serviceDialog(requireActivity)
                general_error_other.isVisible = true
            }

            override fun onResponse(
                call: Call<MerchantTimeManagement>,
                response: Response<MerchantTimeManagement>
            ) {
                val gson = Gson()
                val type = object : TypeToken<MerchantTimeManagement>() {}.type
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    general_error_other.isVisible = false
                    val timeManagementResult = response.body()?.results?.timeManagement
                    val filteredDay = timeManagementResult?.filter { selectedDay ->
                        selectedDay.days == dayOfTheWeek.uppercase(Locale.getDefault())
                    }
                    merchantShopStatus.value = filteredDay?.get(0)
                }  else {
                    var errorResponse: MerchantTimeManagement? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                    mutableErrCode.value = errorResponse?.errCode
                }
            }
        })
    }
}