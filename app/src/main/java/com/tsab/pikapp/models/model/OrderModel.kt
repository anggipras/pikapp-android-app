package com.tsab.pikapp.models.model

import com.google.gson.annotations.SerializedName

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