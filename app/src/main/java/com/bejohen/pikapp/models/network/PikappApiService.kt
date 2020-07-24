package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.LoginRequest
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.RegisterRequest
import com.bejohen.pikapp.models.RegisterResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PikappApiService {
    private val BASE_URL = "http://10.0.2.2:3005/v1/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PikappApi::class.java)

    fun loginUser(email: String, password: String): Single<LoginResponse> {
        val loginData = LoginRequest(email, password)
        return api.loginUser(loginData)
    }

    fun registerUser(email: String, password: String): Single<RegisterResponse> {
        val registerData = RegisterRequest(email, password)
        return api.registerUser(registerData)
    }
}