package com.tsab.pikapp.viewmodel.categoryMenu

import android.app.Application
import android.content.Context
import android.content.Intent
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response

class CategoryViewModel(application: Application) : BaseViewModel(application) {
    private val TAG = javaClass.simpleName

    private var sessionManager = SessionManager(getApplication())

    private val apiService = PikappApiService()
    private val disposable = CompositeDisposable()

    private val mutableIsLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> = mutableIsLoading
    fun setLoading(isLoading: Boolean) {
        mutableIsLoading.value = isLoading
    }

    private val _isLoadingIcon = MutableLiveData<Boolean>()
    val isLoadingIcon: LiveData<Boolean> = _isLoadingIcon

    private val mutableCategoryList = MutableLiveData<List<MenuCategory>>(listOf())
    val categoryList: LiveData<List<MenuCategory>> = mutableCategoryList
    fun setCategoryList(categoryList: List<MenuCategory>) {
        mutableCategoryList.value = categoryList
    }

    private val _isLoadingFinish = MutableLiveData<Boolean>()
    val isLoadingFinish: LiveData<Boolean> = _isLoadingFinish
    fun setLoadingFinish(isLoading: Boolean) {
        _isLoadingFinish.value = isLoading
    }

    fun fetchCategoryList() {
        setLoading(true)
        disposable.add(
            apiService.listMenuCategory(
                email = sessionManager.getUserData()?.email ?: "",
                token = sessionManager.getUserToken() ?: "",
                merchantId = sessionManager.getUserData()?.mid ?: ""
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ListMenuCategoryResponse>() {
                    override fun onSuccess(response: ListMenuCategoryResponse) {
                        Log.d(TAG, response.results.toString())

                        setCategoryList(response.results)
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }

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

    private val mutableCategoryMenuSize = MutableLiveData("")
    val categoryMenuSize: LiveData<String> get() = mutableCategoryMenuSize

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

        disposable.add(
            apiService.listMenuCategory(
                email = sessionManager.getUserData()?.email ?: "",
                token = sessionManager.getUserToken() ?: "",
                merchantId = sessionManager.getUserData()?.mid ?: ""
            ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ListMenuCategoryResponse>() {
                    override fun onSuccess(response: ListMenuCategoryResponse) {
                        Log.d(TAG, response.results.toString())

                        // TODO: Update sort functionality.
                        setLoading(false)
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, e.message.toString())
                        setLoading(false)
                    }
                })
        )
    }

    fun postCategory(categoryName: String, baseContext: Context) {
        _isLoadingIcon.value = true
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
                    Toast.makeText(baseContext, "Kategori berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    _isLoadingIcon.value = false
                    setLoading(false)
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
                    _isLoadingIcon.value = false
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
                    Toast.makeText(baseContext, "Perubahan berhasil tersimpan", Toast.LENGTH_SHORT).show()
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

        PikappApiService().api.deleteMenuCategory(
            getUUID(), timestamp, getClientID(), signature, token, mid, id
        ).enqueue(object : retrofit2.Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                Toast.makeText(baseContext, "failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.code() == 200 && response.body()!!.errCode.toString() == "EC0000") {
                    Toast.makeText(baseContext, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    setLoadingFinish(false)
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
                    setLoadingFinish(false)
                }
            }
        })
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

    fun getCategoryMenuSize(size: String): String {
        mutableCategoryMenuSize.value = size
        return mutableCategoryMenuSize.value.toString()
    }
}
