package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.models.model.SearchResponse
import com.tsab.pikapp.util.*
import com.tsab.pikapp.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

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

    fun getMenuList(context: Context) {
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()?.mid

        CoroutineScope(Dispatchers.IO).launch {
            getSearchList(0, context)
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            val response = PikappApiService().api.merchantMenu(getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, 7))
//            if (response.isSuccessful) {
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
//        }

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

    private suspend fun getSearchList(amountOfMenus: Int, context: Context) {
        if (menuList.value!!.isNotEmpty()) return

        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()?.mid

        val menuMain = object : TypeToken<SearchResponse>() {}.type
        val searchMenuRes: SearchResponse = Gson().fromJson(readJson(context, "menu_main.json"), menuMain)

        withContext(Dispatchers.Main) {
            val searchResult = searchMenuRes.results
            setMenuList(searchResult ?: listOf())
        }

//        val response = PikappApiService().api.merchantMenu(getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, amountOfMenus))
//        if (response.isSuccessful) {
//            withContext(Dispatchers.Main) {
//                val searchResult = response.body()?.results
//                setMenuList(searchResult ?: listOf())
//            }
//        }

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
}