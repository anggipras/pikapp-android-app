package com.bejohen.pikapp.models.network

import com.bejohen.pikapp.models.LoginRequest
import com.bejohen.pikapp.models.LoginResponse
import com.bejohen.pikapp.models.RegisterRequest
import com.bejohen.pikapp.models.RegisterResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.sql.Timestamp

interface PikappApi {
    @Headers("x-request-id:UUID")
    @POST("auth/login")
    fun loginUser(
        @Header("x-request-timestamp") time: String,
        @Body loginRequest: LoginRequest) : Single<LoginResponse>

    @Headers("x-request-id:UUID")
    @POST("auth/register")
    fun registerUser(
        @Header("x-request-timestamp") time: String,
        @Body RegisterRequest: RegisterRequest) : Single<RegisterResponse>
}