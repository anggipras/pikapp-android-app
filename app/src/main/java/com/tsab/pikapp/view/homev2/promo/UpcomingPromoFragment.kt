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
import com.tsab.pikapp.databinding.FragmentUpcomingPromoBinding
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel

class UpcomingPromoFragment : Fragment() {
    private lateinit var dataBinding: FragmentUpcomingPromoBinding
    private val viewModel: PromoViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming_promo, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewUpcomingpromos.layoutManager = linearLayoutManager

        getUpcomingPromos()
    }

    private fun getUpcomingPromos() {
        viewModel.retrievePromoList(requireContext(), "upcoming", dataBinding.recyclerviewUpcomingpromos)
    }
}