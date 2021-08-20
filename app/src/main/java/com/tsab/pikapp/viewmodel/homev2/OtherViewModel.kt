package com.tsab.pikapp.viewmodel.homev2

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsab.pikapp.models.model.MerchantProfileData
import com.tsab.pikapp.models.model.MerchantProfileResponse
import com.tsab.pikapp.models.model.MerchantTimeManagement
import com.tsab.pikapp.models.model.ShopSchedule
import com.tsab.pikapp.models.network.PikappApiService
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

    val merchantResult = MutableLiveData<MerchantProfileData>()
    val merchantShopStatus = MutableLiveData<ShopSchedule>()

    fun getMerchantProfile(context: Context) {
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
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                    override fun onSuccess(t: MerchantProfileResponse) {
                        t.results?.let { res ->
                            merchantProfileRetrieved(res)
                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }

    fun merchantProfileRetrieved(response: MerchantProfileData) {
        Log.d("RESPONSEE", response.toString())
        merchantResult.value = response
        sessionManager.setMerchantProfile(response)
    }

    fun getMerchantShopStatus(context: Context) {
        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)

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
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<MerchantTimeManagement>,
                response: Response<MerchantTimeManagement>
            ) {
                val timeManagementResult = response.body()?.results?.timeManagement
                val filteredDay = timeManagementResult?.filter { selectedDay ->
                    selectedDay.days == dayOfTheWeek.toUpperCase()
                }
                merchantShopStatus.value = filteredDay?.get(0)
            }
        })
    }
}