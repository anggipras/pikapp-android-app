package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.BaseResponse
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.models.model.SearchRequest
import com.tsab.pikapp.models.model.SearchResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.homev2.menu.SearchAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(application: Application) : BaseViewModel(application) {

    lateinit var searchAdapter: SearchAdapter

    private val mutableErrCode = MutableLiveData("")
    val errCode: LiveData<String> get() = mutableErrCode

    private val mutableIsLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    fun getSearchList(
        menu: String, baseContext: Context,
        recyclerview_category: RecyclerView, noFound: ImageView, noFoundText: TextView
    ) {
        setLoading(true)
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.searchMenu(
            getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest(menu, 0, 30)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                val gson = Gson()
                val type = object : TypeToken<SearchResponse>() {}.type
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val searchResult = response.body()?.results
                    val categoryList = ArrayList<String>()
                    Log.e("Total", response.body()!!.total_items.toString())
                    if (response.body()?.total_items == 0) {
                        noFound.isVisible = true
                        noFoundText.isVisible = true
                        recyclerview_category.isVisible = false
                    } else {
                        noFound.isVisible = false
                        noFoundText.isVisible = false
                        recyclerview_category.isVisible = true
                    }
                    if (searchResult != null) {
                        for (category in searchResult) {
                            if (categoryList.isEmpty()) {
                                categoryList.add(category.merchant_category_name.toString())
                            } else if (!categoryList.contains(category.merchant_category_name.toString())) {
                                categoryList.add(category.merchant_category_name.toString())
                            }
                        }
                    }
                    searchAdapter = SearchAdapter(
                        baseContext,
                        searchResult as MutableList<SearchItem>,
                        categoryList
                    )
                    searchAdapter.notifyDataSetChanged()
                    recyclerview_category.adapter = searchAdapter
                    setLoading(false)
                }  else {
                    var errorResponse: SearchResponse? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                    Log.e("err code", errorResponse?.errCode)
                    Toast.makeText(baseContext, "Your account has been logged in to another device", Toast.LENGTH_SHORT).show()
                    Log.e("error", "logged out")
                    mutableErrCode.value = errorResponse?.errCode
                    setLoading(false)
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
                setLoading(false)
            }
        })
    }

}