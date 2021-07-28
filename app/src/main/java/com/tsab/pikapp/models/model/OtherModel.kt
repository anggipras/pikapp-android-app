package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class MerchantProfileResponse(
        @SerializedName("err_code")
        val errCode: String?,
        @SerializedName("err_message")
        val errMessage: String?,
        @SerializedName("results")
        val results: ProfileResponse?
)

data class ProfileResponse (
        @SerializedName("pickups")
        val pickups: String?,

        @SerializedName("mid")
        val mid: String?,

        @SerializedName("merchant_pict")
        val merchantPict: String?,

        @SerializedName("merchant_logo")
        val merchantLogo: String?,

        @SerializedName("merchant_name")
        val merchantName: String?,

        @SerializedName("merchant_level")
        val merchantLevel: String?,

        @SerializedName("merchant_rating")
        val merchantRating: String?,

        @SerializedName("merchant_address")
        val merchantAddress: String?,

        @SerializedName("merchant_distance")
        val merchantDistance: String?,

        @SerializedName("merchant_categories")
        val merchantCategories: List<MerchantCategories>?
)

data class MerchantCategories(
        val merchantCategory: String?
)