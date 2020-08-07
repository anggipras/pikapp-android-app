package com.bejohen.pikapp.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bejohen.pikapp.R
import kotlinx.android.synthetic.main.fragment_home_view_pager.view.*

class HomeViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_view_pager, container, false)

        val fragmentList = arrayListOf<Fragment>(
            HomeHomeFragment(),
            HomeLivechatFragment()
        )

        val adapter = HomeViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle )
        view.homeViewPager.adapter = adapter
        view.homeViewPager.isUserInputEnabled = false

        return view
    }
}