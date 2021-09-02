package com.tsab.pikapp.viewmodel.omni.integration

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.IntegrationArrayResponse
import com.tsab.pikapp.models.model.Omnichannel
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class IntegrationViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName

    private var sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    /**
     * General screen flow.
     */
    private val mutableIsLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val mutableIntegrationList = MutableLiveData<List<Omnichannel>>(listOf())
    val integrationList: LiveData<List<Omnichannel>> = mutableIntegrationList
    fun setIntegrationList(integrationList: List<Omnichannel>) {
        mutableIntegrationList.value = integrationList
    }

    fun fetchIntegrationList() {
        setLoading(true)
        disposable.add(
            apiService.listIntegration(
                merchantId = sessionManager.getMerchantProfile()?.mid ?: ""
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<IntegrationArrayResponse>() {
                    override fun onSuccess(response: IntegrationArrayResponse) {
                        if (!response.results.isNullOrEmpty()) {
                            setIntegrationList(response.results ?: listOf())
                        }
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(tag, e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }
}