package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    val sessionManager = SessionManager(getApplication())

    val mutableIsLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = mutableIsLoading

    private val mutableSize = MutableLiveData(0)
    val size: LiveData<Int> get() = mutableSize

    val mutableMenuTabs = MutableLiveData(0)
    val menuTabs: LiveData<Int> get() = mutableMenuTabs

    val mutableManualTransAct = MutableLiveData(0)
    val manualTransAct: LiveData<Int> get() = mutableManualTransAct

    private val mutableCategoryList = MutableLiveData<List<CategoryListResult>>(listOf())
    val categoryListResult: LiveData<List<CategoryListResult>> = mutableCategoryList
    fun setCategoryList(categoryListResult: List<CategoryListResult>) {
        mutableCategoryList.value = categoryListResult
        mutableSize.value = categoryListResult.size
    }

    private val mutableCategoryName = MutableLiveData(" ")
    val categoryName: LiveData<String> get() = mutableCategoryName

    private val mutableErrCode = MutableLiveData("")
    val errCode: LiveData<String> get() = mutableErrCode

    fun getMenuCategoryList(baseContext: Context) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {}

            override fun onResponse(
                call: Call<MerchantListCategoryResponse>,
                response: Response<MerchantListCategoryResponse>
            ) {
                val gson = Gson()
                val type = object : TypeToken<MerchantListCategoryResponse>() {}.type
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    setCategoryList(response.body()?.results ?: listOf())
                    mutableIsLoading.value = false
                }  else {
                    var errorResponse: MerchantListCategoryResponse? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(baseContext, "Your account has been logged in to another device", Toast.LENGTH_SHORT).show()
                    Log.e("error", "logged out")
                    mutableErrCode.value = errorResponse?.errCode
                    mutableIsLoading.value = false
                }
            }
        })
    }

    fun restartFragment() {
        mutableSize.value = 0
        mutableIsLoading.value = true
    }
}
