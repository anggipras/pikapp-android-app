package com.tsab.pikapp.view.homev2.promo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.PromoFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.transaction.CancelFragment
import com.tsab.pikapp.view.homev2.transaction.DoneFragment
import com.tsab.pikapp.view.homev2.transaction.ProcessFragment
import com.tsab.pikapp.view.homev2.transaction.TransactionAdapter
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel
import kotlinx.android.synthetic.main.other_fragment.*
import kotlinx.android.synthetic.main.promo_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.tabs
import smartdevelop.ir.eram.showcaseviewlib.GuideView

class PromoFragment : Fragment() {
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: PromoFragmentBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewModel: PromoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.promo_fragment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this).get(PromoViewModel::class.java)

        if (activity != null && isAdded) {
            sessionManager.setHomeNav(2)
            setUpTabs()

            swipeRefreshLayout = dataBinding.swipePromoMenu
            swipeRefreshLayout.setOnRefreshListener {
                val position = dataBinding.tabs.selectedTabPosition
                refreshPage()
                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
                swipeRefreshLayout.isRefreshing = false
            }

/*
            ShowIntro("Promo Button", "Tombol Promo digunakan untuk mengakses halaman “Promo” yang dimiliki oleh merchant.", requireActivity().findViewById(R.id.nav_promo), 2)
*/
        }
    }

    private fun setUpTabs() {
        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(AllPromoFragment(), "Semua")
            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")

            dataBinding.viewpager.adapter = adapter
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
        }
    }

    private fun refreshPage() {
        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.rmFragment(AllPromoFragment(), "Semua")
            adapter.rmFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.rmFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.rmFragment(ExpirePromoFragment(), "Berakhir")
            adapter.rmFragment(CanceledPromoFragment(), "Dihentikan")

            adapter.addFragment(AllPromoFragment(), "Semua")
            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")

            dataBinding.viewpager.adapter = adapter
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
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
                        "Promo Page",
                        "Halaman promo akan berisi daftar promo yang merchant anda miliki.",
                        headerPromo, 4
                    )
                } else if (type == 4) {
                    ShowIntro(
                        "Promo Tab",
                        "Terdapat 5 pilihan tab pada halaman Promo yang akan menampilkan Daftar Promo yang sedang berlangsung, akan datang, sudah berakhir, dan diberhentikan.",
                        dataBinding.tabs, 5
                    )
                } else if (type == 5) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(0)
                    ShowIntro(
                        "Tab Semua",
                        "Pada tab Semua akan berisi seluruh daftar promo yang dimiliki merchant saat ini",
                        proses, 6
                    )
                } else if (type == 6) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(1)
                    ShowIntro(
                        "Tab Berlangsung",
                        "Pada tab Berlangsung akan berisi seluruh daftar promo yang sedang berlangsung",
                        proses, 7
                    )
                }else if (type == 7) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(2)
                    ShowIntro(
                        "Tab Akan Datang",
                        "Pada tab Akan Datang akan berisi seluruh daftar promo yang akan berlangsung",
                        proses, 8
                    )
                }else if (type == 8) {
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(3)
                    ShowIntro(
                        "Tab Berakhir",
                        "Pada tab Berakhir akan berisi seluruh daftar promo yang sudah berakhir",
                        proses, 9
                    )
                }else if (type == 9) {
                    tabs.getTabAt(4)?.select()
                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(4)
                    ShowIntro(
                        "Tab Dihentikan",
                        "Pada tab Diberhentikan akan berisi seluruh daftar promo yang diberhentikan merchant",
                        proses, 10
                    )
                }
            }
            .build()
            .show()
    }
}