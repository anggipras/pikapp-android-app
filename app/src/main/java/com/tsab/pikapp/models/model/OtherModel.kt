package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

//MERCHANT PROFILE DATA
data class MerchantProfileResponse(
        @SerializedName("err_code")
        val errCode: String?,
        @SerializedName("err_message")
        val errMessage: String?,
        val results: MerchantProfileData?
)

//MERCHANT PROFILE SET
data class MerchantProfileData(
        @SerializedName("mid")
        val mid: String? = null,

        @SerializedName("full_name")
        val fullName: String? = null,

        @SerializedName("merchant_name")
        val merchantName: String? = null,

        @SerializedName("phone_number")
        val phoneNumber: String? = null,

        @SerializedName("email")
        val email: String? = null,

        @SerializedName("merchant_banner")
        val merchantBanner: String? = null,

        @SerializedName("merchant_logo")
        val merchantLogo: String? = null,

        @SerializedName("address")
        val address: String? = null,

        @SerializedName("date_of_birth")
        val dateOfBirth: String? = null,

        @SerializedName("gender")
        val gender: String? = null,

        @SerializedName("bank_account_no")
        val bankAccountNo: String? = null,

        @SerializedName("bank_account_name")
        val bankAccountName: String? = null,

        @SerializedName("bank_name")
        val bankName: String? = null
)

//SHOP MANAGEMENT SETTINGS
data class MerchantTimeManagement(
        @SerializedName("err_code")
        val errCode: String?,
        @SerializedName("err_message")
        val errMessage: String?,
        val results: TimeManagementResponse?
)

data class TimeManagementResponse(
        @SerializedName("auto_on_off")
        val autoOnOff: Boolean?,

        @SerializedName("time_management")
        val timeManagement: MutableList<ShopSchedule>?
)

data class ShopSchedule(
        @SerializedName("days")
        val days: String?,

        @SerializedName("close_time")
        val closeTime: String?,

        @SerializedName("open_time")
        val openTime: String?,

        @SerializedName("daily_status")
        val dailyStatus: String?,

        @SerializedName("is_force_close")
        val isForceClose: Boolean?
)

//MERCHANT CHANGE PIN
data class pinMerchant(
        @SerializedName("old_pin")
        val oldPin: String?,

        @SerializedName("mid")
        val mid: String?,

        @SerializedName("pin")
        val pin: String?
)

data class OtherBaseResponse(
        @SerializedName("err_code")
        val errCode: String?,
        @SerializedName("err_message")
        val errMessage: String?
)