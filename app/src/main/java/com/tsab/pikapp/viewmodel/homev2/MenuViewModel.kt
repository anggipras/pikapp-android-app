package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel(application: Application) : BaseViewModel(application) {
    val sessionManager = SessionManager(getApplication())

    var activation: Boolean = true

    private val mutableSize = MutableLiveData(0)
    val size: LiveData<Int> get() = mutableSize

    private val mutableCategoryList = MutableLiveData<List<CategoryListResult>>(listOf())
    val categoryListResult: LiveData<List<CategoryListResult>> = mutableCategoryList
    fun setCategoryList(categoryListResult: List<CategoryListResult>) {
        mutableCategoryList.value = categoryListResult
    }

    private val mutableIsLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = mutableIsLoading

    private val mutableCategoryName = MutableLiveData(" ")
    val categoryName: LiveData<String> get() = mutableCategoryName

    fun getMenuCategoryList() {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.d("failed", t.message.toString())
            }

            override fun onResponse(
                call: Call<MerchantListCategoryResponse>,
                response: Response<MerchantListCategoryResponse>
            ) {
                val categoryResponse = response.body()
                val categoryResult = response.body()?.results

                Log.e("result", categoryResponse?.results.toString())
                Log.e("Response raw", response.raw().toString())
                Log.e("response body", response.body().toString())
                Log.d("SUCCEED", "succeed")
                Log.e("uuid", getUUID())
                Log.e("timestamp", timestamp)
                Log.e("client id", getClientID())
                Log.e("signature", signature)
                Log.e("token", token)
                Log.e("mid", mid)

                mutableSize.value = categoryResponse?.results?.size
                mutableIsLoading.value = false

                setCategoryList(categoryResult ?: listOf())
                mutableIsLoading.value = false
            }
        })
    }

    fun restartFragment() {
        mutableSize.value = 0
        mutableIsLoading.value = true
    }
}
