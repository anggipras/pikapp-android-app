package com.tsab.pikapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.SplashViewModel

class OrderListActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        viewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)

        if(intent != null && intent.hasExtra("is_merchant")) {
            val isMerchant = intent.extras!!.getString("is_merchant")
            val transactionID = intent.extras!!.getString("transaction_id")
            val tableNo = intent.extras!!.getString("table_no")
            viewModel.saveNotification(isMerchant!!, transactionID!!, tableNo!!)
        }
    }
}