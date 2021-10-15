package com.tsab.pikapp.view.omni.integration

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel

class IntegrationActivity : AppCompatActivity() {
    companion object {
        const val ARGUMENT_OMNICHANNEL = "omnichannel"
    }

    private val viewModel: IntegrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        setContentView(R.layout.activity_integration)

        viewModel.fetchIntegrationList()
    }
}