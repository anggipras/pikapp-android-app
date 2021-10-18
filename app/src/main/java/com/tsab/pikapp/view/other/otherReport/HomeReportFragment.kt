package com.tsab.pikapp.view.other.otherReport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.other.ReportViewModel

class HomeReportFragment : Fragment() {
    private lateinit var viewModel: ReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_report_fragment, container, false)
    }

}