package com.tsab.pikapp.viewmodel.homev2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.models.model.TransactionListV2RespAPI
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MenuViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    val sessionManager = SessionManager(getApplication())
    private val onlineService = OnlineService()

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

    fun getMenuCategoryList(baseContext: Context, activity: Activity, general_error_menu: View) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        CoroutineScope(Dispatchers.IO).launch {
            val menuCateg = object : TypeToken<MerchantListCategoryResponse>() {}.type
            val cancelTransactionListV2Resp: MerchantListCategoryResponse = Gson().fromJson(readJson(baseContext, "menu_category.json"), menuCateg)
            setMenuList(baseContext, general_error_menu, cancelTransactionListV2Resp)
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val response = PikappApiService().api.getMenuCategoryList(getUUID(), timestamp, getClientID(), signature, token, mid)
//            if (response.isSuccessful) {
//                setMenuList(baseContext, general_error_menu, response)
//            } else {
//                setErrorMenuList(general_error_menu, activity)
//            }
//        }
    }

    private suspend fun setMenuList(
        baseContext: Context,
        general_error_menu: View,
        response: MerchantListCategoryResponse
    ) {
        withContext(Dispatchers.Main) {
            setCategoryList(response.results ?: listOf())
            mutableIsLoading.value = false
//            val gson = Gson()
//            val type = object : TypeToken<MerchantListCategoryResponse>() {}.type
//            general_error_menu.isVisible = false
//            if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
//                setCategoryList(response.body()?.results ?: listOf())
//                mutableIsLoading.value = false
//            }  else {
//                var errorResponse: MerchantListCategoryResponse? =
//                    gson.fromJson(response.errorBody()!!.charStream(), type)
//                Toast.makeText(baseContext, "Your account has been logged in to another device", Toast.LENGTH_SHORT).show()
//                mutableErrCode.value = errorResponse?.errCode
//                mutableIsLoading.value = false
//            }
        }
    }

    private suspend fun setErrorMenuList(general_error_menu: View, activity: Activity) {
        withContext(Dispatchers.Main) {
            general_error_menu.isVisible = true
            mutableIsLoading.value = false
            onlineService.serviceDialog(activity)
        }
    }

    private suspend fun setMenuManualTxnList(baseContext: Context, response: Response<MerchantListCategoryResponse>) {
        withContext(Dispatchers.Main) {
            val gson = Gson()
            val type = object : TypeToken<MerchantListCategoryResponse>() {}.type
            if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                setCategoryList(response.body()?.results ?: listOf())
                mutableIsLoading.value = false
            }  else {
                var errorResponse: MerchantListCategoryResponse? =
                    gson.fromJson(response.errorBody()!!.charStream(), type)
                Toast.makeText(baseContext, "Your account has been logged in to another device", Toast.LENGTH_SHORT).show()
                mutableErrCode.value = errorResponse?.errCode
                mutableIsLoading.value = false
            }
        }
    }

    fun getMenuManualTxnList(baseContext: Context) {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        CoroutineScope(Dispatchers.IO).launch {
            val response = PikappApiService().api.getMenuCategoryListManualTxn(getUUID(), timestamp, getClientID(), signature, token, mid)
            if (response.isSuccessful) {
                setMenuManualTxnList(baseContext, response)
            }
        }
    }

    fun restartFragment() {
        mutableSize.value = 0
        mutableIsLoading.value = true
    }

    private fun readJson(context: Context, fileName: String): String? {
        val jsonString: String

        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

    /* REUSABLE DATA */
//    fun getSortMenuCategoryList(baseContext: Context, activity: Activity, general_error_menu: View) {
//        val email = sessionManager.getUserData()!!.email!!
//        val token = sessionManager.getUserToken()!!
//        val timestamp = getTimestamp()
//        val signature = getSignature(email, timestamp)
//        val mid = sessionManager.getUserData()!!.mid!!
//
//        PikappApiService().api.getSortMenuCategoryList(
//            getUUID(), timestamp, getClientID(), signature, token, mid
//        ).enqueue(object : Callback<MerchantListCategoryResponse> {
//            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
//                general_error_menu.isVisible = true
//                mutableIsLoading.value = false
//                onlineService.serviceDialog(activity)
//            }
//
//            override fun onResponse(
//                call: Call<MerchantListCategoryResponse>,
//                response: Response<MerchantListCategoryResponse>
//            ) {
//                val gson = Gson()
//                val type = object : TypeToken<MerchantListCategoryResponse>() {}.type
//                general_error_menu.isVisible = false
//                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
//                    setCategoryList(response.body()?.results ?: listOf())
//                    mutableIsLoading.value = false
//                }  else {
//                    var errorResponse: MerchantListCategoryResponse? =
//                            gson.fromJson(response.errorBody()!!.charStream(), type)
//                    Toast.makeText(baseContext, "Your account has been logged in to another device", Toast.LENGTH_SHORT).show()
//                    mutableErrCode.value = errorResponse?.errCode
//                    mutableIsLoading.value = false
//                }
//            }
//        })
//    }
}
