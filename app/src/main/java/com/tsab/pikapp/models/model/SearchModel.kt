package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SearchRequest(
    @SerializedName("menu_name")
    var menu_name: String,
    @SerializedName("page")
    var page: Int,
    @SerializedName("size")
    var size: Int
)

data class SearchResponse(
    @SerializedName("err_code")
    var errCode: String?,
    @SerializedName("err_message")
    var errMessage: String?,
    @SerializedName("total_pages")
    var total_pages: Int,
    @SerializedName("current_page")
    var current_page: Int,
    @SerializedName("total_items")
    var total_items: Int,
    var results: List<SearchList>
)

data class SearchList(
    @SerializedName("product_id")
    var product_id: String?,
    @SerializedName("pict_01")
    var pict_01: String?,
    @SerializedName("pict_02")
    var pict_02: String?,
    @SerializedName("pict_03")
    var pict_03: String?,
    @SerializedName("product_name")
    var product_name: String?,
    @SerializedName("price")
    var price: String?,
    @SerializedName("on_off")
    var on_off: String?,
    @SerializedName("deleted")
    var deleted: String?,
    @SerializedName("created_date")
    var created_date: String?,
    @SerializedName("product_desc")
    var product_desc: String?,
    @SerializedName("condition")
    var condition: String?,
    @SerializedName("rating")
    var rating: String?,
    @SerializedName("qty")
    var qty: Int,
    @SerializedName("merchant_category_name")
    var merchant_category_name: String?,
    @SerializedName("merchant_category_order")
    var merchant_category_order: Int
) : Serializable