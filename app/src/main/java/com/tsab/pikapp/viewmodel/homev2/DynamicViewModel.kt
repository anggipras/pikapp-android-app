package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.models.model.SearchRequest
import com.tsab.pikapp.models.model.SearchResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DynamicViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    private val sessionManager = SessionManager(getApplication())

    private val mutableMenuList = MutableLiveData<List<SearchItem>>(listOf())
    val menuList: LiveData<List<SearchItem>> = mutableMenuList
    fun setMenuList(menuList: List<SearchItem>) {
        mutableMenuList.value = menuList
    }

    private var mutableLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = mutableLoading
    fun setLoading(boolean: Boolean) {
        mutableLoading.value = boolean
    }

    fun getMenuList() {
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()?.mid

        CoroutineScope(Dispatchers.IO).launch {
            val response = PikappApiService().api.merchantMenu(getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, 7))
            if (response.isSuccessful) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val amountOfMenus = response.body()!!.total_items
                    if (amountOfMenus != 0) {
                        getSearchList(amountOfMenus)
                    } else {
                        Log.e("Zero_Product", "There is no product available")
                    }
                } else {
                    Log.e("FAIL", "Failed get amount of menus")
                }
            }
        }

//        PikappApiService().api.searchMenu(
//            getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, 7)
//        ).enqueue(object : Callback<SearchResponse> {
//            override fun onResponse(
//                call: Call<SearchResponse>,
//                response: Response<SearchResponse>
//            ) {
//                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
//                    val amountOfMenus = response.body()!!.total_items
//                    if (amountOfMenus != 0) {
//                        getSearchList(amountOfMenus)
//                    } else {
//                        Log.e("Zero_Product", "There is no product available")
//                    }
//                } else {
//                    Log.e("FAIL", "Failed get amount of menus")
//                }
//            }
//
//            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
//                Log.e("FAILED", t.message.toString())
//            }
//        })
    }

    private suspend fun getSearchList(amountOfMenus: Int) {
        if (menuList.value!!.isNotEmpty()) return

        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()?.mid

        val response = PikappApiService().api.merchantMenu(getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, amountOfMenus))
        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                val searchResult = response.body()?.results
                setMenuList(searchResult ?: listOf())
            }
        }

//        PikappApiService().api.searchMenu(
//            getUUID(), timestamp, getClientID(), signature, token,
//            mid, SearchRequest("", 0, amountOfMenus)
//        ).enqueue(object : Callback<SearchResponse> {
//            override fun onResponse(
//                call: Call<SearchResponse>,
//                response: Response<SearchResponse>
//            ) {
//                val searchResult = response.body()?.results
//                setMenuList(searchResult ?: listOf())
//            }
//
//            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
//                Log.e(tag, "Error: " + t.message.toString())
//            }
//        })
    }
}