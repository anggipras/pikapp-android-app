package com.bejohen.pikapp.models.network

import android.util.Log
import com.bejohen.pikapp.BuildConfig
import com.bejohen.pikapp.models.model.*
import com.bejohen.pikapp.util.*
import io.reactivex.Single
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

    fun loginUser(email: String, password: String): Single<LoginResponse> {
        val uuid = getUUID()
        Log.d("Debug", "uuid : " + uuid)
        val loginData =
            LoginRequest(email, password)
        return api.loginUser(uuid, getTimestamp(), getClientID(), loginData)
    }

    fun logoutUser(sessionID: String): Single<LogoutResponse> {
        return api.logoutUser(getUUID(), getTimestamp(), getClientID(), sessionID)
    }

    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        birthday: String,
        gender: String
    ): Single<RegisterResponse> {
        val registerData = RegisterRequest(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phoneNumber,
            birthday = birthday,
            gender = gender
        )
        return api.registerUser(getUUID(), getTimestamp(), getClientID(), registerData)
    }

    fun getHomeBannerSlider(): Single<ItemHomeBannerSliderResponse> {
        val uuid = getUUID()
        Log.d("Debug", "uuid : " + uuid)
        return api.getHomeBannerSlider(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun getHomeCategory(): Single<ItemHomeCategoryResponse> {
        return api.getHomeCategory(getUUID(), getTimestamp(), getClientID(), setTokenPublic())
    }

    fun sendRecommendation(
        email: String,
        token: String,
        userExclusiveRequest: UserExclusiveRecommendationRequest
    ): Single<UserExclusiveRecommendationResponse> {
        val timestamp = getTimestamp()
        val signature = getSignature(email, timestamp)
        Log.d("Debug", "signature : " + signature)
        return api.postUserExclusive(
            getUUID(),
            timestamp,
            getClientID(),
            signature,
            token,
            userExclusiveRequest
        )
    }

    fun getMerchant(
        category: String,
        latitude: String,
        longitude: String
    ): Single<MerchantListResponse> {
        return api.getMerchant(
            getUUID(),
            getTimestamp(),
            getClientID(),
            setTokenPublic(),
            category,
            longitude,
            latitude,
            "ALL"
        )
    }

    fun getMerchantDetail(
        mid: String,
        latitude: String,
        longitude: String
    ): Single<MerchantDetailResponse> {
        return api.getMerchantDetail(
            getUUID(),
            getTimestamp(),
            getClientID(),
            setTokenPublic(),
            mid,
            longitude,
            latitude
        )
    }

    fun getProductList(mid: String): Single<ProductListResponse> {
        return api.getProductList(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), mid)
    }

    fun getProductDetail(
        pid: String, latitude: String,
        longitude: String
    ): Single<ProductDetailResponse> {
        return api.getProductDetail(getUUID(), getTimestamp(), getClientID(), setTokenPublic(), pid, longitude, latitude)
    }
}