package com.bejohen.pikapp.models.network

import android.util.Log
import com.bejohen.pikapp.models.*
import com.bejohen.pikapp.util.*
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PikappApiService {

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PikappApi::class.java)

    fun loginUser(email: String, password: String): Single<LoginResponse> {
        val uuid = getUUID()
        Log.d("Debug","uuid : " + uuid)
        val loginData = LoginRequest(email, password)
        return api.loginUser(uuid, getTime(), getClientID(), loginData)
    }

    fun registerUser(email: String, password: String, fullName: String, phoneNumber: String, birthday: String, gender: String): Single<RegisterResponse> {
        val registerData = RegisterRequest(
            email,
            password,
            fullName,
            phoneNumber,
            birthday,
            gender
        )
        return api.registerUser(getUUID(), getTime(),getClientID(),registerData)
    }

    fun getHomeBannerSlider(): Single<ItemHomeBannerSliderResponse> {
        val uuid = getUUID()
        Log.d("Debug","uuid : " + uuid)
        return api.getHomeBannerSlider(getUUID(), getTime(), getClientID(), setTokenPublic())
    }

    fun getHomeCategory(): Single<ItemHomeCategoryResponse> {
        val uuid = getUUID()
        Log.d("Debug","uuid : " + uuid)
        return api.getHomeCategory(getUUID(), getTime(), getClientID(), setTokenPublic())
    }
}