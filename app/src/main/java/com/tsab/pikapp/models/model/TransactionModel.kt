package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

enum class OmniTransactionStatus {
    OPEN, UNPAID, PAID, ON_PROCESS, DELIVER, CLOSE, FINALIZE, FAILED, ERROR
}

data class OmniTransaction(
    // Internal transaction ID from Pikapp.
    var id: String,

    var omnichannelType: OmnichannelType?,
    var status: OmniTransactionStatus?,

    var transactionId: String?,
    var transactionTime: String?,

    // Transaction item details.
    var items: List<CartTxnModel>?
)

data class ManualTxnRequest(
    var products: List<MenuList>,
    var shipping: ShippingData,
    var customer_id: String,
    var mid: String,
    var order_type: String,
    var order_platform: String,
    var total_product_price: Long,
    var payment_status: String,
    var payment_method: String,
    var order_status: String,
    var total_discount: Long,
    var total_payment: Long
)

data class ShippingData(
    var shipping_method: String,
    var shipping_cost: Int,
    var shipping_time: String,
    var shipping_time_type: String,
    var shipping_insurance: Long?,
    var shipping_service_type: String?
)

data class MenuList(
    var product_id: String,
    var product_name: String,
    var product_category: String,
    var product_price: Int,
    var notes: String,
    var quantity: Int,
    var discount: Int,
    var extra_price: String,
    var extra_menus: List<ExtraList>
)

data class ExtraList(
    var extra_menu_name: String,
    var extra_menu_price: Int
)

data class ManualTxnResponse(
    val err_code: String?,
    val err_message: String?,
    var results: ManualTxnResult
)

data class ManualTxnResult(
    var shipping: ShippingData,
    var customer: CustomerData,
    var productList: List<MenuListResponse>,
    var transaction_id: String,
    var transaction_time: String,
    var order_id: String,
    var mid: String,
    var order_type: String,
    var order_status: String,
    var payment_status: String,
    var order_platform: String,
    var total_product_price: Int,
    var total_discount: Int,
    var total_payment: String,
    var created_at: String,
    var updated_at: String
)

data class MenuListResponse(
    var quantity: Int,
    var price: Int,
    var discount: Int,
    var notes: String,
    var extraPrice: Int,
    var product_id: String,
    var product_name: String,
    var extra_menus: String
)

data class CustomerData(
    var address: String,
    var customer_id: Int,
    var customer_name: String,
    var phone_number: String,
    var address_detail: String
)

/* Transaction List V2 */

//Response from API
data class TransactionListV2RespAPI(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("results")
    var results: List<TransactionListV2Response>?
)

data class TransactionListV2Response(
    @SerializedName("txn_type")
    val txn_type: String?,
    @SerializedName("order_id")
    val order_id: String?,
    @SerializedName("merchant_name")
    val merchant_name: String?,
    @SerializedName("shop_id")
    val shop_id: String?,
    @SerializedName("table_no")
    val table_no: String?,
    @SerializedName("channel")
    val channel: String?,
    @SerializedName("mid")
    val mid: String?,
    @SerializedName("biz_type")
    val biz_type: String?,
    @SerializedName("order_platform")
    val order_platform: String?,
    @SerializedName("payment_method")
    val payment_method: String?,
    @SerializedName("order_status")
    val order_status: String?,
    @SerializedName("payment_status")
    val payment_status: String?,
    @SerializedName("total_product_price")
    val total_product_price: Int?,
    @SerializedName("total_discount")
    val total_discount: Int?,
    @SerializedName("total_payment")
    val total_payment: Int?,
    @SerializedName("total_insurance_cost")
    val total_insurance_cost: Int?,
    @SerializedName("voucher_type")
    val voucher_type: String?,
    @SerializedName("voucher_code")
    val voucher_code: String?,
    @SerializedName("transaction_id")
    val transaction_id: String?,
    @SerializedName("transaction_time")
    val transaction_time: String?,
    @SerializedName("shipping")
    val shipping: ShippingDetailV2Response?,
    @SerializedName("products")
    val products: List<ProductDetailV2Response>?,
    @SerializedName("customer")
    val customer: CustomerDetailV2Response?
)

