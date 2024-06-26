package com.tsab.pikapp.view.homev2.promo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.PromoFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.promo.PromoRegisAdapter
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel
import com.tsab.pikapp.viewmodel.homev2.TutorialViewModel
import androidx.recyclerview.widget.PagerSnapHelper
import com.tsab.pikapp.models.model.PromoAppliedListData
import com.tsab.pikapp.models.model.PromoRegisListData
import com.tsab.pikapp.view.promo.AllRegisPromoActivity
import com.tsab.pikapp.view.promo.PromoAppliedAdapter
import com.tsab.pikapp.view.promo.PromoDetailPageActivity
import java.io.Serializable

class PromoFragment : Fragment(), PromoRegisAdapter.OnItemClickListener, PromoAppliedAdapter.OnItemClickListener {
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: PromoFragmentBinding
    private val viewModel: PromoViewModel by activityViewModels()
    private val viewModel1: TutorialViewModel by activityViewModels()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerAdapter: PromoRegisAdapter
    private lateinit var recyclerAppliedPromoAdapter: PromoAppliedAdapter
    private val recyclerViewSnapHelper = PagerSnapHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        dataBinding = PromoFragmentBinding.inflate(
//            inflater, container, false
//        )
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.promo_fragment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager.setHomeNav(2)

//        if (activity != null && isAdded) {
//            setUpTabs()
//
//            swipeRefreshLayout = dataBinding.swipePromoMenu
//            swipeRefreshLayout.setOnRefreshListener {
//                val position = dataBinding.tabs.selectedTabPosition
//                refreshPage()
//                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
//                swipeRefreshLayout.isRefreshing = false
//            }
//
//            ShowIntro("Promo Button", "Tombol Promo digunakan untuk mengakses halaman “Promo” yang dimiliki oleh merchant.", requireActivity().findViewById(R.id.nav_promo), 2)
//        }

        viewModel.getPromoRegisList(0)
        viewModel.getPromoAppliedList(0)

        dataBinding.regisSeeAllPromo.setOnClickListener {
            Intent(activity?.baseContext, AllRegisPromoActivity::class.java).apply {
                startActivity(this)
                activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
            }
        }

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewPromoList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerAdapter = PromoRegisAdapter(requireContext(), this)
        dataBinding.recyclerviewPromoList.adapter = recyclerAdapter
        dataBinding.arIndicator.attachTo(dataBinding.recyclerviewPromoList, true)
        dataBinding.recyclerviewPromoList.onFlingListener = null
        recyclerViewSnapHelper.attachToRecyclerView(dataBinding.recyclerviewPromoList)
//        dataBinding.recyclerviewPromoList.addItemDecoration(CirclePagerIndicatorDecoration())

        dataBinding.recyclerviewAppliedPromoList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerAppliedPromoAdapter = PromoAppliedAdapter(requireContext(), this)
        dataBinding.recyclerviewAppliedPromoList.adapter = recyclerAppliedPromoAdapter

        dataBinding.nestedScrollViewHomePromo.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!viewModel.finishAppliedPromoPage.value!!) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    val pageAllPromoAct = viewModel.numberAppliedPromoPage.value!! + 1
                    dataBinding.loadingPB.isVisible = true
                    viewModel.getPromoAppliedList(pageAllPromoAct)
                } else {
                    dataBinding.loadingPB.isVisible = false
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel.getLiveDataPromoRegisListObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                dataBinding.registrationPromo.isVisible = true
                recyclerAdapter.setPromoListAdapter(it)
                dataBinding.arIndicator.numberOfIndicators = it.size
            }
        })

        viewModel.getLiveDataPromoAppliedListObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                dataBinding.appliedPromo.isVisible = true
                recyclerAppliedPromoAdapter.setPromoListAdapter(it)
            }
        })

        viewModel.finishAppliedPromoPage.observe(viewLifecycleOwner, {
            if (it) {
                dataBinding.loadingPB.isVisible = false
            }
        })
    }

    override fun onItemRegisPromoClick(promoRegisValue: PromoRegisListData) {
        Intent(activity?.baseContext, PromoDetailPageActivity::class.java).apply {
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_FLOW, "REGIS")
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_DATA, promoRegisValue as Serializable)
            startActivity(this)
            activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
        }
    }

    override fun onItemAppliedPromoClick(promoAppliedValue: PromoAppliedListData) {
        Intent(activity?.baseContext, PromoDetailPageActivity::class.java).apply {
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_FLOW, "APPLIED")
            putExtra(PromoDetailPageActivity.PROMO_DETAIL_DATA, promoAppliedValue as Serializable)
            startActivity(this)
            activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
        }
    }

