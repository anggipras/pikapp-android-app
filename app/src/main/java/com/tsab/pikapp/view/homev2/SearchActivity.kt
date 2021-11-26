package com.tsab.pikapp.view.homev2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tsab.pikapp.R

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        setContentView(R.layout.activity_search)
    }
}