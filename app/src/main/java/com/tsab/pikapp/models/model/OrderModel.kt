package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class GetOrderListResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: List<OrderList>?
) : Response

data class OrderList(
    @SerializedName("transaction_id")
    val transactionID: String?,
    @SerializedName("transaction_time")
    val transactionTime: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("payment_with")
    val paymentWith: String?,
    @SerializedName("biz_type")
    val bizType: String?,
    @SerializedName("total_price")
    val totalPrice: Long?,
    @SerializedName("total_product")
    val totalProduct: String?
)

//detail (user)
data class GetOrderDetailResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: OrderDetail?
) : Response

data class OrderDetail(
    @SerializedName("payment_with")
    val paymentWith: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("transaction_id")
    val transactionID: String?,
    @SerializedName("transaction_time")
    val transactionTime: String?,
    @SerializedName("merchant_name")
    val merchantName: String?,
    @SerializedName("total_price")
    val price: Long?,
    @SerializedName("biz_type")
    val bizType: String?,
    @SerializedName("table_no")
    val tableNo: String?,
    @SerializedName("detail_products")
    val products: List<OrderDetailDetail>?
)

data class OrderDetailDetail(
    @SerializedName("pid")
    val productID: String?,
    @SerializedName("image")
    val productImage: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("notes")
    val productNote: String?,
    @SerializedName("qty")
    val productQty: Int?,
    @SerializedName("price")
    val productPrice: Int?
)

//Omnichannel order
data class ListOrderOmni(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: List<OrderDetailOmni>?
) : Response

data class OrderDetailOmni(
    @SerializedName("order_id")
    val orderId: String?,
    @SerializedName("invoice_no")
    val invoiceNo: String?,
    @SerializedName("shop_id")
    val shopId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("order_time")
    val orderTime: String?,
    @SerializedName("channel")
    val channel: String?,
    @SerializedName("product_details")
    val producDetails: List<ProductDetailOmni>?
)

data class ProductDetailOmni(
    @SerializedName("name")
    val name: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("sku")
    val sku: String?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("weight")
    val weight: Float?,
    @SerializedName("total_weight")
    val totalWeight: Float?,
    @SerializedName("price")
    val price: Long?,
    @SerializedName("total_price")
    val totalPrice: Long?,
    @SerializedName("currency")
    val currency: String?
)

data class ListOrderDetailOmni(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: OrderDetailDetailOmni?
) : Response

data class OrderDetailDetailOmni(
    @SerializedName("order_id")
    val orderId: String?,
    @SerializedName("invoice_no")
    val invoiceNo: String?,
    @SerializedName("shop_id")
    val shopId: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("order_time")
    val orderTime: String?,
    @SerializedName("channel")
    val channel: String?,
    @SerializedName("product_details")
    val producDetails: List<ProductDetailOmni>?,
    @SerializedName("amount")
    val amount: AmountDetailOmni?,
    @SerializedName("logistics")
    val logistics: LogisticsDetailOmni?
)

data class AmountDetailOmni(
    @SerializedName("total_amount")
    val totalAmount: Long?,
    @SerializedName("total_product_price")
    val totalProductPrice: Long?,
    @SerializedName("insurance_cost")
    val insuranceCost: Long?,
    @SerializedName("voucher")
    val voucher: Long?,
    @SerializedName("toppoints_amount")
    val toppointsAmount: Long?,
    @SerializedName("shipping_cost")
    val shippingCost: Long?
)

data class LogisticsDetailOmni(
    @SerializedName("geo")
    val geo: String?,
    @SerializedName("shipping_agency")
    val shippingAgency: String?,
    @SerializedName("service_type")
    val serciveType: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("province")
    val province: String?,
    @SerializedName("country")
    val country: String?,
    @SerializedName("postal_code")
    val postalCode: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("recipient_name")
    val recipientName: String?
)

data class AcceptOrderTokopediaRequest(
    @SerializedName("channel")
    var channel: String? = null,
    @SerializedName("order_id")
    var orderId: String? = null,
    @SerializedName("mid")
    var mid: String? = null
)

data class AcceptOrderTokopediaResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    val results: List<AcceptOrderTokopediaResult>?
) : Response

data class AcceptOrderTokopediaResult(
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("error_code")
    val errorCode: String?
)
