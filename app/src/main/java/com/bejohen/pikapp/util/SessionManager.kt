package com.bejohen.pikapp.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.LoginActivity
import java.util.*


class SessionManager(context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context
    var PRIVATE_MODE = 0
    fun createSession(
        name: String?,
        email: String?,
        id: String?
    ) {
        editor.putBoolean(LOGIN, true)
        editor.putString(NAME, name)
        editor.putString(EMAIL, email)
        editor.putString(ID, id)
        editor.apply()
    }

    val isLoggin: Boolean
        get() = sharedPreferences.getBoolean(LOGIN, false)

    fun checkLogin() {
        if (!isLoggin) {
            val i = Intent(context, LoginActivity::class.java)
            context.startActivity(i)
            (context as HomeActivity).finish()
        }
    }

    val userDetail: HashMap<String, String?>
        get() {
            val user =
                HashMap<String, String?>()
            user[NAME] = sharedPreferences.getString(NAME, null)
            user[EMAIL] = sharedPreferences.getString(EMAIL, null)
            user[ID] = sharedPreferences.getString(ID, null)
            return user
        }

    fun logout() {
        editor.clear()
        editor.commit()
        val i = Intent(context, LoginActivity::class.java)
        context.startActivity(i)
        (context as HomeActivity).finish()
    }

    companion object {
        private const val PREF_NAME = "LOGIN"
        private const val LOGIN = "IS_LOGIN"
        const val NAME = "NAME"
        const val EMAIL = "EMAIL"
        const val ID = "ID"
    }

    init {
        this.context = context
        sharedPreferences =
            context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }
}