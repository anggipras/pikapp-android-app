package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class MerchantProfileResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
//        @SerializedName("results")
    val results: ProfileResponse?
)

data class ProfileResponse(
    @SerializedName("pickups")
    val pickups: String? = null,

    @SerializedName("products")
    val products: MutableList<MerchantProducts>?,

    @SerializedName("mid")
    val mid: String?,

    @SerializedName("merchant_pict")
    val merchantPict: String?,

    @SerializedName("merchant_logo")
    val merchantLogo: String?,

    @SerializedName("merchant_name")
    val merchantName: String?,

    @SerializedName("merchant_level")
    val merchantLevel: String? = null,

    @SerializedName("merchant_rating")
    val merchantRating: String? = null,

    @SerializedName("merchant_address")
    val merchantAddress: String?,

    @SerializedName("merchant_distance")
    val merchantDistance: String?,

    @SerializedName("merchant_categories")
    val merchantCategories: List<String>?
)

data class MerchantProducts(
    @SerializedName("product_name")
    val productName: String?,

    @SerializedName("product_picture1")
    val product_picture1: String?,

    @SerializedName("product_picture2")
    val product_picture2: String?,

    @SerializedName("product_picture3")
    val product_picture3: String?,

    @SerializedName("product_price")
    val product_price: Int?,

    @SerializedName("product_status")
    val product_status: String?,

    @SerializedName("product_category")
    val product_category: Int?,

    @SerializedName("product_desc")
    val product_desc: String?,

    @SerializedName("rating")
    val rating: Double?
)