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
import com.tsab.pikapp.databinding.FragmentAllPromoBinding
import com.tsab.pikapp.models.model.PromoListModel
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel

class AllPromoFragment : Fragment() {
    private lateinit var dataBinding: FragmentAllPromoBinding
    private val viewModel: PromoViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_promo, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewAllpromos.layoutManager = linearLayoutManager

        getAllPromos()
    }

    private fun getAllPromos() {
        viewModel.retrievePromoList(requireContext(), "all", dataBinding.recyclerviewAllpromos)
    }
}