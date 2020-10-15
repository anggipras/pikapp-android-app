package com.bejohen.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.bejohen.pikapp.models.model.CartModel
import com.google.gson.Gson

class CartUtil {

    companion object {

        private const val PREF_CART = "cart"
        private const val PREF_ISCARTON = "is cart"

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
}