package com.tsab.pikapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.tsab.pikapp.models.model.MerchantProfileData
import com.tsab.pikapp.models.model.UserAccess

class SessionManager {

    companion object {

        private const val PREF_ISLOGGINGIN = "login"
        private const val PREF_LOGIN_HISTORY_TIME = "login history"
        private const val PREF_USER_TOKEN = "user token"
        private const val PREF_USER_DATA = "user data"
        private const val PREF_FIRST_USER = "first user"
        private const val PREF_PROFILE_UPDATE = "first profile update"
        private const val PREF_MERCHANT_DATA = "merchant data"
        private const val PREF_MERCHANT_DOB = "merchant dob"
        private const val PREF_MERCHANT_GENDER = "merchant gender"
        private const val PREF_HOME_NAV = "home nav"

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

    fun getUserData(): UserAccess? {
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

    //USER USING APP FOR FIRST TIME
    fun setFirstApp(intData: Int) {
        prefs?.edit(commit = true) {
            putInt(PREF_FIRST_USER, intData)
        }
    }

    fun getFirstApp() = prefs?.getInt(PREF_FIRST_USER, 0)

    //USER NEED TO UPDATE THEIR PROFILE FOR FIRST TIME
    fun setProfileNum(intData: Int) {
        prefs?.edit(commit = true) {
            putInt(PREF_PROFILE_UPDATE, intData)
        }
    }

    fun getProfileNum() = prefs?.getInt(PREF_PROFILE_UPDATE, 0)

    //SETTER GETTER MERCHANT PROFILE
    fun setMerchantProfile(merchantData: MerchantProfileData) {
        val jsonString = GsonBuilder().create().toJson(merchantData)
        prefs?.edit(commit = true) {
            putString(PREF_MERCHANT_DATA, jsonString)
        }

        if (getProfileNum() == 0) {
            prefs?.edit(commit = true) {
                putString(PREF_MERCHANT_DOB, "DEFAULT_DOB")
                putString(PREF_MERCHANT_GENDER, "DEFAULT_GENDER")
            }
        } else {
            prefs?.edit(commit = true) {
                putString(PREF_MERCHANT_DOB, merchantData.dateOfBirth)
                putString(PREF_MERCHANT_GENDER, merchantData.gender)
            }
        }
    }

    fun getMerchantProfile() : MerchantProfileData? {
        val merchantData = prefs?.getString(PREF_MERCHANT_DATA, null)
        return GsonBuilder().create().fromJson(merchantData, MerchantProfileData::class.java)
    }

    //SETTER AND GETTER DOB AND BIRTHDAY
    fun setDOBProfile(dob: String?) = prefs?.edit(commit = true) { putString(PREF_MERCHANT_DOB, dob) }
    fun setGenderProfile(gender: String?) = prefs?.edit(commit = true) { putString(PREF_MERCHANT_GENDER, gender) }
    fun getDOBProfile() = prefs?.getString(PREF_MERCHANT_DOB, null)
    fun getGenderProfile() = prefs?.getString(PREF_MERCHANT_GENDER, null)

    fun setHomeNav(intData: Int){
        prefs?.edit(commit = true) {
            putInt(PREF_HOME_NAV, intData)
        }
    }

    fun getHomeNav() = prefs?.getInt(PREF_HOME_NAV, 0)
}