package com.bejohen.pikapp.util

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.*

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
    return !TextUtils.isEmpty(this)
//    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPasswordValid(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun getUUID() : String {
    val uuid = UUID.randomUUID().toString()
    return uuid
}

fun getTime() : String {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    return timeStamp
}

fun getClientID() : String {
    val clientId: String = "123"
    return clientId
}

