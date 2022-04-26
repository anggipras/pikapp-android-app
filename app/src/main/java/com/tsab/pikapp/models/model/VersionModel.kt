package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class LatestVersionModel(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    @SerializedName("results")
    val results: LatestVersionResponse
) : Response

data class LatestVersionResponse(
    @SerializedName("app_name")
    val app_name: String,
    @SerializedName("app_version")
    val app_version: String,
    @SerializedName("force_update")
    val force_update: Boolean,
    @SerializedName("create_at")
    val create_at: String,
    @SerializedName("update_at")
    val update_at: String
)