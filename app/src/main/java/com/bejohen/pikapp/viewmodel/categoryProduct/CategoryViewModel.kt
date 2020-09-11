package com.bejohen.pikapp.viewmodel.categoryProduct

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.PikappDatabase
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CategoryViewModel(application: Application) : BaseViewModel(application) {

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    private var prefHelper = SharedPreferencesUtil(getApplication())
    val categoryData = MutableLiveData<ItemHomeCategory>()

    val merchantResponse = MutableLiveData<List<MerchantList>>()
    val merchantLoadError = MutableLiveData<Boolean>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    var categoryID = ""
    var latitude = ""
    var longitude = ""

    fun fetch(uuid: Long) {
        fetchFromDB(uuid)
    }

    private fun fetchFromDB(uuid: Long) {
        launch {
            val data = PikappDatabase(getApplication()).pikappDao().getHomeCategory(uuid)
            categoryRetrieved(data)
        }
    }

    private fun categoryRetrieved(data: ItemHomeCategory) {
        categoryID = data.categoryId.toString()

        Log.d("Debug", "category id: " + categoryID)
        latitude = "-6.234916"
        longitude = "106.634157"
        categoryData.value = data
    }

    fun getMerchant() {
        loading.value = true
        val location = prefHelper.getLatestLocation()
        location.let {
            latitude = it.latitude!!
            longitude = it.longitude!!
        }

        disposable.add(
            pikappService.getMerchant(categoryID, latitude, longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantListResponse>() {
                    override fun onSuccess(t: MerchantListResponse) {
                        t.results?.let { it1 -> merchantRetrieved(it1) }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: MerchantListErrorResponse
                        try {
                            Log.d("Debug", "error category : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<MerchantListErrorResponse>(body, MerchantListErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse = MerchantListErrorResponse(
                                "now", "503", "Unavailable", "Unavailable", "Unavailable"
                            )
                        }

                        merchantFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            "${errorResponse.message} ${errorResponse.path}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("Debug", "error category : ${errorResponse.message} ${errorResponse.path}")
                    }

                }
                ))
    }

    private fun merchantRetrieved(merchantList: List<MerchantList>) {
        merchantResponse.value = merchantList
        merchantLoadError.value = false
        loading.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
