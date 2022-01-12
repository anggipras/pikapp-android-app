package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentMerchantAddShipmentBinding

class MerchantAddShipmentFragment : Fragment() {
    private lateinit var dataBinding: FragmentMerchantAddShipmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_merchant_add_shipment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.shipping_title)
        dataBinding.nextButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateTo_shipmentAddAddressDetailFragment)
        }
    }
}