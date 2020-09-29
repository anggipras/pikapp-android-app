package com.bejohen.pikapp.viewmodel.store

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.MerchantListErrorResponse
import com.bejohen.pikapp.models.model.StoreProductList
import com.bejohen.pikapp.viewmodel.BaseViewModel

class StoreProductListViewModel(application: Application) : BaseViewModel(application) {

    val storeProductListResponse = MutableLiveData<List<StoreProductList>>()
    val loadingMerchantDetail = MutableLiveData<Boolean>()
    val productErrorResponse = MutableLiveData<MerchantListErrorResponse>()

    fun getProductList() {
        val storeList: List<StoreProductList> = listOf(
            StoreProductList("", "", "", "", "", 200, true, false, "", "", "", "", 1),
            StoreProductList("", "", "", "", "", 200, true, false, "", "", "", "", 1)
        )
        storeProductListResponse.value = storeList
    }
}