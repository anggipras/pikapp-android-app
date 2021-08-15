package com.tsab.pikapp.util

fun generateResponseMessage(err_Code: String?, err_Message: String?): String? {
    var responseMessage: String? = ""
    when (err_Code) {
        "EC0009" -> responseMessage = "Param not found"
        "EC0013" -> responseMessage = "Internal Server Error"
        "EC0014" -> responseMessage = "Email/Phone is used"
        "EC0015" -> responseMessage = "Nomor Telepon atau PIN salah"
        "EC0099" -> responseMessage = "User is suspended"
        "EC0016" -> responseMessage = "Client id not valid"
        "EC0017" -> responseMessage = "Token expired"
        "EC0018" -> responseMessage = "Signature invalid"
        "EC0019" -> responseMessage = "No slider available!"
        "EC0020" -> responseMessage = "No category available!"
        "EC0021" -> responseMessage = "Token invalid"
        "EC0022" -> responseMessage = "Merchant is not valid"
        "EC0023" -> responseMessage = "Transaction id is not valid"
        "EC0024" -> responseMessage = "Product id is not valid"
        "EC0025" -> responseMessage = "Wishlist is not available"
        "EC0026" -> responseMessage = "Product is not available"
        "EC0027" -> responseMessage = "Payment is not available"
        "EC0028" -> responseMessage = "No history available"
        "EC0029" -> responseMessage = "User is not active"
        "EC0030" -> responseMessage = "Payment is not available"
        "EC0031" -> responseMessage = "Merchant doesn't have template issued"
        "EC0032" -> responseMessage = "Service unauthorized, please contact admin"
        "EC0033" -> responseMessage = "Email/Nomor Telepon Yang Anda Gunakan Sudah Terpakai"
        "EC0034" -> responseMessage = "Customer is exist"
    }
    return responseMessage
}