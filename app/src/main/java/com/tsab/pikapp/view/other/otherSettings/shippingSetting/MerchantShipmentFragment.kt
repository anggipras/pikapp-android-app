package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentMerchantShipmentBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class MerchantShipmentFragment : Fragment() {
    private lateinit var dataBinding: FragmentMerchantShipmentBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_merchant_shipment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.shipping_title)

        dataBinding.switchShippingMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setShippingMode(isChecked)
        }

        Navigation.findNavController(view).navigate(R.id.navigateTo_merchantAddShipmentFragment)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.shippingMode.observe(viewLifecycleOwner, {
            dataBinding.switchShippingMode.isChecked = it
        })
    }
}