package com.tsab.pikapp.models.model

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
