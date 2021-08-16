package com.tsab.pikapp.viewmodel.homev2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsab.pikapp.models.model.MerchantProfileData
import com.tsab.pikapp.models.model.MerchantProfileResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class OtherViewModel : ViewModel() {
    private val disposable = CompositeDisposable()
    private var sessionManager = SessionManager()

    val merchantResult = MutableLiveData<MerchantProfileData>()

    fun getMerchantProfile() {
        val timeStamp = getTimestamp()
        val email = sessionManager.getUserData()!!.email!!
        val mid = sessionManager.getUserData()!!.mid!!
        val signature = getSignature(email, timeStamp)
        val token = sessionManager.getUserToken()!!
        val uuid = getUUID()
        val clientId = getClientID()

//        Log.d("UUID", uuid)
//        Log.d("TIMESTAMP", timeStamp)
//        Log.d("CLIENTID", clientId)
//        Log.d("TIMESTAMP", timeStamp)
//        Log.d("SIGNATURE", signature)
//        Log.d("TOKEN", token)

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
        merchantResult.value = response
        sessionManager.setMerchantProfile(response)
    }
}