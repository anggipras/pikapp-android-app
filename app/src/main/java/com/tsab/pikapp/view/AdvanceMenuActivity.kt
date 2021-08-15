package com.tsab.pikapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.menu.advance.AdvanceMenuViewModel

class AdvanceMenuActivity : AppCompatActivity() {
    companion object {
        // Intent extras identifier.
        const val PRODUCT_ID = "PRODUCT_ID"
        const val RESULT_DATA = "RESULT_DATA"
    }

    private val viewModel: AdvanceMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_menu)

        // Set product ID in view model.
        val productId = intent.getStringExtra(PRODUCT_ID)
        viewModel.setProductId(productId)
        viewModel.fetchAdvanceMenuData()

        // Send result when push success.
        viewModel.isPushSuccess.observe(this, Observer { isPushSuccess ->
            if (isPushSuccess) {
                Intent().apply {
                    putExtra(RESULT_DATA, viewModel.advanceMenuList.value.toString())
                    setResult(RESULT_OK, this)
                    finish()
                }
            }
        })
    }
}