package com.tsab.pikapp.view.homev2.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class DynamicFragmentAdapter internal constructor(
    fm: FragmentManager?,
    private val mNumOfTabs: Int
) :
    FragmentStatePagerAdapter(fm!!) {

    // get the current item with position number
    override fun getItem(position: Int): Fragment {
        val b = Bundle()
        b.putInt("position", position)
        val frag: Fragment = DynamicFragment.newInstance()
        frag.arguments = b
        return frag
    }

    // get total number of tabs
    override fun getCount(): Int {
        return mNumOfTabs
    }

}
