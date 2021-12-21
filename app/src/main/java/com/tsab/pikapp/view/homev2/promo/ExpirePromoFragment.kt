package com.tsab.pikapp.view.homev2.promo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentExpirePromoBinding

class ExpirePromoFragment : Fragment() {
    private lateinit var dataBinding: FragmentExpirePromoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_expire_promo, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getExpirePromos()
    }

    private fun getExpirePromos() {
        Log.e("expirepromo", "this is expire promo")
    }
}