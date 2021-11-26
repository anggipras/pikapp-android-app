package com.tsab.pikapp.view.homev2.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebViewClient
import com.tsab.pikapp.R
import com.tsab.pikapp.models.network.PikappApiService
import kotlinx.android.synthetic.main.activity_web_menu.*

class WebMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_menu)
        val midStore = intent.getStringExtra("MID")
        webViewSetup(midStore)

        menuWeb_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun webViewSetup(mid: String) {
        menuWebView.webViewClient = WebViewClient()
        menuWebView.apply {
            val menuWebApi = PikappApiService().menuWeb()
            loadUrl("${menuWebApi}store?mid=${mid}")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }
}