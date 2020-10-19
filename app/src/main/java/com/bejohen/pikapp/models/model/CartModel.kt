package com.bejohen.pikapp.models.model

import com.google.gson.annotations.SerializedName

data class AddToCartModel(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("pid")
    val productID: String?,
    @SerializedName("qty")
    val qty: String?,
    @SerializedName("notes")
    val notes: String?
)

data class AddToCartResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?) : Response

data class CartListResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    @SerializedName("results")
    val results: List<CartModel>?
) : Response

data class CartModel(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("pid")
    val productID: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("product_image")
    val productImage: String?,
    @SerializedName("qty")
    var qty: Int?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("price")
    val price: Int?,
    var totalPrice: Int? = (qty!! * price!!)
)

data class TransactionModel(
    @SerializedName("mid")
    val merchantID: String?,
    @SerializedName("prices")
    val totalPrices: String?,
    @SerializedName("payment_with")
    val paymentWith: String?,
    @SerializedName("biz_type")
    val bizType: String?,
    @SerializedName("table_no")
    val tableNo: String?,
    @SerializedName("products")
    val products: List<CartTxnModel>?
)

data class CartTxnModel(
    @SerializedName("product_id")
    val productID: String?,
    @SerializedName("notes")
    val notes: String?,
    @SerializedName("qty")
    val qty: Int?
)

data class TransactionResponse(
    @SerializedName("err_code")
    override val errCode: String?,
    @SerializedName("err_message")
    override val errMessage: String?,
    @SerializedName("results")
    val results: List<TransactionResponseDetail>?
) : Response

data class TransactionResponseDetail(
    @SerializedName("transaction_id")
    val transactionID: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("checkout_url")
    val checkoutUrl: String?
)