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

//COURIER LIST
data class CourierListResponse(
        val err_code: String?,
        val err_message: String?,
        val result: MutableList<CourierList>
)

data class CourierList(
        var courier_image: String?,
        var courier_name: String,
        var services_list: MutableList<CourierServiceList>
)

data class CourierServiceList(
        var courier_services_code: String?,
        var courier_services_name: String?,
        var description: String?,
        var courier_services_type: Boolean = true
)

//REQUEST SUBMIT DATA SHIPMENT
data class SubmitDataShipmentResponse (
        val err_code: String?,
        val err_message: String?,
        val result: String?
)

data class RequestMerchantShipment (
        val merchant_address: String,
        val latitude: String,
        val longtitude: String,
        val postal_code: String,
        val subdistrict_name: String,
        val province: String,
        val shipping_available: Boolean,
        val courier: MutableList<Courier>
)

data class Courier (
        val gojek: Gojek,
        val grab: Grab,
        val lalamove: Lalamove,
        val mr_speedy: MrSpeedy,
        val paxel: Paxel,
        val rara: Rara
)

data class Gojek (
        @SerializedName("gojek_main")
        var gojek_main: Boolean,

        @SerializedName("instant_services")
        var instant_services: Boolean,

        @SerializedName("same_day_services")
        var same_day_services: Boolean
)

data class Grab (
        @SerializedName("grab_main")
        var grab_main: Boolean,

        @SerializedName("instant_services")
        var instant_services: Boolean,

        @SerializedName("same_day_services")
        var same_day_services: Boolean,

        @SerializedName("instant_car_services")
        var instant_car_services: Boolean
)

data class Lalamove (
        @SerializedName("lalamove_main")
        var lalamove_main: Boolean,

        @SerializedName("motor_services")
        var motor_services: Boolean,

        @SerializedName("mpv_services")
        var mpv_services: Boolean,

        @SerializedName("truck_services")
        var truck_services: Boolean,

        @SerializedName("van_services")
        var van_services: Boolean
)

data class MrSpeedy (
        @SerializedName("mr_speedy_main")
        var mr_speedy_main: Boolean,

        @SerializedName("car_services")
        var car_services: Boolean,

        @SerializedName("bike_services")
        var bike_services: Boolean
)

data class Paxel (
        @SerializedName("paxel_main")
        var paxel_main: Boolean,

        @SerializedName("big_services")
        var big_services: Boolean,

        @SerializedName("large_services")
        var large_services: Boolean,

        @SerializedName("medium_services")
        var medium_services: Boolean,

        @SerializedName("small_services")
        var small_services: Boolean
)

data class Rara (
        @SerializedName("rara_main")
        var rara_main: Boolean,

        @SerializedName("instant_services")
        var instant_services: Boolean
)