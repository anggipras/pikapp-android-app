package com.bejohen.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class UserExclusiveRecommendationRequest(
    @SerializedName("recommendation_01")
    val recommendation01: String?,

    @SerializedName("recommendation_02")
    val recommendation_02: String?,

    @SerializedName("recommendation_03")
    val recommendation_03: String?,

    @SerializedName("recommendation_04")
    val recommendation_04: String?,

    @SerializedName("recommendation_05")
    val recommendation_05: String?
)

data class UserExclusiveRecommendationResponse(
    @SerializedName("err_code")
    override val errCode: String?,

    @SerializedName("err_message")
    override val errMessage: String?) : Response