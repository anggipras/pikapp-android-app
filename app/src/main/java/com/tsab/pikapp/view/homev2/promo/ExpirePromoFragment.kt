package com.tsab.pikapp.view.homev2.promo

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentExpirePromoBinding
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel

class ExpirePromoFragment : Fragment() {
    private lateinit var dataBinding: FragmentExpirePromoBinding
    private val viewModel: PromoViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_expire_promo, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewExpiredpromos.layoutManager = linearLayoutManager

        getExpirePromos()
    }

    private fun getExpirePromos() {
        viewModel.retrievePromoList(requireContext(), "expired", dataBinding.recyclerviewExpiredpromos)
    }
}