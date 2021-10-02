package com.tsab.pikapp.view.orderList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentOrderListStatusBinding
import com.tsab.pikapp.view.OrderListActivity
import com.tsab.pikapp.view.home.HomeViewPagerAdapter
import com.tsab.pikapp.viewmodel.orderList.OrderListStatusViewModel


class OrderListStatusFragment : Fragment() {

    private lateinit var dataBinding: FragmentOrderListStatusBinding
    private lateinit var viewModel: OrderListStatusViewModel
    var tabSelected = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val fragmentList = arrayListOf<Fragment>(
            OrderListUnpaidFragment(),
            OrderListPreparedFragment(),
            OrderListReadyFragment(),
            OrderListGiveRatingFragment()
        )

        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_list_status, container, false)
        viewModel = ViewModelProviders.of(this).get(OrderListStatusViewModel::class.java)
        val adapter =
            HomeViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
//        dataBinding.orderListViewPager.isUserInputEnabled = false
        dataBinding.orderListViewPager.adapter = adapter
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabSelected = viewModel.getSelectedTab()
        viewModel.checkNotificationOrder()
        TabLayoutMediator(
            dataBinding.tabLayoutOrderList,
            dataBinding.orderListViewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_unpaid)
                    tab.text = "Belum Bayar"
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_prepare)
                    tab.text = "Disiapkan"
                }
                2 -> {
                    tab.setIcon(R.drawable.ic_ready)
                    tab.text = "Pesanan Siap"
                }
                3 -> {
                    tab.setIcon(R.drawable.ic_finish)
                    tab.text = "Selesai"
                }
            }
            dataBinding.orderListViewPager.setCurrentItem(tab.position, true)
        }.attach()

        val tab: TabLayout.Tab = dataBinding.tabLayoutOrderList.getTabAt(tabSelected)!!
        tab.select()

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.notificationActive.observe(this, Observer { t ->
            if (t.isNotEmpty()) {
                viewModel.goToOrderListDetail(activity as OrderListActivity)
            }
        })
    }
}