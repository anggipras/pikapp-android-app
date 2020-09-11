package com.bejohen.pikapp.models.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class ItemHomeCategoryResponse(

    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("results")
    val results: List<ItemHomeCategory>?
)

@Entity
data class ItemHomeCategory(
    @SerializedName("category_id")
    val categoryId: Long?,
    @SerializedName("category_name")
    val categoryName: String?,
    @SerializedName("category_ordering")
    val categoryOrdering: Int?,
    @SerializedName("category_pict")
    val categoryPict: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

data class ItemHomeBannerSliderResponse(

    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("results")
    val results: List<ItemHomeBannerSlider>?
)

@Entity
data class ItemHomeBannerSlider(
    @SerializedName("slider_name")
    val sliderName: String?,
    @SerializedName("slider_pict")
    val sliderPict: String?
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}

data class LatestLocation(
    val longitude: String?,
    val latitude: String?
)