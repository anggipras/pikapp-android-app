package com.tsab.pikapp.viewmodel.homev2

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.models.model.DummyAdvData
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.models.model.SearchRequest
import com.tsab.pikapp.models.model.SearchResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualAddAdvMenuFragment
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualAdvMenuAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManualTxnViewModel(application: Application) : BaseViewModel(application) {
    private val tag = javaClass.simpleName
    private val sessionManager = SessionManager(getApplication())
    lateinit var manualAdvMenuAdapter: ManualAdvMenuAdapter

    val mutableMenuList = MutableLiveData<List<SearchItem>>(listOf())
    val menuList: LiveData<List<SearchItem>> = mutableMenuList
    fun setMenuList(menuList: List<SearchItem>) {
        mutableMenuList.value = menuList
    }

    val mutableMenuListEmpty = MutableLiveData<List<SearchItem>>(listOf())
    val menuListEmpty: LiveData<List<SearchItem>> = mutableMenuListEmpty

    val mutableSearchEnter = MutableLiveData(false)
    val isSearch: LiveData<Boolean> get() = mutableSearchEnter

    val mutableSearchMenu = MutableLiveData("")
    val MenuSubmit: LiveData<String> get() = mutableSearchMenu

    private var mutableLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = mutableLoading
    fun setLoading(boolean: Boolean) {
        mutableLoading.value = boolean
    }

    private val mutableNote = MutableLiveData("")
    val note: LiveData<String> get() = mutableNote
    fun setManualNote(note: String) {
        mutableNote.value = note
    }

    private val mutableQuantity = MutableLiveData(0)
    val quantity: LiveData<Int> get() = mutableQuantity
    fun setManualQuantity(quantity: String) {
        mutableQuantity.value = quantity.toInt()
    }

    private val mutableTopping = MutableLiveData("")
    val topping: LiveData<String> get() = mutableNote
    fun setManualTopping(topping: String) {
        mutableTopping.value = topping
    }

    fun getMenuList() {
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token, mid, SearchRequest(mutableSearchMenu.value!!, 0, 7)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
            ) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    val amountOfMenus = response.body()!!.total_items
                    if (amountOfMenus != 0) {
                        getSearchList(amountOfMenus)
                    } else {
                        mutableMenuList.value = mutableMenuListEmpty.value
                        Log.e("Zero_Product", "There is no product available")
                    }
                } else {
                    Log.e("FAIL", "Failed get amount of menus")
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e("FAILED", t.message.toString())
            }
        })
    }

    fun getSearchList(amountOfMenus: Int) {
        /*if (menuList.value!!.isNotEmpty()) return*/

        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!

        PikappApiService().api.searchMenu(
                getUUID(), timestamp, getClientID(), signature, token,
                mid, SearchRequest(mutableSearchMenu.value!!, 0, amountOfMenus)
        ).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
            ) {
                val searchResult = response.body()?.results
                setMenuList(searchResult ?: listOf())
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Log.e(tag, "Error: " + t.message.toString())
            }
        })
    }

    fun searchMenu(name: String, status: Boolean){
        mutableSearchMenu.value = name
        mutableSearchEnter.value = status
    }

    fun getManualAdvanceMenuList(baseContext: Context, recyclerview_category: RecyclerView, advMenuChoice: ArrayList<DummyAdvData>, dummyAddChoice: ArrayList<ManualAddAdvMenuFragment.AddAdvDummy>) {
        //ADDING API TO GET ADVANCE MENU LIST
        manualAdvMenuAdapter = ManualAdvMenuAdapter(baseContext, advMenuChoice, dummyAddChoice)
        manualAdvMenuAdapter.notifyDataSetChanged()
        recyclerview_category.adapter = manualAdvMenuAdapter
    }
}