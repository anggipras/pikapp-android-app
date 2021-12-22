package com.tsab.pikapp.view.homev2.promo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentUpcomingPromoBinding

class UpcomingPromoFragment : Fragment() {
    private lateinit var dataBinding: FragmentUpcomingPromoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_promo, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUpcomingPromos()
    }

    private fun getUpcomingPromos() {
        Log.e("upcomingpromo", "this is upcoming promo")
    }
}