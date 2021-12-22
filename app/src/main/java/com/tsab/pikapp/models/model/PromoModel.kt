package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class PromoListModel(
    @SerializedName("promo_id")
    val promoID: String?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("status")
    val status: String?
)