package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

enum class OmnichannelType {
    @SerializedName("tokopedia")
    TOKOPEDIA
}

enum class OmnichannelStatus {
    @SerializedName("connected")
    CONNECTED,

    @SerializedName("waiting")
    WAITING,

    @SerializedName("expired")
    EXPIRED
}

data class Omnichannel(
    var name: String,
    var type: OmnichannelType,
    var status: OmnichannelStatus
)
