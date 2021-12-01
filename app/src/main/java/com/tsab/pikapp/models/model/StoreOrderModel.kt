package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class GetStoreOrderListResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: List<StoreOrderList>?
) : Response

data class GetStoreOrderListV2Response(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    @SerializedName("total_items")
    val total_items: Int?,
    @SerializedName("total_page")
    val total_page: Int?,
    val results: List<StoreOrderList>?
)

data class TransactionListRequest(
    @SerializedName("page")
    val page: Int?,
    @SerializedName("size")
    val size: Int?,
    @SerializedName("transaction_id")
    val transaction_id: String?,
    @SerializedName("status")
    val status: List<String>?
)

data class StoreOrderList(
    @SerializedName("transaction_id")
    val transactionID: String?,
    @SerializedName("transaction_time")
    val transactionTime: String?,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("payment_with")
    val paymentWith: String?,
    @SerializedName("biz_type")
    val bizType: String?,
    @SerializedName("table_no")
    val tableNo: String?,
    @SerializedName("detail_products")
    val detailProduct: List<OrderDetailDetail>?
)

data class GetStoreOrderDetailResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: StoreOrderList?
) : Response

data class GetManualTransactionResp(
    @SerializedName("err_code")
    val errCode: String?,
    @SerializedName("err_message")
    val errMessage: String?,
    val results: List<ManualTransactionResult>?
)

data class ManualTransactionResult(
    @SerializedName("shipping")
    val shipping: ManualShippingResponse?,
    @SerializedName("customer")
    val customer: ManualCustomerResponse?,
    @SerializedName("productList")
    val productList: List<ManualProductListResponse>?,
    @SerializedName("transaction_id")
    val transaction_id: String?,
    @SerializedName("transaction_time")
    val transaction_time: String?,
    @SerializedName("order_id")
    val order_id: Int?,
    @SerializedName("mid")
    val mid: String?,
    @SerializedName("order_type")
    val order_type: String?,
    @SerializedName("order_status")
    val order_status: String?,
    @SerializedName("payment_status")
    val payment_status: String?,
    @SerializedName("order_platform")
    val order_platform: String?,
    @SerializedName("total_product_price")
    val total_product_price: Int?,
    @SerializedName("total_discount")
    val total_discount: Int?,
    @SerializedName("total_payment")
    val total_payment: Int?,
    @SerializedName("created_at")
    val created_at: String?,
    @SerializedName("updated_at")
    val updated_at: String?
)

data class ManualShippingResponse(
    @SerializedName("shipping_method")
    val shipping_method: String?,
    @SerializedName("shipping_cost")
    val shipping_cost: Int?,
    @SerializedName("shipping_time")
    val shipping_time: String?
)

data class ManualCustomerResponse(
    @SerializedName("address")
    val address: String?,
    @SerializedName("customer_id")
    val shipping_cost: Int?,
    @SerializedName("customer_name")
    val customer_name: String?,
    @SerializedName("phone_number")
    val phone_number: String?,
    @SerializedName("address_detail")
    val address_detail: String?
)

data class ManualProductListResponse(
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("price")
    val price: Int?,
    @SerializedName("discount")
    val discount: Int?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("extraPrice")
    val extraPrice: Int?,
    @SerializedName("product_id")
    val product_id: String?,
    @SerializedName("product_name")
    val product_name: String?
)

data class UpdateStatusManualTxnRequest(
    @SerializedName("customer_id")
    val customerId: String?,
    @SerializedName("mid")
    val mid: String?,
    @SerializedName("order_id")
    val orderId: Int?,
    @SerializedName("order_status")
    val orderStatus: String?,
    @SerializedName("payment_status")
    val paymentStatus: String?,
)

data class UpdateStatusManualResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?
): Response

data class UpdateOrderStatusModel(
    @SerializedName("transaction_id")
    val transactionID: String?,
    @SerializedName("status")
    val status: String?
)

data class UpdateStatusResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?
) : Response

