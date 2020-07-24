package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.LoginRequest
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.RegisterRequest
import com.bejohen.pikapp.models.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface PikappApi {
    @POST("account/login")
    fun loginUser(@Body loginRequest: LoginRequest):Single<LoginResponse>

    @POST("account/register")
    fun registerUser(@Body RegisterRequest: RegisterRequest):Single<RegisterResponse>
}