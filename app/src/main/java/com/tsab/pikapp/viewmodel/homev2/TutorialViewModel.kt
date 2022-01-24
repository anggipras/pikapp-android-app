package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.TutorialGetResponse
import com.tsab.pikapp.models.model.TutorialPostRequest
import com.tsab.pikapp.models.model.TutorialPostResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutorialViewModel(application: Application) : BaseViewModel(application) {

    val mutableMenuTabs = MutableLiveData(false)
    val menuTabs: LiveData<Boolean> get() = mutableMenuTabs

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()
    private var sessionManager = SessionManager(getApplication())

    fun postTutorial(type: String){
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        var mid = sessionManager.getUserData()?.mid
        var timestamp = getTimestamp()
        var uuid = getUUID()
        var clientId = getClientID()
        var signature = getSignature(email, timestamp)

        PikappApiService().api.updateTutorial(uuid, timestamp, clientId, signature, token, TutorialPostRequest(mid.toString(), type)
        ).enqueue(object : Callback<TutorialPostResponse>{
            override fun onResponse(
                call: Call<TutorialPostResponse>,
                response: Response<TutorialPostResponse>
            ) {
                Log.e("Status", response.code().toString())
            }

            override fun onFailure(call: Call<TutorialPostResponse>, t: Throwable) {
                Log.e("Status", t.message.toString())
            }

        })
    }

    fun getTutorial(name: String){
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        var mid = sessionManager.getUserData()?.mid
        var timestamp = getTimestamp()
        var uuid = getUUID()
        var clientId = getClientID()
        var status = false
        var signature = getSignature(email, timestamp)

        PikappApiService().api.getTutorial(uuid, timestamp, clientId, signature, token, mid, mid.toString())
            .enqueue(object : Callback<TutorialGetResponse>{
                override fun onResponse(
                    call: Call<TutorialGetResponse>,
                    response: Response<TutorialGetResponse>
                ) {
                    Log.e("Response", response.code().toString())
                    Log.e("Response", response.body()?.results.toString())
                    if(response.body()?.results?.isEmpty() == true){
                        status = false
                    }else{
                        for (i in response.body()?.results!!){
                            if(i.tutorial_page == name){
                                status = true
                                mutableMenuTabs.value = true
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TutorialGetResponse>, t: Throwable) {
                    Log.e("error", t.message.toString())
                }

            })
    }

}