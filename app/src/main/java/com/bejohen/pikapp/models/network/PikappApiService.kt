package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.LoginRequest
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.RegisterRequest
import com.bejohen.pikapp.models.RegisterResponse
import com.bejohen.pikapp.util.BASE_URL
import com.bejohen.pikapp.util.getTime
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
        val loginData = LoginRequest(email, password)
        return api.loginUser(getTime(), loginData)
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
        return api.registerUser(getTime(), registerData)
    }
}