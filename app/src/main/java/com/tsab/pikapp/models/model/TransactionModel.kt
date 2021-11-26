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
    var total_product_price: String,
    var payment_status: String,
    var payment_method: String,
    var order_status: String,
    var total_discount: Int,
    var total_payment: String
)

data class ShippingData(
    var shipping_method: String,
    var shipping_cost: String,
    var shipping_time: String
)

data class MenuList(
    var product_id: String,
    var product_name: String,
    var product_category: String,
    var product_price: String,
    var notes: String,
    var quantity: Int,
    var discount: Int,
    var extra_price: String
)

data class ManualTxnResponse(
    val err_code: String?,
    val err_message: String?,
    var results: ManualTxnResult
)

data class ManualTxnResult(
    var shipping: ShippingData,
    var customer: CustomerData,
    var products: List<MenuListResponse>,
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
    var extra_price: Int,
    var product_id: String,
    var product_name: String
)

data class CustomerData(
    var address: String,
    var customer_id: Int,
    var customer_name: String,
    var phone_number: String,
    var address_detail: String
)





