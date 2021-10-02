package com.tsab.pikapp.view.categoryProduct.merchantDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import com.tsab.pikapp.view.home.HomeLivechatFragment
import com.tsab.pikapp.view.home.HomeViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_merchant_detail_view_pager.view.*

class MerchantDetailViewPagerFragment : Fragment() {

    var merchantID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_merchant_detail_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            MerchantProductListFragment(),
            HomeLivechatFragment()
        )

        val adapter =
            HomeViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)
        view.merchantViewPager.adapter = adapter

        return view
    }
}