package com.tsab.pikapp.view.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class OtherSettingsActivity : AppCompatActivity() {

    val viewModel: OtherSettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_settings)
    }
}