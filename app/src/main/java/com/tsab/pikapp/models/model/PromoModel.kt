package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PromoListRequest(
    @SerializedName("start_date")
    val start_date: String? = null,
    @SerializedName("end_date")
    val end_date: String? = null,
    @SerializedName("campaign_name")
    val campaign_name: String? = null,
    @SerializedName("campaign_status")
    val campaign_status: List<String>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int
)

data class PromoRegisRequest(
    @SerializedName("merchant_id")
    val merchant_id: String,
    @SerializedName("campaign_id")
    val campaign_id: Long?
)

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

//PROMO REGIS LIST START
data class PromoRegisListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("results")
    val results: List<PromoRegisListModel>?
) : Serializable

data class PromoRegisListModel(
    @SerializedName("id")
    val id: Long,
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
    @SerializedName("campaign_deadline_date")
    val campaign_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?,
    @SerializedName("campaign_code")
    val campaign_code: String?
) : Serializable

data class PromoRegisListData(
    val viewType: Int,
    @SerializedName("id")
    val id: Long,
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
    @SerializedName("campaign_deadline_date")
    val campaign_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?,
    @SerializedName("campaign_code")
    val campaign_code: String?
) : Serializable
//PROMO REGIS LIST END

//PROMO APPLIED LIST START
data class PromoAppliedListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("results")
    val results: List<PromoAppliedListModel>?
) : Serializable

data class PromoAppliedListModel(
    @SerializedName("id")
    val id: Long,
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
    @SerializedName("campaign_deadline_date")
    val campaign_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?,
    @SerializedName("campaign_code")
    val campaign_code: String?
) : Serializable

data class PromoAppliedListData(
    val viewType: Int,
    @SerializedName("id")
    val id: Long,
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
    @SerializedName("campaign_deadline_date")
    val campaign_deadline_date: String?,
    @SerializedName("campaign_detail")
    val campaign_detail: String?,
    @SerializedName("campaign_status")
    val campaign_status: String?,
    @SerializedName("campaign_code")
    val campaign_code: String?
) : Serializable
//PROMO APPLIED LIST END