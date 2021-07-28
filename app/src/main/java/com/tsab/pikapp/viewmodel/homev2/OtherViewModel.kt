package com.tsab.pikapp.viewmodel.homev2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsab.pikapp.models.model.MerchantDetail
import com.tsab.pikapp.models.model.MerchantProfileResponse
import com.tsab.pikapp.models.model.ProfileResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.getClientID
import com.tsab.pikapp.util.getTimestamp
import com.tsab.pikapp.util.getUUID
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class OtherViewModel : ViewModel() {
    private val disposable = CompositeDisposable()

    val merchantResult = MutableLiveData<ProfileResponse>()

    fun getMerchantProfile() {
        var sessionManager = SessionManager()
        val token = "PUBLIC"
        val timeStamp = getTimestamp()
        val mid = sessionManager.getUserData()!!.mid!!
        val uuid = getUUID()
        val clientId = getClientID()
        val longitude = "123456"
        val latitude = "109382"

        disposable.add(
                PikappApiService().api.getMerchantProfile(uuid, timeStamp, clientId, token, mid, longitude, latitude)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<MerchantProfileResponse>() {
                            override fun onSuccess(t: MerchantProfileResponse) {
                                t.results?.let { res -> merchantProfileRetrieved(res) }
                            }

                            override fun onError(e: Throwable) {

                            }

                        })
        )

    }

    fun merchantProfileRetrieved(response: ProfileResponse) {
        merchantResult.value = response
    }
}
