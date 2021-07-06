package com.tsab.pikapp.models.network

import android.util.Log
import com.tsab.pikapp.BuildConfig
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.util.*
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
    fun loginUser(email: String, password: String, fcmToken: String): Single<LoginResponse> {
        val uuid = getUUID()
        val loginData = LoginRequest(email, password, fcmToken)
        Log.d("Debug", "fcm token : $fcmToken")
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
        Log.d("Debug", "timestamp getStoreProductList : $timestamp")
        Log.d("Debug", "signature getStoreProductList : $signature")
        return api.getStoreProductList(getUUID(), timestamp, getClientID(), signature, token, mid, status)
    }

    fun getStoreProductDetail(email: String, token: String, pid: String): Single<StoreProductDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp getStoreProductDetail : $timestamp")
        Log.d("Debug", "signature getStoreProductDetail : $signature")
        return api.getStoreProductDetail(getUUID(), timestamp, getClientID(), signature, token, pid)
    }

    fun postStoreAddProduct(email: String, token: String, mid: String, file01: MultipartBody.Part, file02: MultipartBody.Part,
        file03: MultipartBody.Part, productName: RequestBody, price: RequestBody, condition: RequestBody, status: RequestBody,
        productQty: RequestBody, productDesc: RequestBody): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "ADD")
        return api.postStoreProduct(getUUID(), timestamp, getClientID(), signature, token, mid, file01, file02, file03,
            productName, price, condition, status, productQty, productDesc, action)
    }

    fun postStoreEditProductWithImage(email: String, token: String, mid: String, pid: String, file01: MultipartBody.Part?, file02: MultipartBody.Part?,
                            file03: MultipartBody.Part?, productName: RequestBody, price: RequestBody, condition: RequestBody,
                                      productQty: RequestBody, productDesc: RequestBody): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "MODIFY")
        return api.postStoreEditProduct(getUUID(), timestamp, getClientID(), signature, token, mid, pid, file01, file02, file03,
            productName, price, condition, productQty, productDesc, action)
    }

    fun postStoreEditProductWithoutImage(email: String, token: String, mid: String, pid: String, productName: RequestBody, price: RequestBody, condition: RequestBody,
                                         productQty: RequestBody, productDesc: RequestBody): Single<StoreProductActionResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)

        val action: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "MODIFY")
        return api.postStoreProductWithoutImage(getUUID(), timestamp, getClientID(), signature, token, mid, pid,
            productName, price, condition, productQty, productDesc, action)
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

    fun getTransactionList(email: String, token: String): Single<GetOrderListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnList : $timestamp")
        Log.d("Debug", "signature txnList : $signature")
        return api.getTransactionList(getUUID(), timestamp, getClientID(), signature, token)
    }

    fun getTransactionDetail(email: String, token: String, transactionID: String) : Single<GetOrderDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnDetail : $timestamp")
        Log.d("Debug", "signature txnDetail : $signature")
        return api.getTransactionDetail(getUUID(), timestamp, getClientID(), signature, token, transactionID)
    }

    fun getTransactionListMerchant(email: String, token: String, mid: String): Single<GetStoreOrderListResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.getTransactionListMerchant(getUUID(), timestamp, getClientID(), signature, token, mid)
    }

    fun getTransactionDetailMerchant(email: String, token: String, transactionID: String, tableNo: String) : Single<GetStoreOrderDetailResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "timestamp txnDetail merchant : $timestamp")
        Log.d("Debug", "signature txnDetail merchant : $signature")
        return api.getTransactionDetailMerchant(getUUID(), timestamp, getClientID(), signature, token, transactionID, tableNo)
    }

    fun postUpdateOrderStatus(email: String, token: String, transactionID : RequestBody, status : RequestBody): Single<UpdateStatusResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        return api.postUpdateTransactionStatus(getUUID(), timestamp, getClientID(), signature, token, transactionID, status)
    }


}