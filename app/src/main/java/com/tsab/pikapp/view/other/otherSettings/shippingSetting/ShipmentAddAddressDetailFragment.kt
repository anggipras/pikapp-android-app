package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShipmentAddAddressDetailBinding
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.util.PermissionUtils
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class ShipmentAddAddressDetailFragment : Fragment() {
    private lateinit var dataBinding: FragmentShipmentAddAddressDetailBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_shipment_add_address_detail,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.change_shipping_title)
        dataBinding.selectLocationId.setOnClickListener {
            when {
                PermissionUtils.isLocationEnabled(requireContext()) -> {
                    fetchLocation()
                }
                else -> {
                    PermissionUtils.showGPSNotEnabledDialog(requireContext())
                }
            }
        }
    }

    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                viewModel.setCurrentLocation(CurrentLatLng(latitude = it.latitude, longitude = it.longitude))
                view?.let { v -> Navigation.findNavController(v).navigate(R.id.navigateTo_merchantGetLocationFragment) }
            }
        }
    }
}