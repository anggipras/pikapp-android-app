package com.bejohen.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class CartModel(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("merchant_logo")
    val merchantLogo: String?,
    @SerializedName("merchant_address")
    val merchantAddress: String?,
    @SerializedName("total_price")
    val totalPrice: String?,
    @SerializedName("product_tax")
    val productTax: Int?,
    @SerializedName("product_service")
    val productService: Int?,
    @SerializedName("cart_detail")
    val cartDetail: ArrayList<CartDetail>?
)

data class CartDetail(
    @SerializedName("pid")
    val productID: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_picture")
    val productPicture: String?,
    @SerializedName("product_quantity")
    val productQuantity: String?,
    @SerializedName("product_price")
    val productPrice: Int?
)