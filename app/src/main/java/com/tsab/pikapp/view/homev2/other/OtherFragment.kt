package com.tsab.pikapp.view.homev2.other

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.OtherFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.omni.integration.IntegrationActivity
import com.tsab.pikapp.view.other.OtherSettingsActivity
import com.tsab.pikapp.view.other.otherReport.ReportActivity
import com.tsab.pikapp.viewmodel.homev2.OtherViewModel
import kotlinx.android.synthetic.main.other_fragment.*

class OtherFragment : Fragment() {
    private lateinit var dataBinding: OtherFragmentBinding
    private lateinit var viewModel: OtherViewModel
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val sessionManager = SessionManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this).get(OtherViewModel::class.java)

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.other_fragment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = swipeOtherMenu
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getMerchantProfile(requireContext())
        }

        viewModel.getMerchantProfile(requireContext())
        viewModel.getMerchantShopStatus(requireActivity())
        dataBinding.merchantProfile = viewModel

        dataBinding.merchantSettingClick.setOnClickListener {
            Intent(activity?.baseContext, OtherSettingsActivity::class.java).apply {
                activity?.startActivity(this)
            }
        }

        dataBinding.integrasiButton.setOnClickListener {
            Intent(activity?.baseContext, IntegrationActivity::class.java).apply {
                activity?.startActivity(this)
            }
        }

        dataBinding.merchantReportClick.setOnClickListener {
            Intent(activity?.baseContext, ReportActivity::class.java).apply {
                activity?.startActivity(this)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.merchantResult.observe(viewLifecycleOwner, Observer { merchantProfile ->
            merchantProfile?.let {
                dataBinding.personName.text = merchantProfile.fullName.toString()
                Picasso.get().load(merchantProfile.merchantLogo).into(merchant_logo)
                dataBinding.merchantName.text = merchantProfile.merchantName.toString()
                dataBinding.merchantPhone.text = merchantProfile.phoneNumber.toString()
                dataBinding.merchantEmail.text = merchantProfile.email.toString()
                swipeRefreshLayout.isRefreshing = false
            }
        })

        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017") {
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
                    activity?.finishAffinity()
                }
            }
        })

        viewModel.merchantShopStatus.observe(viewLifecycleOwner, Observer { shopSchedule ->
            shopSchedule?.let {
                val merchantStatus = when (shopSchedule.dailyStatus) {
                    "OPEN" -> "Buka"
                    else -> "Tutup"
                }

                when (shopSchedule.dailyStatus) {
                    "OPEN" -> dataBinding.merchantStatus.setTextColor(Color.parseColor("#4BB7AC"))
                    else -> dataBinding.merchantStatus.setTextColor(Color.parseColor("#DC6A84"))
                }

                val merchantDay = when (shopSchedule.days) {
                    "MONDAY" -> "Senin"
                    "TUESDAY" -> "Selasa"
                    "WEDNESDAY" -> "Rabu"
                    "THURSDAY" -> "Kamis"
                    "FRIDAY" -> "Jumat"
                    "SATURDAY" -> "Sabtu"
                    else -> "Minggu"
                }

                dataBinding.merchantStatus.text = merchantStatus
                dataBinding.shopScheduleSeparator.visibility = View.VISIBLE

                if (shopSchedule.dailyStatus == "OPEN") {
                    val theHours =
                        if (shopSchedule.openTime == "00:00" && shopSchedule.closeTime == "23:59") {
                            "24 jam"
                        } else {
                            "${shopSchedule.openTime} - ${shopSchedule.closeTime}"
                        }
                    dataBinding.merchantHour.text =
                        getString(R.string.shop_daily_status, merchantDay, theHours)
                } else {
                    dataBinding.merchantHour.text = merchantDay
                }
            }
        })
    }
}