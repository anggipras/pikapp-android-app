package com.tsab.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.tsab.pikapp.models.model.DeepLinkModel
import com.tsab.pikapp.models.model.LatestLocation
import com.google.gson.Gson

class SharedPreferencesUtil {

    companion object {

        private const val PREF_TIME = "Pref time"

        private const val PREF_ONBOARDING_FINISHED = "onboarding"

        private const val PREF_ISUSEREXCLUSIVE = "user exclusive"

        private const val PREF_ISUSEREXCLUSIVEFORM = "user exclusive form"

        private const val LONGITUDE = "longitude"

        private const val LATITUDE = "latitude"

        private const val DEEPLINK = "deeplink"

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

}