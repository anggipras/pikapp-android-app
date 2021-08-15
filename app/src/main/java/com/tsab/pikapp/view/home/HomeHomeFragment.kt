package com.tsab.pikapp.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentHomeHomeBinding
import com.tsab.pikapp.viewmodel.home.HomeHomeViewModel
import kotlinx.android.synthetic.main.fragment_home_home.*
import java.util.*

class HomeHomeFragment : Fragment() {

    private lateinit var dataBinding: FragmentHomeHomeBinding
    private lateinit var viewModel: HomeHomeViewModel
    private val homeBannerSliderAdapter = HomeBannerSliderListAdapter(arrayListOf())
    private val homeCategoryListAdapter = HomeCategoryListAdapter(arrayListOf())

    private var currentPage = 0
    private var NUM_PAGES = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_home, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeHomeViewModel::class.java)
        viewModel.refresh()

        dataBinding.viewPagerSlider.apply {
            adapter = homeBannerSliderAdapter
            viewPagerSlider.requestDisallowInterceptTouchEvent(true)
            TabLayoutMediator(dataBinding.tabLayout, dataBinding.viewPagerSlider) { tab, position ->
            }.attach()
        }

        // apply to home category
        dataBinding.recyclerViewCategory.apply {
            val gridLayoutManager =
                GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
            adapter = homeCategoryListAdapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
            addItemDecoration(ItemHomeCategoryDecoration(spacingInPixels))
        }

        dataBinding.homeHomeRefreshLayout.setOnRefreshListener {
            dataBinding.recyclerViewCategory.visibility = View.GONE
            dataBinding.homeBannerSliderLoadingShimmerView.visibility = View.VISIBLE
            dataBinding.homeCategoryLoadingShimmerView.visibility = View.VISIBLE

            viewModel.refreshBypassCache()
            dataBinding.homeHomeRefreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.homeBannerSlider.observe(this, Observer { homeBannerSlider ->
            homeBannerSlider?.let {
                dataBinding.viewPagerSlider.visibility = View.VISIBLE
                homeBannerSliderAdapter.updateHomeBannerList(homeBannerSlider)
                NUM_PAGES = homeBannerSlider.count()
                setTimerBanner()
            }
        })

        viewModel.homeBannerSliderLoadError.observe(this, Observer { isError ->
            isError?.let {
            }
        })

        viewModel.homeBannerSliderLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.homeBannerSliderLoadingShimmerView.visibility =
                    if (it) View.VISIBLE else View.GONE
                if (it) {
                    dataBinding.homeBannerSliderLoadingShimmerView.startShimmer()
//                    listError.visibility = View.GONE
                    dataBinding.viewPagerSlider.visibility = View.GONE
                } else {
                    dataBinding.homeBannerSliderLoadingShimmerView.startShimmer()
                }
            }
        })


        viewModel.homeCategory.observe(this, Observer { homeCategory ->
            homeCategory?.let {
                dataBinding.homeCategoryTitle.visibility = View.VISIBLE
                dataBinding.homeCategorySubtitle.visibility = View.VISIBLE
                dataBinding.homeCategoryCardView.visibility = View.VISIBLE
                dataBinding.recyclerViewCategory.visibility = View.VISIBLE
                homeCategoryListAdapter.updateCategoryList(homeCategory)
            }
        })

        viewModel.homeCategoryLoadError.observe(this, Observer { isError ->
            isError?.let {

            }
        })

        viewModel.homeCategoryLoading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.homeCategoryLoadingShimmerView.visibility =
                    if (it) View.VISIBLE else View.GONE
                if (it) {
                    dataBinding.homeCategoryLoadingShimmerView.startShimmer()
//                    listError.visibility = View.GONE
//                    dataBinding.homeCategoryTitle.visibility = View.GONE
//                    dataBinding.homeCategorySubtitle.visibility = View.GONE
//                    dataBinding.homeCategoryCardView.visibility = View.GONE
                    dataBinding.recyclerViewCategory.visibility = View.GONE
                } else {
                    dataBinding.homeCategoryLoadingShimmerView.stopShimmer()
                }
            }
        })

    }

    private fun setTimerBanner() {
        val handler = Handler()
        val Update = Runnable {
            if (currentPage === NUM_PAGES) {
                currentPage = 0
            }
            dataBinding.viewPagerSlider.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 5000, 5000)
    }
}