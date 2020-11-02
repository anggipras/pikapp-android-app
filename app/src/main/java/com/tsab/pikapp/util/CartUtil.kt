package com.tsab.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.tsab.pikapp.models.model.CartModel
import com.google.gson.Gson

class CartUtil {

    companion object {

        private const val PREF_CART = "cart"
        private const val PREF_ISCARTON = "is cart"
        private const val PREF_CARTTYPE = "cart type"
        private const val PREF_ISNEWCART = "new cart"

        private const val PREF_PAYMENTTYPE = "payment type"

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: CartUtil? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): CartUtil =
            instance ?: synchronized(LOCK) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): CartUtil {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return CartUtil()
        }
    }

    fun setCartStatus(status: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ISCARTON, status)
        }
    }

    fun getCartStatus() = prefs?.getBoolean(PREF_ISCARTON, false)

    fun setCart(cartList: List<CartModel>) {
        val json = Gson().toJson(cartList)
        Log.d("Debug", "JSOn : $json")
        prefs?.edit(commit = true) {
            putString(PREF_CART, json)
        }
    }

    fun setCartEmpty() {
        prefs?.edit(commit = true) {
            putString(PREF_CART, "")
            putBoolean(PREF_ISCARTON, false)
            putString(PREF_CARTTYPE, "")
        }
    }

    fun getCart(): List<CartModel>? {
        val gson = Gson()
        val json: String? = prefs?.getString(PREF_CART, "")
        Log.d("Debug", "JSOn2 : $json")
        json?.let {
            if (json.isNotEmpty()) {
                val cartList: List<CartModel> = gson.fromJson(json, Array<CartModel>::class.java).toList()
                return cartList
            }
        }
        return null
    }

    fun setCartType(status: String) {
        prefs?.edit(commit = true) {
            putString(PREF_CARTTYPE, status)
        }
    }

    fun getCartType() = prefs?.getString(PREF_CARTTYPE, "")

    fun setCartIsNew(status: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ISNEWCART, status)
        }
    }

    fun getCartIsNew() = prefs?.getBoolean(PREF_ISNEWCART, false)

    fun setPaymentType(paymentType: String) {
        prefs?.edit(commit = true) {
            putString(PREF_PAYMENTTYPE, paymentType)
        }
    }

    fun getPaymentType() = prefs?.getString(PREF_PAYMENTTYPE, "WALLET_OVO")
}