package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class ListMenuCategoryResponse(
    @SerializedName("err_code")
    val errorCode: String,
    @SerializedName("err_message")
    val errorMessage: String,
    @SerializedName("results")
    val results: List<MenuCategory>
)

data class MenuCategory(
    @SerializedName("category_name")
    val categoryName: String,
    @SerializedName("category_ordering")
    val categoryOrder: Int,
    @SerializedName("activation")
    val isActive: Boolean,
    @SerializedName("id")
    val id: Long,
    @SerializedName("product_size")
    val productSize: Int
)