package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

enum class OmnichannelType {
    @SerializedName("TOKOPEDIA")
    TOKOPEDIA,

    @SerializedName("GRAB")
    GRAB,

    @SerializedName("GOJEK")
    GOJEK
}

enum class ShopCategory {
    @SerializedName("OFFICIAL")
    OFFICIAL,

    @SerializedName("POWER_MERCHANT")
    POWER_MERCHANT
}

enum class OmnichannelStatus {
    @SerializedName("CONNECTED")
    CONNECTED,

    @SerializedName("ON_PROGRESS")
    ON_PROGRESS,

    @SerializedName("EXPIRED")
    EXPIRED,

    @SerializedName("DISCONNECTED")
    DISCONNECTED,

    @SerializedName("FAIL_TO_CONNECT")
    FAIL_TO_CONNECT
}

data class ConnectIntegrationRequest(
    @SerializedName("mid")
    var merchantId: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("phone_number")
    var phoneNumber: String,

    @SerializedName("shop_name")
    var shopName: String,
    @SerializedName("shop_domain")
    var shopDomain: String,

    @SerializedName("channel_type")
    var channelType: OmnichannelType,
    @SerializedName("shop_category")
    var shopCategory: ShopCategory
)

data class IntegrationObjectResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var result: Omnichannel
)

data class IntegrationArrayResponse(
    @SerializedName("err_code")
    var errorCode: String,
    @SerializedName("err_message")
    var errorMessage: String,

    @SerializedName("results")
    var results: List<Omnichannel>?
)

data class Omnichannel(
    @SerializedName("id")
    var id: String?,

    @SerializedName("channel")
    var name: String,
    @SerializedName("status")
    var status: OmnichannelStatus,
    @SerializedName("channel_id")
    var channelId: String,

    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("updated_at")
    var updatedAt: String
)
