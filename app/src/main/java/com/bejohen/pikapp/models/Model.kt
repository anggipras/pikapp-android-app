package com.bejohen.pikapp.models

import com.google.gson.annotations.SerializedName


data class LoginRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?
)

data class LoginResponse(
    val user: String?,
    val token: String?
)

data class RegisterRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("password")
    val password: String?
)

data class RegisterResponse(
    val user: String?,
    val token: String?
)