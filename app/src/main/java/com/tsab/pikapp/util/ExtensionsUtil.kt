package com.tsab.pikapp.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tsab.pikapp.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
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

@BindingAdapter("android:imageUri")
fun setImageUri(view: ImageView, userImage: Uri?) {
    view.setImageURI(userImage)
}

@BindingAdapter("android:imageUrl")
fun loadImage(view: ImageView, url: String?) {
    view.loadImage(url, getProgressDrawable(view.context))
}

fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Check if a String is a valid Pikapp PIN.
 * @return true if the pin has length 6 and contains repeating number.
 */
fun String.isPinValid(): Boolean {
    if (this.isEmpty() || this.isBlank() || this.trim().length != 6) return false

    for (i in 0..5) if (this.count { it == this[i] } > 1) return true
    return false
}

fun detectSpecialCharacter(string: String): Boolean {
    val p: Pattern = Pattern.compile("[^a-zA-Z0-9 ]", Pattern.CASE_INSENSITIVE)
    val m: Matcher = p.matcher(string)
    val b: Boolean = m.find()
    return b
}

fun containsForbiddenCharacter(string: String): Boolean {
    val p: Pattern = Pattern.compile("[<>\"'=;()]", Pattern.CASE_INSENSITIVE)
    val m: Matcher = p.matcher(string)
    val b: Boolean = m.find()
    return b
}

fun String.isPasswordValid(): Boolean {
    return !TextUtils.isEmpty(this)
}

fun isPhoneValid(string: String): Boolean {
    if (string[0] == '0' && string[1] == '8') {
        return true
    }
    return false
}

fun getUUID(): String {
    val uuid = UUID.randomUUID().toString()
    val trimmedUuid = uuid.replace("-", "")
    return trimmedUuid
}

@SuppressLint("SimpleDateFormat")
fun getTimestamp(): String {
    val timeStamp: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Date())
    return timeStamp
}

@SuppressLint("SimpleDateFormat")
fun getHour(): String {
    val hour: String = SimpleDateFormat("HH:mm").format(Date())
    return hour
}

fun getDay(): String{
    val day: String = SimpleDateFormat("EEEE").format(Date())
    return day
}

fun getClientID(): String {
    val clientId = BuildConfig.CLIENT_ID
    return clientId
}

fun getClientSecret(): String {
    val clientSecret = BuildConfig.CLIENT_SECRET
    return clientSecret
}

fun setTokenPublic(): String {
    val token: String = "PUBLIC"
    return token
}

fun getSignature(email: String?, timestamp: String): String {

    val byte: ByteArray = getClientSecret().toByteArray(charset("UTF-8"))
    val sk: Key = SecretKeySpec(byte, "HmacSHA256")
    val mac = Mac.getInstance(sk.algorithm)
    val mess = "${getClientID()}:${email}:${getClientSecret()}:${timestamp}"
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
        for (x in 0..1) {
            initial = "${initial}${parts[x].substring(0, 1)}"
        }
    } else {
        initial = parts[0].substring(0, 1)
    }
    return initial.toUpperCase()
}

fun rupiahFormat(price: Long): String {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    return formatRupiah.format(price)
}

private const val TAG = "FileSaver"

@SuppressLint("SimpleDateFormat")
fun saveUriToFile(context: Context, uri: Uri?): File? {
    val format = SimpleDateFormat("YYYY_MM_dd_HH_mm")
    val imageDate = format.format(Date())
    val file = File(context.filesDir, "/img_$imageDate.jpg")
    return try {
        val input: InputStream? = context.contentResolver.openInputStream(uri!!)
        val bitmap = BitmapFactory.decodeStream(input)
        val output = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, output)
        output.flush()
        output.close()
        Log.v(TAG, "File path: " + file.absolutePath)
        file
    } catch (e: IOException) {
        Log.v(TAG, "Error" + e.message)
        null
    }
}

fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

fun substringPhone(str: String?): String? {
    var str = str
    if (str != null && str.length > 0) {
        str = str.substring(0, str.length - 3)
    }
    str = str + "xxx"
    return str
}

fun isUsernameValid(str: String?): Boolean {

    //TODO : check only allow for digit
    return true
}

fun Group.setAllOnClickListener(listener: View.OnClickListener?, view: View? = null) {
    referencedIds.forEach { id ->
        (view ?: rootView).findViewById<View>(id).setOnClickListener(listener)
    }
}
