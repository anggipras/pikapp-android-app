package com.bejohen.pikapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.util.DL_MERCHANTID
import com.bejohen.pikapp.util.DL_TABLENO
import com.bejohen.pikapp.viewmodel.home.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)

    }
}