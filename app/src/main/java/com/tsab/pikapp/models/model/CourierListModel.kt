package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class CustomerCourierListResponse(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val result: List<CustomerCourierListResult>
)

data class CustomerCourierListResult(
    val name: String,
    val description: String,
    val lower_limit: Long,
    val upper_limit: Long,
    val courier_list: MutableList<CustomerCourierServiceList>,
)

data class CustomerCourierServiceList(
    val courier_image: String?,
    val name: String,
    val description: String,
    val price: Long,
    val service_name: String?
)