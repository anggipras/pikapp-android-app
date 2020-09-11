package com.bejohen.pikapp.viewmodel.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.*
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

class HomeHomeViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var refreshTime = 60 * 60 * 1000 * 1000 * 1000L
    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    val homeBannerSlider = MutableLiveData<List<ItemHomeBannerSlider>>()
    val homeBannerSliderLoadError = MutableLiveData<Boolean>()
    val homeBannerSliderLoading = MutableLiveData<Boolean>()
    val homeBannerSliderErrorResponse = MutableLiveData<ErrorResponse>()

    val homeCategory = MutableLiveData<List<ItemHomeCategory>>()
    val homeCategoryLoadError = MutableLiveData<Boolean>()
    val homeCategoryLoading = MutableLiveData<Boolean>()
    val homeCategoyErrorResponse = MutableLiveData<ErrorResponse>()

    fun refresh() {
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchBannerSliderFromDatabase()
            fetchCategoryFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {

        homeCategoryLoading.value = true
        disposable.add(
            pikappService.getHomeBannerSlider()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ItemHomeBannerSliderResponse>() {
                    override fun onSuccess(t: ItemHomeBannerSliderResponse) {
                        storeHomeBannerSliderLocally(t.results!!)
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            Log.d("Debug","error slider : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<ErrorResponse>(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse =
                                ErrorResponse(
                                    "503",
                                    "Service Unavailable"
                                )
                        }

                        itemHomeBannerSliderFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            errorResponse.errMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
        )
        disposable.add(
            pikappService.getHomeCategory()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ItemHomeCategoryResponse>() {
                    override fun onSuccess(t: ItemHomeCategoryResponse) {
                        storeHomeCategoryLocally(t.results!!)
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        try {
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<ErrorResponse>(body, ErrorResponse::class.java)
                        } catch (err: Throwable) {
                            errorResponse =
                                ErrorResponse(
                                    "503",
                                    "Service Unavailable"
                                )
                        }

                        itemHomeCategoryFail(errorResponse)
                        Toast.makeText(
                            getApplication(),
                            errorResponse.errMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        )
    }

    //home slider banner

    private fun storeHomeBannerSliderLocally(list: List<ItemHomeBannerSlider>) {
        launch {
            val dao = PikappDatabase(getApplication()).pikappDao()
            dao.deleteAllHomeBannerSlider()
            val result = dao.insertAllHomeBannerSlider(*list.toTypedArray())
            var i = 0
            while (i < list.size) {
                list[i].uuid = result[i].toInt()
                ++i
            }
            itemHomeBannerSliderRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    private fun fetchBannerSliderFromDatabase() {
        homeBannerSliderLoading.value = true
        launch {
            val itemHomeBannerSlider =
                PikappDatabase(getApplication()).pikappDao().getAllHomeBannerSlider()
            itemHomeBannerSliderRetrieved(itemHomeBannerSlider)
//            Toast.makeText(getApplication(), "dari database jing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun itemHomeBannerSliderRetrieved(itemList: List<ItemHomeBannerSlider>) {
        homeBannerSlider.value = itemList
        homeBannerSliderLoadError.value = false
        homeBannerSliderLoading.value = false
    }

    private fun itemHomeBannerSliderFail(response: ErrorResponse) {
        homeBannerSliderErrorResponse.value = response
        homeBannerSliderLoading.value = false
    }

    // home category

    private fun storeHomeCategoryLocally(list: List<ItemHomeCategory>) {
        launch {
            val dao = PikappDatabase(getApplication()).pikappDao()
            dao.deleteAllHomeCategory()
            val result = dao.insertAllHomeCategory(*list.toTypedArray())
            var i = 0
            while (i < list.size) {
                list[i].uuid = result[i].toInt()
                ++i
            }
            itemHomeCategoryRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    private fun itemHomeCategoryRetrieved(itemList: List<ItemHomeCategory>) {
        homeCategory.value = itemList
        homeCategoryLoadError.value = false
        homeCategoryLoading.value = false
    }

    private fun fetchCategoryFromDatabase() {
        homeCategoryLoading.value = true
        launch {
            val itemHomeCategory = PikappDatabase(getApplication()).pikappDao().getAllHomeCategory()
            itemHomeCategoryRetrieved(itemHomeCategory)
            Toast.makeText(getApplication(), "dari database jing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun itemHomeCategoryFail(response: ErrorResponse) {
        homeCategoyErrorResponse.value = response
        homeCategoryLoading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}