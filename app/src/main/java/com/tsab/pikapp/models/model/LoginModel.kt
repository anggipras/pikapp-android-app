package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    val email: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("fcm_token")
    val fcmToken: String?
)

data class LoginRequestV2(
    @SerializedName("username")
    var username: String? = null,

    @SerializedName("pin")
    var pin: String? = null,

    @SerializedName("token")
    var token: String? = null
)

data class LoginResponseV2(
    @SerializedName("err_code")
    val errCode: String?,

    @SerializedName("err_message")
    val errMessage: String?,

    @SerializedName("results")
    val results: LoginResultV2?
)

data class LoginResultV2 (
    @SerializedName("token")
    val token: String?,

    @SerializedName("is_verified")
    val isVerified: Boolean?,

    @SerializedName("login_time")
    val loginTime: String?
)

data class LoginResponse(
    @SerializedName("err_code")
    val errCode: String?,

    @SerializedName("err_message")
    val errMessage: String?,

    val token: String?,

    @SerializedName("new_event")
    val newEvent: Boolean?,

    @SerializedName("recommendation_status")
    val recommendationStatus: Boolean?
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

data class ErrorResponse(
    @SerializedName("err_code")
    val errCode: String?,

    @SerializedName("err_message")
    val errMessage: String?
)

data class LogoutResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?
) : Response