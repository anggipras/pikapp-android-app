package com.tsab.pikapp.view.homev2.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class DynamicFragmentAdapter internal constructor(
    fm: FragmentManager?,
    private val mNumOfTabs: Int,
    private val name: List<String>
) : FragmentPagerAdapter(fm!!) {
    // Get the current item with position number
    override fun getItem(position: Int): Fragment {
        return DynamicFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(
                    DynamicFragment.argumentCategoryName,
                    name[position]
                )
            }
        }
    }

    // Get total number of tabs
    override fun getCount(): Int {
        return mNumOfTabs
    }
}
