package com.bejohen.pikapp.models

import com.google.gson.annotations.SerializedName


data class LoginRequest(
    @SerializedName("username")
    val email: String?,
    @SerializedName("password")
    val password: String?
)

data class LoginResponse(
    @SerializedName("err_code")
    val errCode: String?,

    @SerializedName("err_message")
    val errMessage: String?,

    val token:String?
)

data class RegisterRequest(
    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    val password: String?,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("birth_day")
    val birthday: String?,

    @SerializedName("gender")
    val gender: String?
)

data class RegisterResponse(
    @SerializedName("err_code")
    val errCode: String?,

    @SerializedName("err_message")
    val errMessage: String?
)