package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.models.model.SearchRequest
import com.tsab.pikapp.models.model.SearchResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.menu.DynamicListAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DynamicViewModel (application: Application) : BaseViewModel(application) {

    lateinit var dynamicAdapter: DynamicListAdapter
    private var mutableLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = mutableLoading
    fun setLoading(boolean: Boolean) {
        mutableLoading.value = boolean
    }

    fun getAmountOfMenu(baseContext: Context,
                      recyclerview_category: RecyclerView, noFound: ImageView, noFoundText: TextView, categoryName:String, noFoundButton: Button, foundButton: Button, listener: DynamicListAdapter.OnItemClickListener){
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, 7)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val amountOfMenus = response.body()!!.total_items
                    getSearchList(baseContext, recyclerview_category, noFound, noFoundText, categoryName, noFoundButton, foundButton, amountOfMenus, listener)
                } else {
                    Log.e("FAIL", "Failed get amount of menus")
                }
            }
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("FAILED", t.message.toString())
            }
        })
    }

    fun getSearchList(baseContext: Context,
                      recyclerview_category: RecyclerView, noFound: ImageView, noFoundText: TextView, categoryName:String, noFoundButton: Button, foundButton: Button, amountOfMenus: Int, listener: DynamicListAdapter.OnItemClickListener){
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        var jumlah: Int

        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest("", 0, amountOfMenus)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    setLoading(false)
                    val searchResult = response.body()?.results
                    val categoryList = ArrayList<SearchList>()
                    if (searchResult != null) {
                        for(category in searchResult){
                            if(category.merchant_category_name == categoryName){
                                categoryList.add(category)
                            }
                        }
                        jumlah = categoryList.size
                        if(jumlah == 0){
                            noFound.isVisible = true
                            noFoundText.isVisible = true
                            noFoundButton.isVisible = true
                            foundButton.isVisible = false
                            recyclerview_category.isVisible = false
                        }else{
                            noFound.isVisible = false
                            noFoundText.isVisible = false
                            noFoundButton.isVisible = false
                            foundButton.isVisible = true
                            recyclerview_category.isVisible = true
                        }
                    }
                    dynamicAdapter = DynamicListAdapter(baseContext, categoryList as MutableList<SearchList>, listener)
                    dynamicAdapter.notifyDataSetChanged()
                    recyclerview_category.adapter = dynamicAdapter
                } else {
                    Log.e("FAIL", "Fail")
                }
            }
            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("failed", t.message.toString())
                setLoading(false)
            }
        })
    }

}