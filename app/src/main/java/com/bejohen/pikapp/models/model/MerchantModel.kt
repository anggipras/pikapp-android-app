package com.bejohen.pikapp.models.model

import com.google.gson.annotations.SerializedName

// Merchant List API Specs
data class MerchantListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: List<MerchantList>?
)

data class MerchantList(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("merchant_address")
    val merchantAddress: String?,
    @SerializedName("merchant_rating")
    val merchantRating: String?,
    @SerializedName("merchant_pict")
    val merchantPict: String?,
    @SerializedName("merchant_logo")
    val merchantLogo: String?,
    @SerializedName("merchant_distance")
    val merchantDistance: String?,
    @SerializedName("products")
    val products: List<ProductListSmall>?
)

data class ProductListSmall(
    @SerializedName("product_id")
    val productID: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_price")
    val productPrice: Int?,
    @SerializedName("product_picture1")
    val productPicture1: String?,
    @SerializedName("product_picture2")
    val productPicture2: String?,
    @SerializedName("product_picture3")
    val productPicture3: String?
)

data class MerchantListErrorResponse(
    @SerializedName("timestamp")
    val timestamp: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("path")
    val path: String?
)

// Merchant Detail API Specs
data class MerchantDetailResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: MerchantDetail?
)

data class MerchantDetail(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("merchant_address")
    val merchantAddress: String?,
    @SerializedName("merchant_rating")
    val merchantRating: String?,
    @SerializedName("merchant_pict")
    val merchantPict: String?,
    @SerializedName("merchant_logo")
    val merchantLogo: String?,
    @SerializedName("merchant_distance")
    val merchantDistance: String?,
    @SerializedName("merchant_level")
    val merchantLevel: String?,
    @SerializedName("pickups")
    val pickups: List<Pickups>?
)

data class Pickups(
    @SerializedName("pickup_type")
    val pickupType: String?,
    @SerializedName("pickup_est_time")
    val pickupEstTime: String?,
    @SerializedName("pickup_type_desc")
    val pickupTypeDesc: String?,
    @SerializedName("pickup_logo")
    val pickupLogo: String?
)

// Product List API Specs
data class ProductListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: List<ProductList>?
)

data class ProductList(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("product_id")
    val productID: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_price")
    val productPrice: Int?,
    @SerializedName("product_picture")
    val productPicture: List<String>?
)

// Product Detail API Specs
data class ProductDetailResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: ProductDetail?
)

data class ProductDetail(
    @SerializedName("product_id")
    val productID: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_price")
    val productPrice: Int?,
    @SerializedName("product_picture")
    val productPicture: List<String>?,
    @SerializedName("product_desc")
    val productDesc: String?,
    @SerializedName("total_sold")
    val totalSold: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("total_rate")
    val totalRate: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("merchant_logo")
    val merchantLogo: String?,
    @SerializedName("mid")
    val merchantID: String?
)

