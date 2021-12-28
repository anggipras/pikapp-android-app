package com.tsab.pikapp.view.homev2.other

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.OtherFragmentBinding
import com.tsab.pikapp.models.model.TutorialGetResponse
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.omni.integration.IntegrationActivity
import com.tsab.pikapp.view.other.OtherSettingsActivity
import com.tsab.pikapp.view.other.otherReport.ReportActivity
import com.tsab.pikapp.viewmodel.homev2.OtherViewModel
import com.tsab.pikapp.viewmodel.homev2.TutorialViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import kotlinx.android.synthetic.main.other_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import smartdevelop.ir.eram.showcaseviewlib.GuideView

class OtherFragment : Fragment() {
    private lateinit var dataBinding: OtherFragmentBinding
    private lateinit var viewModel: OtherViewModel
    private val viewModel1: TutorialViewModel by activityViewModels()
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

        getMerchProfileData()
        general_error_other.try_button.setOnClickListener {
            getMerchProfileData()
        }
        swipeRefreshLayout = swipeOtherMenu
        swipeRefreshLayout.setOnRefreshListener {
            getMerchProfileData()
        }

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

        getTutorial("TUTORIAL_OTHERS")
        /*ShowIntro("Merchant Info", "Tombol lainnya digunakan untuk mengkases halaman yang berisi informasi dari merchant anda.", requireActivity().findViewById(R.id.nav_other), 2)*/

        observeViewModel()
    }

    private fun getMerchProfileData() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            viewModel.getMerchantProfile(requireContext(), requireActivity(), general_error_other)
            viewModel.getMerchantShopStatus(requireContext(), requireActivity(), general_error_other)
            general_error_other.isVisible = false
        } else {
            general_error_other.isVisible = true
            onlineService.networkDialog(requireActivity())
        }
    }

    fun ShowIntro(title: String, desc:String, view: View, type: Int){
        GuideView.Builder(requireContext())
            .setTitle(title)
            .setContentText(desc)
            .setGravity(GuideView.Gravity.auto)
            .setTargetView(view)
            .setDismissType(GuideView.DismissType.anywhere)
            .setContentTextSize(12)
            .setTitleTextSize(14)
            .setGuideListener {
                if (type == 2) {
                    ShowIntro(
                        "Pengaturan Button",
                        "Pada halaman ini terdapat tombol pengaturan yang berfungsi untuk mengatur toko Anda pada aplikasi",
                        merchant_setting_click, 4
                    )
                } else if (type == 4) {
                    ShowIntro(
                        "Integrasi Button",
                        "Pada halaman ini terdapat tombol integrasi di mana Anda dapat melakukan integrasi toko Anda dengan marketplace",
                        integrasiButton, 5
                    )
                } else if (type == 5) {
                    ShowIntro(
                        "Laporan Button",
                        "Pada halaman ini terdapat tombol laporan di mana Anda dapat melihat laporan mengenai toko Anda",
                        merchant_report_click, 6
                    )
                } else if (type == 6) {
                    ShowIntro(
                        "Help Button",
                        "Pada halaman ini terdapat tombol bantuan\u2028berguna untuk membantu Anda menghadapi kesulitan saat menggunakan aplikasi.",
                        merchant_click_help, 7
                    )
                }else if(type == 7){
                    viewModel1.postTutorial("TUTORIAL_OTHERS")
                }
            }
            .build()
            .show()
    }

    private fun observeViewModel() {
        viewModel.merchantResult.observe(viewLifecycleOwner, Observer { merchantProfile ->
            merchantProfile?.let {
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

    fun getTutorial(name: String){
        val email = sessionManager.getUserData()?.email
        val token = sessionManager.getUserToken()!!
        var mid = sessionManager.getUserData()?.mid
        var timestamp = getTimestamp()
        var uuid = getUUID()
        var clientId = getClientID()
        var status = false
        var signature = getSignature(email, timestamp)

        PikappApiService().api.getTutorial(uuid, timestamp, clientId, signature, token, mid, mid.toString())
            .enqueue(object : Callback<TutorialGetResponse> {
                override fun onResponse(
                    call: Call<TutorialGetResponse>,
                    response: Response<TutorialGetResponse>
                ) {
                    Log.e("Response", response.code().toString())
                    Log.e("Response", response.body()?.results.toString())
                    if(response.body()?.results?.isEmpty() == true){
                        ShowIntro("Merchant Info", "Tombol lainnya digunakan untuk mengkases halaman yang berisi informasi dari merchant anda.", requireActivity().findViewById(R.id.nav_other), 2)
                    }else{
                        for (i in response.body()?.results!!){
                            if(i.tutorial_page == name){
                                status = true
                            }
                        }
                        if(status == false){
                            ShowIntro("Merchant Info", "Tombol lainnya digunakan untuk mengkases halaman yang berisi informasi dari merchant anda.", requireActivity().findViewById(R.id.nav_other), 2)
                        }
                    }
                }

                override fun onFailure(call: Call<TutorialGetResponse>, t: Throwable) {
                    Log.e("error", t.message.toString())
                }

            })
    }

}