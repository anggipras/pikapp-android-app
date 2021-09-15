package com.tsab.pikapp.viewmodel.omni.integration

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.*
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

    // General screen flow.

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
                        Log.d(tag, response.toString())
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

    fun disconnectIntegration(omnichannel: Omnichannel) {
        setLoading(true)
        disposable.add(
            apiService.disconnectIntegration(
                channelId = omnichannel.channelId
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<IntegrationObjectResponse>() {
                    override fun onSuccess(response: IntegrationObjectResponse) {
                        Log.d(tag, response.toString())

                        removeIntegration(omnichannel)
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(tag, e.message.toString())
                    }
                })
        )
    }

    /**
     * Method to insert a newly connected omnichannel to the list.
     */
    fun addIntegration(omnichannel: Omnichannel) {
        if (!integrationList.value!!.none { it.id == omnichannel.id }) {
            setIntegrationList(integrationList.value?.filter { it.id != omnichannel.id }
                ?: listOf())
        }

        setIntegrationList(integrationList.value?.toMutableList()?.apply {
            add(omnichannel)
        } ?: listOf())
    }

    fun removeIntegration(omnichannel: Omnichannel) {
        setIntegrationList(integrationList.value?.filter { it.id != omnichannel.id } ?: listOf())
    }

    // List integration flow.

    /**
     * Group integration list according to their status.
     */
    private val mutableSelectedStatus = MutableLiveData<OmnichannelStatus?>(null)
    val selectedStatus: LiveData<OmnichannelStatus?> = mutableSelectedStatus
    fun setSelectedStatus(selectedStatus: OmnichannelStatus?) {
        mutableSelectedStatus.value = selectedStatus
        updateSelectedList()
    }

    private val mutableSelectedList = MutableLiveData<List<Omnichannel>>(listOf())
    val selectedList: LiveData<List<Omnichannel>> = mutableSelectedList
    private fun updateSelectedList() {
        mutableSelectedList.value = integrationList.value?.filter { item ->
            if (selectedStatus.value == null) true
            else item.status == selectedStatus.value
        }
    }

    // Connect integration flow.

    private val mutableConnectOmnichannelType = MutableLiveData<OmnichannelType?>(null)
    val currentConnectOmnichannelType: LiveData<OmnichannelType?> = mutableConnectOmnichannelType
    fun setCurrentConnectOmnichannelType(omnichannelType: OmnichannelType) {
        mutableConnectOmnichannelType.value = omnichannelType
    }
}