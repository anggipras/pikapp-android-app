package com.tsab.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.tsab.pikapp.models.model.*

class SharedPreferencesUtil {

    companion object {

        private const val PREF_TIME = "Pref time"

        private const val PREF_ONBOARDING_FINISHED = "onboarding"

        private const val PREF_ISUSEREXCLUSIVE = "user exclusive"

        private const val PREF_ISUSEREXCLUSIVEFORM = "user exclusive form"

        private const val LONGITUDE = "longitude"

        private const val LATITUDE = "latitude"

        private const val DEEPLINK = "deeplink"

        private const val PREF_FCMTOKEN = "fcm token"

        private const val PREF_NOTIFICATION_DATA = "notification data"

        private const val PREF_ORDER_LIST_TAB_STATUS = "tab status order list"

        private const val PREF_ORDER_LIST = "order list customer"

        private const val PREF_STORE_ORDER_LIST = "order list merchant"

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SharedPreferencesUtil? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesUtil =
            instance ?: synchronized(LOCK) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): SharedPreferencesUtil {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesUtil()
        }
    }

    fun saveUpdateTime(time: Long) {
        prefs?.edit(commit = true) {
            putLong(PREF_TIME, time)
        }
    }

    fun getUpdateTime() = prefs?.getLong(PREF_TIME, 0)

    fun getCacheDuration() = prefs?.getString("pref_cache_duration", "")

    fun saveOnboardingFinised(boolean: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ONBOARDING_FINISHED, boolean)
        }
    }

    fun isOnboardingFinished() = prefs?.getBoolean(PREF_ONBOARDING_FINISHED, false)

    fun saveUserExclusive(boolean: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ISUSEREXCLUSIVE, boolean)
        }
    }

    fun isUserExclusive() = prefs?.getBoolean(PREF_ISUSEREXCLUSIVE, false)

    fun saveUserExclusiveForm(boolean: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ISUSEREXCLUSIVEFORM, boolean)
        }
    }

    fun isUserExclusiveFormFinished() = prefs?.getBoolean(PREF_ISUSEREXCLUSIVEFORM, false)

    fun saveLatestLocation(longitude: String, latitude: String) {
        prefs?.edit(commit = true) {
            putString(LONGITUDE, longitude)
            putString(LATITUDE, latitude)
        }
    }

    fun getLatestLocation(): LatestLocation {
        val latitude = prefs?.getString(LATITUDE, "")
        val longitude = prefs?.getString(LONGITUDE, "")
        return LatestLocation(longitude, latitude)
    }

    fun saveDeeplinkUrl(mid: String, tableNo: String?) {
        prefs?.edit(commit = true) {
            putString(DL_MERCHANTID, mid)
            tableNo?.let {
                putString(DL_TABLENO, tableNo)
            }
        }
        storeDeepLink(mid, tableNo)
    }

    fun deleteDeeplinkUrl() {
        prefs?.edit(commit = true) {
            putString(DL_MERCHANTID, "")
            putString(DL_TABLENO, "")
        }
    }

    fun getDeeplink(): DeepLinkModel {
        val mid = prefs?.getString(DL_MERCHANTID, "")
        val tableNo = prefs?.getString(DL_TABLENO, "")
        return DeepLinkModel(mid, tableNo)
    }

    fun storeDeepLink(mid: String, tableNo: String?) {
        val deeplLinkModel = DeepLinkModel(mid, tableNo)
        val json = Gson().toJson(deeplLinkModel)
        prefs?.edit(commit = true) {
            putString(DEEPLINK, json)
        }
    }

    fun getStoredDeepLink() : DeepLinkModel? {
        val gson = Gson()
        val json: String? = prefs?.getString(DEEPLINK, "")
        json?.let {
            if (json.isNotEmpty()) {
                val deepLinkModel: DeepLinkModel = gson.fromJson(json, DeepLinkModel::class.java)
                return deepLinkModel
            }
        }
        return null
    }

    fun deleteStoredDeepLink() {
        prefs?.edit(commit = true) {
            putString(DEEPLINK, "")
        }
    }

    fun setFcmToken(token: String) {
        prefs?.edit(commit = true) {
            putString(PREF_FCMTOKEN, token)
        }
    }

    fun getFcmToken() = prefs?.getString(PREF_FCMTOKEN, "")

    fun setNotificationDetail(notif: NotificationModel) {
        val json = Gson().toJson(notif)
        Log.d("Debug", "notif json : $json")
        prefs?.edit(commit = true) {
            putString(PREF_NOTIFICATION_DATA, json)
        }
    }

    fun getNotificationDetail(): NotificationModel? {
        val gson = Gson()
        val json: String? = prefs?.getString(PREF_NOTIFICATION_DATA, "")
        Log.d("Debug", "notif json : $json")
        json?.let {
            if (json.isNotEmpty()) {
                val notificationModel = gson.fromJson(json, NotificationModel::class.java)
                return notificationModel
            }
        }
        return null
    }

    fun deleteNotificationDetail() {
        prefs?.edit(commit = true) {
            putString(PREF_NOTIFICATION_DATA, "")
        }
    }

    fun setOrderListTabSelected(status: Int) {
        prefs?.edit(commit = true) {
            putInt(PREF_ORDER_LIST_TAB_STATUS, status)
        }
    }

    fun getOrderListTabSelected() = prefs?.getInt(PREF_ORDER_LIST_TAB_STATUS, 0)

    fun resetOrderListTabSelected() {
        prefs?.edit(commit = true) {
            putInt(PREF_ORDER_LIST_TAB_STATUS, 0)
        }
    }

    fun setOrderList(orderList: List<OrderList>) {
        val json = Gson().toJson(orderList)
        Log.d("Debug", "order list : $json")
        prefs?.edit(commit = true) {
            putString(PREF_ORDER_LIST, json)
        }
    }

    fun getOrderList(): List<OrderList>? {
        val gson = Gson()
        val json: String? = prefs?.getString(PREF_ORDER_LIST, "")
        Log.d("Debug", "order list get : $json")
        json?.let {
            if (json.isNotEmpty()) {
                val orderList: List<OrderList> = gson.fromJson(json, Array<OrderList>::class.java).toList()
                return orderList
            }
        }
        return null
    }

    fun clearOrderList() {
        prefs?.edit(commit = true) {
            putString(PREF_ORDER_LIST, "")
        }
    }

    fun setStoreOrderList(orderList: List<StoreOrderList>) {
        val json = Gson().toJson(orderList)
        Log.d("Debug", "order list : $json")
        prefs?.edit(commit = true) {
            putString(PREF_STORE_ORDER_LIST, json)
        }
    }

    fun getStoreOrderList(): List<StoreOrderList>? {
        val gson = Gson()
        val json: String? = prefs?.getString(PREF_STORE_ORDER_LIST, "")
        Log.d("Debug", "order list get : $json")
        json?.let {
            if (json.isNotEmpty()) {
                val orderList: List<StoreOrderList> = gson.fromJson(json, Array<StoreOrderList>::class.java).toList()
                return orderList
            }
        }
        return null
    }

    fun clearStoreOrderList() {
        prefs?.edit(commit = true) {
            putString(PREF_STORE_ORDER_LIST, "")
        }
    }

}