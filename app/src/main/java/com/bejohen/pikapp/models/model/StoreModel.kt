package com.bejohen.pikapp.models.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class StoreProductListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: List<StoreProductList>?
)

data class StoreProductList(
    @SerializedName("product_id")
    var productId: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("pict_01")
    val productPicture1: String?,
    @SerializedName("pict_02")
    val productPicture2: String?,
    @SerializedName("pict_03")
    val productPicture3: String?,
    @SerializedName("price")
    val productPrice: Long?,
    @SerializedName("on_off")
    val onOff: Boolean?,
    @SerializedName("deleted")
    val deleted: Boolean?,
    @SerializedName("created_date")
    val createdDate: String?,
    @SerializedName("product_desc")
    val productDesc: String?,
    @SerializedName("condition")
    val condition: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("qty")
    val qty: Int?
)

data class StoreProductActionResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?
): Response

data class StoreProductDetailResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: StoreProductList?
)

data class StoreImage(
    val imageUri: Uri?
)

