package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.categoryMenu.CategoryAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuViewModel (application: Application) : BaseViewModel(application) {

    var activation: Boolean = true

    lateinit var categoryAdapter: CategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    var size: String = "0"

    fun getMenuCategoryList(baseContext: Context, recyclerview_category: RecyclerView){
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.getMenuCategoryList(
            getUUID(), timestamp, getClientID(), signature, token, mid
        ).enqueue(object : Callback<MerchantListCategoryResponse> {
            override fun onFailure(call: Call<MerchantListCategoryResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
            }

            override fun onResponse(call: Call<MerchantListCategoryResponse>, response: Response<MerchantListCategoryResponse>) {

                val categoryResponse = response.body()
                val categoryResult = response.body()?.results
                Log.e("result", categoryResponse?.results.toString())
                Log.e("Response raw", response.raw().toString())
                Log.e("response body", response.body().toString())
                Log.d("SUCCEED", "succeed")

                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                categoryAdapter = CategoryAdapter(baseContext, categoryResult as MutableList<CategoryListResult>)
                categoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = categoryAdapter

            }

        })
    }



}