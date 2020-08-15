package com.bejohen.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SharedPreferencesUtil {

    companion object {

        private const val PREF_TIME = "Pref time"

        private const val PREF_ONBOARDING_FINISHED = "onboarding"

        private const val PREF_ISLOGGINGIN = "login"

        private const val PREF_USER_TOKEN = "user token"

        private const val PREF_ISUSEREXCLUSIVE = "user exclusive"
        private const val PREF_ISUSEREXCLUSIVEFORM = "user exclusive form"

        private const val PREF_LOGIN_HISTORY_TIME = "login history"

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


    private fun saveUserLogin(boolean: Boolean) {
        prefs?.edit(commit = true) {
            putBoolean(PREF_ISLOGGINGIN, boolean)
        }
    }

    fun isLoggingIn() = prefs?.getBoolean(PREF_ISLOGGINGIN, false)

    private fun saveUserToken(token: String) {
        prefs?.edit(commit = true) {
            putString(PREF_USER_TOKEN, token)
        }
    }

    fun getUserToken() = prefs?.getString(PREF_USER_TOKEN, "")

    fun deleteUserToken() {
        prefs?.edit(commit = true) {
            putString(PREF_USER_TOKEN, "")
        }
    }

    private fun saveLoginHistory(time: Long) {
        prefs?.edit(commit = true) {
            putLong(PREF_LOGIN_HISTORY_TIME, time)
        }
    }

    fun getLoginHistory() = prefs?.getLong(PREF_LOGIN_HISTORY_TIME, 0)

    fun setUserSession(token: String, time: Long) {
        saveUserLogin(true)
        saveUserToken(token)
        saveLoginHistory(time)
    }

    fun logout() {
        saveUserLogin(false)
        deleteUserToken()
    }


}