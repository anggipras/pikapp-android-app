package com.tsab.pikapp.view.storeOrderList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreOrderListStatusBinding
import com.tsab.pikapp.view.home.HomeViewPagerAdapter
import com.tsab.pikapp.viewmodel.storeOrderList.StoreOrderListStatusViewModel

class StoreOrderListStatusFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreOrderListStatusBinding
    private lateinit var viewModel: StoreOrderListStatusViewModel

    var tabSelected = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentList = arrayListOf<Fragment>(
            StoreOrderListPreparedFragment(),
            StoreOrderListReadyFragment(),
            StoreOrderListGiveRatingFragment()
        )

        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_order_list_status, container, false)
        viewModel = ViewModelProviders.of(this).get(StoreOrderListStatusViewModel::class.java)
        val adapter = HomeViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        dataBinding.storeOrderListViewPager.adapter = adapter
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.deleteNotificationDetail()

        viewModel.getSelectedTab().let {
            tabSelected = it
        }

        TabLayoutMediator(
            dataBinding.tabLayoutStoreOrderList,
            dataBinding.storeOrderListViewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.setIcon(R.drawable.ic_prepare)
                    tab.text = "Pesanan Baru"
                }
                1 -> {
                    tab.setIcon(R.drawable.ic_ready)
                    tab.text = "Pesanan Siap"
                }
                2 -> {
                    tab.setIcon(R.drawable.ic_finish)
                    tab.text = "Pesanan Selesai"
                }
            }
            dataBinding.storeOrderListViewPager.setCurrentItem(tab.position, true)
        }.attach()

        val tab: TabLayout.Tab = dataBinding.tabLayoutStoreOrderList.getTabAt(tabSelected)!!
        tab.select()
    }
}