package com.tsab.pikapp.viewmodel.categoryMenu

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.menuCategory.MenuCategoryAdapter
import com.tsab.pikapp.view.menuCategory.SortCategoryAdapter
import com.tsab.pikapp.viewmodel.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(application: Application) : BaseViewModel(application) {
    val gson = Gson()
    val type = object : TypeToken<BaseResponse>() {}.type
    var activation: Boolean = true

    val dataList: MutableList<CategoryListResult> = mutableListOf()
    lateinit var menuCategoryAdapter: MenuCategoryAdapter
    lateinit var sortCategoryAdapter: SortCategoryAdapter
    lateinit var linearLayoutManager: LinearLayoutManager

    var size: String = "0"

    val mutableNamaCategory = MutableLiveData("")
    val mutableNamaCategoryError = MutableLiveData("")
    val isNamaCategoryValid = MutableLiveData(false)
    val namaCatagory: LiveData<String> get() = mutableNamaCategory
    val namaCategoryError: LiveData<String> get() = mutableNamaCategoryError

    private val mutableCategoryOrder = MutableLiveData("")
    val categoryOrder: LiveData<String> get() = mutableCategoryOrder

    private val mutableActivationToggle = MutableLiveData("")
    val activationToggle: LiveData<String> get() = mutableActivationToggle

    private val mutableCategoryId = MutableLiveData("")
    val categoryId: LiveData<String> get() = mutableCategoryId

    fun getMenuCategoryListSort(
        baseContext: Context,
        recyclerview_category: RecyclerView,
        listener: SortCategoryAdapter.OnItemClickListener
    ) {
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

                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                sortCategoryAdapter = SortCategoryAdapter(
                    baseContext,
                    categoryResult as MutableList<CategoryListResult>,
                    listener
                )
                sortCategoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = sortCategoryAdapter

            }

        })
    }

    fun getMenuCategoryList(
        baseContext: Context,
        recyclerview_category: RecyclerView,
        listener: MenuCategoryAdapter.OnItemClickListener
    ) {
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

                Log.e("size", categoryResponse?.results?.size.toString())
                size = categoryResponse?.results?.size.toString()
                Log.e("size on response", size)

                menuCategoryAdapter = MenuCategoryAdapter(
                    baseContext,
                    categoryResult as MutableList<CategoryListResult>,
                    listener
                )
                menuCategoryAdapter.notifyDataSetChanged()
                recyclerview_category.adapter = menuCategoryAdapter

            }

        })
    }

    fun postCategory(categoryName: String, baseContext: Context) {
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        var categoryReq = MenuCategoryRequest()
        categoryReq.category_name = categoryName
        categoryReq.category_order = size.toInt() + 1
        categoryReq.activation = activation

        PikappApiService().api.menuCategory(
            getUUID(), timestamp, getClientID(), signature, token, mid, categoryReq
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "added", Toast.LENGTH_SHORT).show()
                } else {
                    var errorResponse: BaseResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(
                        baseContext,
                        generateResponseMessage(
                            errorResponse?.errCode,
                            errorResponse?.errMessage
                        ).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun updateCategory(categoryName: String, baseContext: Context) {
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        var categoryReq = UpdateMenuCategoryRequest()
        categoryReq.category_name = categoryName
        categoryReq.category_order = categoryOrder.value?.toInt()
        categoryReq.activation = activation
        categoryReq.id = categoryId.value?.toLong()

        Log.e("id", categoryId.value.toString())

        PikappApiService().api.updateMenuCategory(
            getUUID(), timestamp, getClientID(), signature, token, mid, categoryReq
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "changed", Toast.LENGTH_SHORT).show()
                } else {
                    var errorResponse: BaseResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(
                        baseContext,
                        generateResponseMessage(
                            errorResponse?.errCode,
                            errorResponse?.errMessage
                        ).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun deleteCategoryPopup(categoryName: String, baseContext: Context) {
        var sessionManager = SessionManager(getApplication())
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val mid = sessionManager.getUserData()!!.mid!!
        val id = categoryId.value.toString()

        Log.e("timestamp", timestamp)
        Log.e("signature", signature)
        Log.e("token", token)
        Log.e("mid", mid)
        Log.e("id", id)

        PikappApiService().api.deleteMenuCategory(
            getUUID(), timestamp, getClientID(), signature, token, mid, id
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "deleted", Toast.LENGTH_SHORT).show()
                } else {
                    var errorResponse: BaseResponse? =
                        gson.fromJson(response.errorBody()!!.charStream(), type)
                    Toast.makeText(
                        baseContext,
                        generateResponseMessage(
                            errorResponse?.errCode,
                            errorResponse?.errMessage
                        ).toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun delete() {

    }

    fun validateNama(categoryName: String): Boolean {
        if (categoryName.isEmpty() || categoryName.isBlank()) {
            mutableNamaCategoryError.value = "Nama Kategori Menu tidak boleh kosong"
        } else if (categoryName.length > 30) {
            mutableNamaCategoryError.value = "Nama Kategori Menu tidak boleh melebihi 30 karakter"
        } else {
            mutableNamaCategoryError.value = ""
        }

        mutableNamaCategory.value = categoryName
        isNamaCategoryValid.value = mutableNamaCategoryError.value!!.isEmpty()
        return isNamaCategoryValid.value!!
    }

    fun getCategoryName(categoryName: String) {
        mutableNamaCategory.value = categoryName
    }

    fun getCategoryOrder(categoryOrder: String) {
        mutableCategoryOrder.value = categoryOrder
    }

    fun getCategoryActivation(activation: String) {
        mutableActivationToggle.value = activation
    }

    fun getCategoryId(id: String): String {
        mutableCategoryId.value = id
        return mutableCategoryId.value.toString()
    }

}