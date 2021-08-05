package com.tsab.pikapp.view.loginv2

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tsab.pikapp.databinding.ActivityLoginRegisterBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.RegisterV2Activity

class LoginRegisterActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityLoginRegisterBinding
    private var sessionManager = SessionManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = ActivityLoginRegisterBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)

        dataBinding.masukButtonAuth.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        dataBinding.masukButtonAuth.setOnClickListener {
            val intent = Intent(this, LoginV2Activity::class.java)
            startActivity(intent)
        }

        dataBinding.daftarButtonAuth.setOnClickListener {
            val intent = Intent(this, RegisterV2Activity::class.java)
            startActivity(intent)
        }

        dataBinding.privacyTextAuth.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://pikapp.id/ketentuan-dan-kebijakan-privasi/"))
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val firstApp = sessionManager.getFirstApp()

        if (firstApp == 1) {
            finishAffinity()
        }
    }
}