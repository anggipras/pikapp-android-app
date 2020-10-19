package com.bejohen.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.bejohen.pikapp.models.model.TransactionResponseDetail
import com.google.gson.Gson

class TransactionUtil {

    companion object {

        private const val PREF_ACTIVE_TRANSACTION = "active transaction"

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: TransactionUtil? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): TransactionUtil =
            instance ?: synchronized(LOCK) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): TransactionUtil {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return TransactionUtil()
        }
    }

    fun setTransactionActive(txnList: List<TransactionResponseDetail>) {
        val json = Gson().toJson(txnList)
        Log.d("Debug", "JSOn : $json")
        prefs?.edit(commit = true) {
            putString(PREF_ACTIVE_TRANSACTION, json)
        }
    }

    fun setTransactionActiveEmpty() {
        prefs?.edit(commit = true) {
            putString(PREF_ACTIVE_TRANSACTION, "")
        }
    }

    fun getTransactionActive(): List<TransactionResponseDetail>? {
        val gson = Gson()
        val json: String? = prefs?.getString(PREF_ACTIVE_TRANSACTION, "")
        Log.d("Debug", "transaction active json : $json")
        json?.let {
            if (json.isNotEmpty()) {
                val txnList : List<TransactionResponseDetail> = gson.fromJson(json, Array<TransactionResponseDetail>::class.java).toList()
                return txnList
            }
        }
        return null
    }

}