//    private fun setUpTabs() {
//        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
//        if (adapter != null) {
//            adapter.addFragment(AllPromoFragment(), "Semua")
//            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
//            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
//            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
//            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")
//
//            dataBinding.viewpager.adapter = adapter
//            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
//        }
//    }
//
//    private fun refreshPage() {
//        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
//        if (adapter != null) {
//            adapter.rmFragment(AllPromoFragment(), "Semua")
//            adapter.rmFragment(OngoingPromoFragment(), "Berlangsung")
//            adapter.rmFragment(UpcomingPromoFragment(), "Akan Datang")
//            adapter.rmFragment(ExpirePromoFragment(), "Berakhir")
//            adapter.rmFragment(CanceledPromoFragment(), "Dihentikan")
//
//            adapter.addFragment(AllPromoFragment(), "Semua")
//            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
//            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
//            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
//            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")
//
//            dataBinding.viewpager.adapter = adapter
//            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
//        }
//    }
//
//    fun ShowIntro(title: String, desc:String, view: View, type: Int){
//        GuideView.Builder(requireContext())
//            .setTitle(title)
//            .setContentText(desc)
//            .setGravity(GuideView.Gravity.auto)
//            .setTargetView(view)
//            .setDismissType(GuideView.DismissType.anywhere)
//            .setContentTextSize(12)
//            .setTitleTextSize(14)
//            .setGuideListener {
//                if (type == 2) {
//                    ShowIntro(
//                        "Promo Page",
//                        "Halaman promo akan berisi daftar promo yang merchant anda miliki.",
//                        headerPromo, 4
//                    )
//                } else if (type == 4) {
//                    ShowIntro(
//                        "Promo Tab",
//                        "Terdapat 5 pilihan tab pada halaman Promo yang akan menampilkan Daftar Promo yang sedang berlangsung, akan datang, sudah berakhir, dan diberhentikan.",
//                        dataBinding.tabs, 5
//                    )
//                } else if (type == 5) {
//                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(0)
//                    ShowIntro(
//                        "Tab Semua",
//                        "Pada tab Semua akan berisi seluruh daftar promo yang dimiliki merchant saat ini",
//                        proses, 6
//                    )
//                } else if (type == 6) {
//                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(1)
//                    ShowIntro(
//                        "Tab Berlangsung",
//                        "Pada tab Berlangsung akan berisi seluruh daftar promo yang sedang berlangsung",
//                        proses, 7
//                    )
//                }else if (type == 7) {
//                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(2)
//                    ShowIntro(
//                        "Tab Akan Datang",
//                        "Pada tab Akan Datang akan berisi seluruh daftar promo yang akan berlangsung",
//                        proses, 8
//                    )
//                }else if (type == 8) {
//                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(3)
//                    ShowIntro(
//                        "Tab Berakhir",
//                        "Pada tab Berakhir akan berisi seluruh daftar promo yang sudah berakhir",
//                        proses, 9
//                    )
//                }else if (type == 9) {
//                    tabs.getTabAt(4)?.select()
//                    var proses = (tabs.getChildAt(0) as ViewGroup).getChildAt(4)
//                    ShowIntro(
//                        "Tab Dihentikan",
//                        "Pada tab Diberhentikan akan berisi seluruh daftar promo yang diberhentikan merchant",
//                        proses, 10
//                    )
//                }
//            }
//            .build()
//            .show()
//    }

}