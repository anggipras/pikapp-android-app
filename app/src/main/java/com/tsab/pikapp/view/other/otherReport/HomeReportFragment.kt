package com.tsab.pikapp.view.other.otherReport

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.HomeReportFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class HomeReportFragment : Fragment() {
    private lateinit var dataBinding: HomeReportFragmentBinding
    private val viewModel: ReportViewModel by activityViewModels()
    private val sessionManager = SessionManager()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = HomeReportFragmentBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.buttonUploadReport.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeReportFragment_to_uploadReportFragment)
        }

        sessionManager.setHomeNav(3)
        dataBinding.backButton.setOnClickListener {
            Intent(activity?.baseContext, HomeActivity::class.java).apply {
                startActivity(this)
                activity?.finish()
            }
        }

        dataBinding.backImage.setColorFilter(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.textSubtle
                ), android.graphics.PorterDuff.Mode.SRC_IN
        )
        dataBinding.downArrow.setColorFilter(
                ContextCompat.getColor(
                        requireContext(),
                        R.color.colorSecondary
                ), android.graphics.PorterDuff.Mode.SRC_IN
        )

        dataBinding.downArrow.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_homeReportFragment_to_filterPageReport)
        }

        if (viewModel.dateSelection.value.isNullOrEmpty()) {
            val id = Locale("in", "ID")
            val dateNow = SimpleDateFormat("EEEE, d MMMM yyyy", id).format(Date())
            dataBinding.dateChoice.text = "Hari ini"
            dataBinding.dateSelection.text = dateNow
        } else {
            dataBinding.dateChoice.text = viewModel.dateSelection.value
            if (viewModel.dateSelection.value == "Hari ini" || viewModel.dateSelection.value == "Kemarin") {
                dataBinding.dateSelection.text = viewModel.endDate.value
            } else {
                dataBinding.dateSelection.text = "${viewModel.startDate.value} s/d ${viewModel.endDate.value}"
            }
        }

//        val calendar = Calendar.getInstance()
//        calendar.set(2021, 10, 15, 0, 0, 0)
//        val timestamp = Timestamp(calendar.timeInMillis)
//        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", id).format(timestamp)

        val startDate = "M00000150"
        val endDate = "7"

        webViewSetup(startDate, endDate)
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(
                        true
                ) {
                    override fun handleOnBackPressed() {
                        val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                        activity?.startActivity(intent)
                        activity?.finish()
                    }
                })
    }

    private fun webViewSetup(startDate: Any, endDate: Any) {
        dataBinding.reportWebView.webViewClient = WebViewClient()

        dataBinding.reportWebView.apply {
            loadUrl("https://web-dev.pikapp.id/merchant/${startDate}/${endDate}")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

}
