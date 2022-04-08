package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

data class PromoRegisListModel(
    @SerializedName("campaign_name")
    val campaign_name: String?,
    @SerializedName("campaign_image")
    val campaign_image: String?,
    @SerializedName("campaign_quota")
    val campaign_quota: String?,
    @SerializedName("discount_amt_type")
    val discount_amt_type: String?,
    @SerializedName("discount_amt")
    val discount_amt: Long?,
    @SerializedName("campaign_start_date")
    val campaign_start_date: String?,
    @SerializedName("campaign_end_date")
    val campaign_end_date: String?,
    @SerializedName("campaign_regis_deadline_date")
    val campaign_regis_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?
) : Serializable

data class PromoRegisListData(
    val viewType: Int,
    @SerializedName("campaign_name")
    val campaign_name: String?,
    @SerializedName("campaign_image")
    val campaign_image: String?,
    @SerializedName("campaign_quota")
    val campaign_quota: String?,
    @SerializedName("discount_amt_type")
    val discount_amt_type: String?,
    @SerializedName("discount_amt")
    val discount_amt: Long?,
    @SerializedName("campaign_start_date")
    val campaign_start_date: String?,
    @SerializedName("campaign_end_date")
    val campaign_end_date: String?,
    @SerializedName("campaign_regis_deadline_date")
    val campaign_regis_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?
) : Serializable

data class PromoAppliedListModel(
    @SerializedName("campaign_name")
    val campaign_name: String?,
    @SerializedName("campaign_image")
    val campaign_image: String?,
    @SerializedName("campaign_quota")
    val campaign_quota: String?,
    @SerializedName("discount_amt_type")
    val discount_amt_type: String?,
    @SerializedName("discount_amt")
    val discount_amt: Long?,
    @SerializedName("campaign_start_date")
    val campaign_start_date: String?,
    @SerializedName("campaign_end_date")
    val campaign_end_date: String?,
    @SerializedName("campaign_regis_deadline_date")
    val campaign_regis_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?
) : Serializable

data class PromoAppliedListData(
    val viewType: Int,
    @SerializedName("campaign_name")
    val campaign_name: String?,
    @SerializedName("campaign_image")
    val campaign_image: String?,
    @SerializedName("campaign_quota")
    val campaign_quota: String?,
    @SerializedName("discount_amt_type")
    val discount_amt_type: String?,
    @SerializedName("discount_amt")
    val discount_amt: Long?,
    @SerializedName("campaign_start_date")
    val campaign_start_date: String?,
    @SerializedName("campaign_end_date")
    val campaign_end_date: String?,
    @SerializedName("campaign_regis_deadline_date")
    val campaign_regis_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?
) : Serializable