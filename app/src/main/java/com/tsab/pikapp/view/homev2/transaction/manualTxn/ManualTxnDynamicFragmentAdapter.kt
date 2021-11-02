package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ManualTxnDynamicFragmentAdapter internal constructor(
        fm: FragmentManager?,
        private val mNumOfTabs: Int,
        private val name: List<String>
) : FragmentPagerAdapter(fm!!) {
    // Get the current item with position number
    override fun getItem(position: Int): Fragment {
        return ManualTxnDynamicFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(
                    ManualTxnDynamicFragment.argumentCategoryName,
                    name[position]
                )
            } }
        }

    override fun getCount(): Int {
        return mNumOfTabs
    }
}