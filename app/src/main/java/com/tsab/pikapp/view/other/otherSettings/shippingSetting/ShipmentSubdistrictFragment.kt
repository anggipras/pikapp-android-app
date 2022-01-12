package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShipmentSubdistrictBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class ShipmentSubdistrictFragment : Fragment(), SubdistrictListAdapter.OnSubdistrictClickListener {
    private lateinit var dataBinding: FragmentShipmentSubdistrictBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var recyclerAdapter: SubdistrictListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shipment_subdistrict,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.shipping_title)
        dataBinding.subdistrictSearchField.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchSubdistrict(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        dataBinding.subdistrictSearchField.setOnClickListener {
            dataBinding.subdistrictSearchField.onActionViewExpanded()
        }
        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewSubdistrict.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = SubdistrictListAdapter(this)
        dataBinding.recyclerviewSubdistrict.adapter = recyclerAdapter
    }

    private fun initViewModel() {
        viewModel.getLiveDataSubdistrictListObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                recyclerAdapter.setSubdistrictList(it)
            }
        })

        viewModel.searchSubdistrict.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                viewModel.getSubdistrictList()
            }
        })

        viewModel.getLiveDataSubdistrictSelectedObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {

            }
        })
    }

    override fun onItemClick(position: Int) {
        viewModel.setSelectedSubdistrict(position)
    }
}