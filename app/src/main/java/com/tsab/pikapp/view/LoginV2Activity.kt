package com.tsab.pikapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SessionManager

class LoginV2Activity : AppCompatActivity() {
    private var sessionManager = SessionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_v2)
    }

    override fun onBackPressed() {
        val firstApp = sessionManager.getFirstApp()
        if (firstApp == 1) {
            finishAffinity()
        }
    }
}