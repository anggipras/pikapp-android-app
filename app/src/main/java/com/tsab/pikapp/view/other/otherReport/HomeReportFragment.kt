package com.tsab.pikapp.view.other.otherReport

import android.content.Intent
import android.os.Bundle
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
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeReportFragment : Fragment() {
    private lateinit var dataBinding: HomeReportFragmentBinding
    private val viewModel: ReportViewModel by activityViewModels()
    private val sessionManager = SessionManager()
    private var startDateISO = ""
    private var endDateISO = ""
    private val dateFormatISO = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)

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

        dateFormatISO.timeZone = TimeZone.getDefault()
        val mid = sessionManager.getUserData()!!.mid!!
        if (viewModel.dateSelection.value.isNullOrEmpty()) {
            val id = Locale("in", "ID")
            val dateNow = SimpleDateFormat("EEEE, d MMMM yyyy", id).format(Date())
            dataBinding.dateChoice.text = "Hari ini"
            dataBinding.dateSelection.text = dateNow
            val cal = Calendar.getInstance()
            endDateISO = dateFormatISO.format(cal.time)

            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            startDateISO = dateFormatISO.format(cal.time)
            webViewSetup(startDateISO, endDateISO, mid)
        } else {
            dataBinding.dateChoice.text = viewModel.dateSelection.value
            if (viewModel.dateSelection.value == "Hari ini" || viewModel.dateSelection.value == "Kemarin" || viewModel.dateSelection.value == "2 Hari yang lalu") {
                dataBinding.dateSelection.text = viewModel.endDate.value
            } else {
                dataBinding.dateSelection.text = "${viewModel.startDate.value} s/d ${viewModel.endDate.value}"
            }
            webViewSetup(viewModel.startISO.value!!, viewModel.endISO.value!!, mid)
        }

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

    private fun webViewSetup(startDate: Any, endDate: Any, mid: String) {
        dataBinding.reportWebView.webViewClient = WebViewClient()

        val reportApi = PikappApiService().webReport()
        dataBinding.reportWebView.apply {
            loadUrl("${reportApi}report/generate?startdate=${startDate}&enddate=${endDate}&mid=${mid}")
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
        }
    }

}
