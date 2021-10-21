package com.tsab.pikapp.view.other.otherReport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import kotlinx.android.synthetic.main.home_report_fragment.*

class HomeReportFragment : Fragment() {
    private lateinit var viewModel: ReportViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_report_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        navReport.setOnClickListener {
            navController.navigate(R.id.action_homeReportFragment_to_filterPageReport)
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
        }
    }

}