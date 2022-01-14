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
        @SerializedName("results")
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

data class ShopManagementUpdateRequest(
        @SerializedName("auto_on_off")
        var autoOnOff: Boolean? = true,

        @SerializedName("days")
        var days: String? = null,

        @SerializedName("close_time")
        var closeTime: String? = null,

        @SerializedName("open_time")
        var openTime: String? = null,

        @SerializedName("is_force_close")
        var isForceClose: Boolean? = true
)
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

data class UploadReportResponse(
        @SerializedName("err_code")
        val err_code: String?,
        @SerializedName("err_message")
        val err_message: String?,
        var results: UploadResult
)

data class UploadResult(
        @SerializedName("Status")
        val Status: String?,
        var filename: List<String>
)

//SHIPMENT MANAGEMENT SETTINGS ------- //
data class CurrentLatLng(
        val latitude: Double,
        val longitude: Double,
)

//LIST OF GOOGLE PLACES
data class GooglePlacesResponse (
        @SerializedName("html_attributions")
        val htmlAttributions: List<String>?,
        val results: List<ListGooglePlaces>,
        val status: String
)

data class ListGooglePlaces (
        @SerializedName("business_status")
        val businessStatus: String?,
        @SerializedName("formatted_address")
        val formattedAddress: String?,
        val geometry: Geometry,
        val icon: String?,
        @SerializedName("icon_background_color")
        val iconBackgroundColor: String?,
        @SerializedName("icon_mask_base_uri")
        val iconMaskBaseURI: String?,
        val name: String?,
        @SerializedName("opening_hours")
        val openingHours: OpeningHours?,
        val photos: List<Photo>?,
        @SerializedName("place_id")
        val placeID: String?,
        @SerializedName("plus_code")
        val plusCode: PlusCode?,
        val rating: Double?,
        val reference: String?,
        val types: List<String>?,
        @SerializedName("user_ratings_total")
        val userRatingsTotal: Long?
)

data class Geometry (
        val location: Location,
        val viewport: Viewport?
)

data class Location (
        val lat: Double,
        val lng: Double
)

data class Viewport (
        val northeast: Location?,
        val southwest: Location?
)

data class OpeningHours (
        @SerializedName("open_now")
        val openNow: Boolean?
)

data class Photo (
        val height: Long?,
        @SerializedName("html_attributions")
        val htmlAttributions: List<String>?,
        @SerializedName("photo_reference")
        val photoReference: String?,
        val width: Long?
)

data class PlusCode (
        @SerializedName("compound_code")
        val compoundCode: String?,
        @SerializedName("global_code")
        val globalCode: String?
)

data class CourierList(
        var courier_name: String?,
        var courierService: MutableList<CourierServiceList>
)

data class CourierServiceList(
        var service_name: String?,
        var service_desc: String?,
        var service_type: Boolean
)