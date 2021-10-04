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

