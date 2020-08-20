package com.bejohen.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class UserJWTBody(
    val sub: UserAccess,
    val iss: String?,
    val exp: String?,
    val iat: String?
)

data class UserAccess(
    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("customer_name")
    val customerName: String?,

    val email: String?,

    @SerializedName("session_id")
    val sessionId: String?
) {
    var expired: Long? = null
}