data class ShippingDetailV2Response(
    @SerializedName("awb")
    val awb: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("province")
    val province: String?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("geo")
    val geo: String?,
    @SerializedName("shipping_method")
    val shipping_method: String?,
    @SerializedName("shipping_cost")
    val shipping_cost: Long?,
    @SerializedName("shipping_time_type")
    val shipping_time_type: String?,
    @SerializedName("shipping_time")
    val shipping_time: String?,
    @SerializedName("shipping_service")
    val shipping_service: String?,
    @SerializedName("shipping_id")
    val shipping_id: Int?,
    @SerializedName("recipient_name")
    val recipient_name: String?,
    @SerializedName("address_detail")
    val address_detail: String?,
    @SerializedName("phone_number")
    val phone_number: String?,
    @SerializedName("postal_code")
    val postal_code: String?
)

data class ProductDetailV2Response(
    @SerializedName("product_id")
    val product_id: String?,
    @SerializedName("product_name")
    val product_name: String?,
    @SerializedName("product_price")
    val product_price: String?,
    @SerializedName("discount")
    val discount: Int?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("extra_price")
    val extra_price: Int?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("weight")
    val weight: Float?,
    @SerializedName("sku")
    val sku: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("total_weight")
    val total_weight: Float?,
    @SerializedName("total_price")
    val total_price: Int?,
    @SerializedName("extra_menus")
    val extra_menus: List<ExtraMenusDetailV2Response>?
)

data class CustomerDetailV2Response(
    @SerializedName("name")
    val name: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("address_detail")
    val address_detail: String?,
    @SerializedName("phone_number")
    val phone_number: String?,
    @SerializedName("email")
    val email: String?
)

data class ExtraMenusDetailV2Response(
    @SerializedName("variant_id")
    val variant_id: String?,
    @SerializedName("variant_name")
    val variant_name: String?,
    @SerializedName("variant_price")
    val variant_price: Long?,
    @SerializedName("quantity")
    val quantity: Int?,
)

//Mapped response added with txnType condition (viewType)
data class TransactionListV2Data(
    val viewType: Int,
    @SerializedName("txn_type")
    val txn_type: String?,
    @SerializedName("order_id")
    val order_id: String?,
    @SerializedName("merchant_name")
    val merchant_name: String?,
    @SerializedName("shop_id")
    val shop_id: String?,
    @SerializedName("table_no")
    val table_no: String?,
    @SerializedName("channel")
    val channel: String?,
    @SerializedName("mid")
    val mid: String?,
    @SerializedName("biz_type")
    val biz_type: String?,
    @SerializedName("order_platform")
    val order_platform: String?,
    @SerializedName("payment_method")
    val payment_method: String?,
    @SerializedName("order_status")
    val order_status: String?,
    @SerializedName("payment_status")
    val payment_status: String?,
    @SerializedName("total_product_price")
    val total_product_price: Int?,
    @SerializedName("total_discount")
    val total_discount: Int?,
    @SerializedName("total_payment")
    val total_payment: Int?,
    @SerializedName("total_insurance_cost")
    val total_insurance_cost: Int?,
    @SerializedName("voucher_type")
    val voucher_type: String?,
    @SerializedName("voucher_code")
    val voucher_code: String?,
    @SerializedName("transaction_id")
    val transaction_id: String?,
    @SerializedName("transaction_time")
    val transaction_time: String?,
    @SerializedName("shipping")
    val shipping: ShippingDetailV2Response?,
    @SerializedName("products")
    val products: List<ProductDetailV2Response>?,
    @SerializedName("customer")
    val customer: CustomerDetailV2Response?
)

data class FilterMockUp(
    var txnType: String?,
    var orderPlatform: String?
)

/* TRACK DETAIL ORDER */
data class TrackingDetailResponse (
    @SerializedName("err_code")
    val errCode: String,
    @SerializedName("err_message")
    val errMessage: String,
    val result: TrackingDetailResult
)

data class TrackingDetailResult (
    val success: Boolean,
    val messsage: String?,
    @SerializedName("object")
    val resultObject: String,
    val id: String,
    @SerializedName("waybill_id")
    val waybillID: String,
    val courier: CourierDriver,
    val origin: OriginPlace,
    val destination: DestinationPlace,
    val history: List<TrackingDetail>,
    val link: String?,
    @SerializedName("order_id")
    val orderID: String? = null,
    val status: String?
)

data class CourierDriver (
    val company: String,
    val name: String? = null,
    val phone: String? = null
)

data class OriginPlace (
    @SerializedName("contact_name")
    val contactName: String,
    val address: String
)

data class DestinationPlace (
    @SerializedName("contact_name")
    val contactName: String,
    val address: String
)

data class TrackingDetail(
    var note: String?,
    var updated_at: String?,
    var status: String?
)

data class TrackingOrderRequest(
    var waybill_id: String?,
    var courier: String?
)





