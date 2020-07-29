package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.LoginRequest
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.RegisterRequest
import com.bejohen.pikapp.models.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PikappApi {
    @POST("auth/login")
    fun loginUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Body loginRequest: LoginRequest) : Single<LoginResponse>

    @POST("auth/register")
    fun registerUser(
        @Header("x-request-id") uuid: String,
        @Header("x-request-timestamp") time: String,
        @Header("x-client-id") clientID: String,
        @Body RegisterRequest: RegisterRequest) : Single<RegisterResponse>
}