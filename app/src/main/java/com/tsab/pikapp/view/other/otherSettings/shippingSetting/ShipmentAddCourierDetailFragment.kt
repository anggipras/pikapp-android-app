package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShipmentAddCourierDetailBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class ShipmentAddCourierDetailFragment : Fragment(), CourierServiceListAdapter.OnCheckListener {
    private lateinit var dataBinding: FragmentShipmentAddCourierDetailBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var recyclerAdapter: CourierListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shipment_add_courier_detail,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initViewModel()

        viewModel.getCourierList(dataBinding.loadingOverlay)
        viewModel.setShippingMode(true)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.select_courier_title)
        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        dataBinding.nextButton.setOnClickListener {
            viewModel.openSubmitDialog(requireActivity(), view, dataBinding.loadingOverlay, viewModel.addPostalCode.value.toString(), viewModel.addressShippingDetail.value.toString())
        }
    }

    private fun initViewModel() {
        viewModel.getLiveDataCourierListObserver().observe(viewLifecycleOwner, {
            if (it != null) {
                recyclerAdapter.setCourierListAdapter(it)
            }
        })
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewCourierChoice.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = CourierListAdapter(requireContext(), this)
        dataBinding.recyclerviewCourierChoice.adapter = recyclerAdapter
    }

    override fun onCheckClick(courierNameIndex: Int, courierServiceIndex: Int, isChecked: Boolean) {
        viewModel.changeCourierService(courierNameIndex, courierServiceIndex, isChecked)
    }
}