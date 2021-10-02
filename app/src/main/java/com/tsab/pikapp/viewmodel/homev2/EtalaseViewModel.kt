package com.tsab.pikapp.viewmodel.homev2

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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response

class EtalaseViewModel(application: Application) : BaseViewModel(application) {
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

    fun validateNama(categoryName: String): Boolean {
        if (categoryName.isEmpty() || categoryName.isBlank()) {
            mutableNamaCategoryError.value = "Nama Etalase Menu tidak boleh kosong"
        } else if (categoryName.length > 30) {
            mutableNamaCategoryError.value = "Nama Etalase Menu tidak boleh melebihi 30 karakter"
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
