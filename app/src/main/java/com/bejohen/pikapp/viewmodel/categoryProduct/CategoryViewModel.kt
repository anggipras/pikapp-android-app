package com.bejohen.pikapp.viewmodel.categoryProduct

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.bejohen.pikapp.models.PikappDatabase
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.models.network.PikappApiService
import com.bejohen.pikapp.util.CartUtil
import com.bejohen.pikapp.util.SharedPreferencesUtil
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.categoryProduct.CategoryFragmentDirections
import com.bejohen.pikapp.view.home.HomeFragmentDirections
import com.bejohen.pikapp.viewmodel.BaseViewModel
import com.google.gson.Gson
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
    val categoryData = MutableLiveData<ItemHomeCategory>()

    val locationResponse = MutableLiveData<String>()
    val merchantResponse = MutableLiveData<List<MerchantList>>()
    val merchantLoadError = MutableLiveData<Boolean>()
    val merchantErrorResponse = MutableLiveData<MerchantListErrorResponse>()
    val loading = MutableLiveData<Boolean>()

    var address = ""
    var categoryID = ""
    var latitude = ""
    var longitude = ""

    val cart = MutableLiveData<Boolean>()

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
        merchantResponse.value = merchantList
        merchantLoadError.value = false
        loading.value = false
    }

    private fun merchantFail(response: MerchantListErrorResponse) {
        merchantErrorResponse.value = response
        loading.value = false
    }

    fun getLocation(activity: Activity) {
        val location = prefHelper.getLatestLocation()
        longitude = location.longitude.toString()
        latitude = location.latitude.toString()

        val geocoder = Geocoder(activity as HomeActivity, Locale.getDefault())
        val addresses: List<Address>
        addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1)
        address = addresses[0].getAddressLine(0)
        locationResponse.value = address
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
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
