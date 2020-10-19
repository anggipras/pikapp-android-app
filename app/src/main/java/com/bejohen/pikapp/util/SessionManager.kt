package com.bejohen.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.bejohen.pikapp.models.model.UserAccess
import com.google.gson.GsonBuilder

class SessionManager {

    companion object {

        private const val PREF_ISLOGGINGIN = "login"
        private const val PREF_LOGIN_HISTORY_TIME = "login history"
        private const val PREF_USER_TOKEN = "user token"
        private const val PREF_USER_DATA = "user data"

        private var prefs: SharedPreferences? = null

        @Volatile
        private var instance: SessionManager? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SessionManager =
            instance ?: synchronized(LOCK) {
                instance ?: buildHelper(context).also {
                    instance = it
                }
            }

        private fun buildHelper(context: Context): SessionManager {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SessionManager()
        }
    }

    fun saveUserLogin(boolean: Boolean) {
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

    private fun saveLoginHistory(time: Long) {
        prefs?.edit(commit = true) {
            putLong(PREF_LOGIN_HISTORY_TIME, time)
        }
    }

    fun getLoginHistory() = prefs?.getLong(PREF_LOGIN_HISTORY_TIME, 0)

    private fun saveUserData(userData: UserAccess) {
        val jsonString = GsonBuilder().create().toJson(userData)
        prefs?.edit(commit = true) {
            putString(PREF_USER_DATA, jsonString)
        }
    }

    fun getUserData() : UserAccess? {
        val userData = prefs?.getString(PREF_USER_DATA, null)
        return GsonBuilder().create().fromJson(userData, UserAccess::class.java)
    }

    fun setUserSession(token: String, time: Long, userData: UserAccess) {
        saveUserLogin(true)
        saveUserToken(token)
        saveLoginHistory(time)
        saveUserData(userData)
    }

    fun logout() {
        deleteUserData()
    }

    private fun deleteUserData() {
        prefs?.edit(commit = true) {
            putString(PREF_USER_TOKEN, "") //delete token
            putBoolean(PREF_ISLOGGINGIN, false) //logging in false
            putString(PREF_USER_DATA, "") //delete data
        }
    }
}