package com.tsab.pikapp.viewmodel.categoryProduct

import android.app.Activity
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.tsab.pikapp.models.PikappDatabase
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.CartUtil
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.categoryProduct.CategoryFragmentDirections
import com.tsab.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
import com.tsab.pikapp.util.LocationLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.util.*

class CategoryViewModel(application: Application) : BaseViewModel(application) {

    private val pikappService = PikappApiService()
    private val disposable = CompositeDisposable()

    private var prefHelper = SharedPreferencesUtil(getApplication())
    private var cartUtil = CartUtil(getApplication())
    private val locationData = LocationLiveData(application)

    val categoryData = MutableLiveData<ItemHomeCategory>()
    val locationResponse = MutableLiveData<String>()
    val merchantResponse = MutableLiveData<List<MerchantList>>()
    val merchantLoadError = MutableLiveData<Boolean>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    val isLocation = MutableLiveData<Boolean>()
    fun getLocationData() = locationData

    var address = ""
    var categoryID = ""
    var latitude = ""
    var longitude = ""


    val cart = MutableLiveData<Boolean>()

    fun getLocation(activity: Activity) {
        loading.value = true
        val location = prefHelper.getLatestLocation()

        longitude = location.longitude.toString()
        latitude = location.latitude.toString()

        isLocation.value = !longitude.isEmpty()
    }

    fun getAddress(activity: Activity) {
        val geocoder = Geocoder(activity as HomeActivity, Locale.getDefault())
        val addresses: List<Address>
        addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        address = addresses[0].getAddressLine(0)
        locationResponse.value = address
    }

    fun getAddress(activity: Activity, lati: String, longi: String) {
        prefHelper.saveLatestLocation(longi, lati)
        val geocoder = Geocoder(activity as HomeActivity, Locale.getDefault())
        val addresses: List<Address>
        addresses = geocoder.getFromLocation(lati.toDouble(), longi.toDouble(), 1)
        address = addresses[0].getAddressLine(0)
        locationResponse.value = address
    }



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
        categoryData.value = data
    }

    fun getMerchant() {
        loading.value = true
        val location = prefHelper.getLatestLocation()
        location.latitude?.let {
            latitude = it
        }

        location.longitude?.let {
            longitude = it
        }

        disposable.add(
            pikappService.getMerchant(categoryID, latitude, longitude)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<MerchantListResponse>() {
                    override fun onSuccess(t: MerchantListResponse) {
                        t.results?.let { it1 ->
                            merchantRetrieved(it1)
                        }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: MerchantListErrorResponse
                        try {
                            Log.d("Debug", "error category : " + e)
                            val responseBody = (e as HttpException)
                            val body = responseBody.response()?.errorBody()?.string()
                            errorResponse =
                                Gson().fromJson<MerchantListErrorResponse>(
                                    body,
                                    MerchantListErrorResponse::class.java
                                )
                        } catch (err: Throwable) {
                            errorResponse = MerchantListErrorResponse(
                                "now", "503", "Unavailable", "Unavailable", "Unavailable"
                            )
                        }

                        merchantFail(errorResponse)
//                        createToastShort(getApplication(), "${errorResponse.message} ${errorResponse.path}")
                        Log.d(
                            "Debug",
                            "error category : ${errorResponse.message} ${errorResponse.path}"
                        )
                    }

                }
                ))
    }

    private fun merchantRetrieved(merchantList: List<MerchantList>) {
        val deeplink = prefHelper.getDeeplink()
        if(deeplink.address != "") {
            filterMerchant(merchantList)
        } else {
            merchantResponse.value = merchantList
            merchantLoadError.value = false
            loading.value = false
        }
    }

    private fun filterMerchant(merchantList: List<MerchantList>) {
        val deeplink = prefHelper.getDeeplink()
        var filteredMerchant = arrayListOf<MerchantList>()
        for(merchant in merchantList) {
            if(merchant.merchantAddress!!.contains(deeplink.address!!)) {
                filteredMerchant.add(merchant)
            }
        }
        merchantResponse.value = filteredMerchant
        merchantLoadError.value = false
        loading.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
        loading.value = false
    }



    fun goToMerchantDetail(mid: String, view: View) {
        val action = CategoryFragmentDirections.actionToMerchantDetailFragment(mid)
        Navigation.findNavController(view).navigate(action)
    }

    fun goToProductDetail(pid: String, view: View) {
        val action = CategoryFragmentDirections.actionFromCategoryFragmentToProductDetailFragment(pid)
        Navigation.findNavController(view).navigate(action)
    }

    fun getCart() {
        cart.value = cartUtil.getCartStatus()
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
