package com.tsab.pikapp.view.other.otherReport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.other.ReportViewModel

class ReportActivity : AppCompatActivity() {
    val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        setContentView(R.layout.activity_report)
    }
}