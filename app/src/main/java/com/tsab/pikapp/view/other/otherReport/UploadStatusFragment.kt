package com.tsab.pikapp.view.other.otherReport

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.databinding.FragmentUploadReportBinding
import com.tsab.pikapp.databinding.FragmentUploadStatusBinding
import com.tsab.pikapp.viewmodel.other.ReportViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class UploadStatusFragment : Fragment() {

    private lateinit var dataBinding: FragmentUploadStatusBinding
    private lateinit var viewModel: ReportViewModel
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dataBinding = FragmentUploadStatusBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabs()

        navController = Navigation.findNavController(view)

        dataBinding.backButton.setOnClickListener {
            navController?.navigateUp()
        }

    }

    private fun setUpTabs() {
        val adapter = activity?.let { UploadStatusTabAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(GofoodReportStatusFragment(), "GoFood")
            adapter.addFragment(ShopeeReportStatusFragment(), "ShopeeFood")

            dataBinding.viewPager.adapter = adapter
            tabs.setupWithViewPager(dataBinding.viewPager)
        }
    }

}