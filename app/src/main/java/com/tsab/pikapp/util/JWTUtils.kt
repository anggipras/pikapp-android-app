package com.tsab.pikapp.util

import android.util.Log
import com.tsab.pikapp.models.model.UserAccess
import com.google.gson.Gson
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts

fun decodeJWT(jwt: String): UserAccess {
    var sub = ""
    var expired: Long = 0
    try {
        val jws: Jws<Claims?> = Jwts.parserBuilder()
            .setSigningKey(JWT_SECRET)
            .build()
            .parseClaimsJws(jwt)
        sub = jws.body?.get("sub").toString()
        expired = jws.body?.get("exp").toString().toLong()
    } catch (ex: JwtException) {
        Log.d("debug", "gagal extract token ${ex}")
    }
    Log.d("debug", "isi token : ${sub}")
    val convertedObject: UserAccess = Gson().fromJson(sub, UserAccess::class.java)
    convertedObject.expired = expired * 1000
    Log.d("debug", "phone number : ${convertedObject.phoneNumber}")
    Log.d("debug", "expired date : ${convertedObject.expired}")

    return convertedObject
}