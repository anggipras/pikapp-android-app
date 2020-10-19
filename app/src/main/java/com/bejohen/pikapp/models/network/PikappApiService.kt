package com.bejohen.pikapp.models.network

import android.util.Log
import com.bejohen.pikapp.BuildConfig
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.util.*
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PikappApiService {

    private val api = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PikappApi::class.java)

    // AUTH
    fun loginUser(email: String, password: String): Single<LoginResponse> {
        val uuid = getUUID()
        val loginData = LoginRequest(email, password)
        return api.loginUser(uuid, getTimestamp(), getClientID(), loginData)
    }

    fun logoutUser(sessionID: String): Single<LogoutResponse> {
        return api.logoutUser(getUUID(), getTimestamp(), getClientID(), sessionID)
    }

    fun registerUser(email: String, password: String, fullName: String, phoneNumber: String, birthday: String, gender: String): Single<RegisterResponse> {
        val registerData = RegisterRequest(email = email, password = password, fullName = fullName, phoneNumber = phoneNumber, birthday = birthday, gender = gender)
        return api.registerUser(getUUID(), getTimestamp(), getClientID(), registerData)
    }

    // HOME
    fun getHomeBannerSlider(): Single<ItemHomeBannerSliderResponse> {
        return api.getHomeBannerSlider(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun getHomeCategory(): Single<ItemHomeCategoryResponse> {
        return api.getHomeCategory(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun sendRecommendation(email: String, token: String, userExclusiveRequest: UserExclusiveRecommendationRequest): Single<UserExclusiveRecommendationResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postUserExclusive(getUUID(), timestamp, getClientID(), signature, token, userExclusiveRequest)
    }

    fun getMerchant(category: String, latitude: String, longitude: String): Single<MerchantListResponse> {
        return api.getMerchant(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), category, longitude, latitude, "ALL")
    }

    fun getMerchantDetail(mid: String, latitude: String, longitude: String): Single<MerchantDetailResponse> {
        return api.getMerchantDetail(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), mid, longitude, latitude)
    }

    fun getProductList(mid: String): Single<ProductListResponse> {
        return api.getProductList(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), mid)
    }

    fun getProductDetail(pid: String, latitude: String, longitude: String): Single<ProductDetailResponse> {
        return api.getProductDetail(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), pid, longitude, latitude)
    }

    // MERCHANT
    fun getStoreProductList(email: String, token: String, mid: String, available: Boolean): Single<StoreProductListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val status = if (available) "ON" else "OFF"
        return api.getStoreProductList(getUUID(), timestamp, getClientID(), signature, token, mid, status)
    }

    fun getStoreProductDetail(email: String, token: String, pid: String): Single<StoreProductDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getStoreProductDetail(getUUID(), timestamp, getClientID(), signature, token, pid)
    }

    fun postStoreAddProduct(email: String, token: String, mid: String, file01: MultipartBody.Part, file02: MultipartBody.Part,
        file03: MultipartBody.Part, productName: RequestBody, price: RequestBody, condition: RequestBody, status: RequestBody,
        productQty: RequestBody, productDesc: RequestBody): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "ADD")
        return api.postStoreProductAdd(getUUID(), timestamp, getClientID(), signature, token, mid, file01, file02, file03,
            productName, price, condition, status, productQty, productDesc, action)
    }

    fun postStoreOnOffProduct(email: String, token: String, mid: String, pid: String, stts: Boolean): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val status = if (stts) "ON" else "OFF"
        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), status)
        return api.postStoreProductAction(getUUID(), timestamp, getClientID(), signature, token, mid, pid, action)
    }

    fun postStoreDeleteProduct(email: String, token: String, mid: String, pid: String): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "DELETE")
        return api.postStoreProductAction(getUUID(), timestamp, getClientID(), signature, token, mid, pid, action)
    }

    // TRANSACTIONS

    fun postAddToCart(email: String, token: String, addToCartModel: AddToCartModel): Single<AddToCartResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.addToCart(getUUID(), timestamp, getClientID(), signature, token, addToCartModel)
    }

    fun getCartList(email: String, token: String): Single<CartListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getCartList(getUUID(), timestamp, getClientID(), signature, token)
    }

    fun postTransaction(email: String, token: String, transactionModel: TransactionModel): Single<TransactionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postTransaction(getUUID(), timestamp, getClientID(), signature, token, transactionModel)
    }
}