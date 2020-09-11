package com.bejohen.pikapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bejohen.pikapp.BuildConfig
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.security.Key
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
    val options = RequestOptions()
        .placeholder(progressDrawable)
    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(uri)
        .into(this)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun detectSpecialCharacter(string: String) : Boolean {
    val p: Pattern = Pattern.compile("[^a-zA-Z0-9 ]", Pattern.CASE_INSENSITIVE)
    val m: Matcher = p.matcher(string)
    val b: Boolean = m.find()
    return b
}

fun containsForbiddenCharacter(string: String) : Boolean {
    val p: Pattern = Pattern.compile("[<>\"'=;()]", Pattern.CASE_INSENSITIVE)
    val m: Matcher = p.matcher(string)
    val b: Boolean = m.find()
    return b
}

fun String.isPasswordValid(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun getUUID() : String {
    val uuid = UUID.randomUUID().toString()
    val trimmedUuid = uuid.replace("-", "")
    return trimmedUuid
}

@SuppressLint("SimpleDateFormat")
fun getTimestamp() : String {
    val timeStamp: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date())
    return timeStamp
}

fun getClientID() : String {
    val clientId = BuildConfig.CLIENT_ID
    return clientId
}

fun getClientSecret() : String {
    val clientSecret = BuildConfig.CLIENT_SECRET
    return clientSecret
}

fun setTokenPublic() : String {
    val token: String = "PUBLIC"
    return token
}

fun getSignature(email: String, timestamp: String) : String {

    val byte: ByteArray = getClientSecret().toByteArray(charset("UTF-8"))
    val sk: Key = SecretKeySpec(byte, "HmacSHA256")
    val mac = Mac.getInstance(sk.getAlgorithm())
    val mess = "${getClientID()}:${email}:${getClientSecret()}:${timestamp}"
    Log.d("debug", "kenapa ni : ${mess}")
    mac.init(sk)
    val hmac = mac.doFinal(mess.toByteArray(charset("UTF-8")))
    return toHexString(hmac)
}

fun toHexString(bytes: ByteArray): String {
    val sb = StringBuilder(bytes.size * 2)
    val formatter = Formatter(sb)
    for (b in bytes) {
        formatter.format("%02x", b)
    }
    return sb.toString()
}

fun getInitial(string: String): String {
    var initial = ""
    val parts: List<String> = string.split(" ")
    if (parts.count() > 1) {
        for(x in 0..1) {
            initial = "${initial}${parts[x].substring(0, 1)}"
        }
    } else {
        initial = parts[0].substring(0, 1)
    }
    return initial.toUpperCase()
}

fun rupiahFormat(price: Int): String {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(price)
}