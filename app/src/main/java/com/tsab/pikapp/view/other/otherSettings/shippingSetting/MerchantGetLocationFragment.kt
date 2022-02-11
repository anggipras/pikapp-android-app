package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentMerchantGetLocationBinding
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import java.lang.NumberFormatException

class MerchantGetLocationFragment : Fragment() {
    private lateinit var dataBinding: FragmentMerchantGetLocationBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_merchant_get_location,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.shipping_title)
        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
        dataBinding.searchLocation.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.navigateTo_merchantFindLocationFragment)
        }

        dataBinding.nextButton.setOnClickListener {
            if (viewModel.editOrAddShipment.value == true) {
                Navigation.findNavController(view).popBackStack()
            } else {
                Navigation.findNavController(view).navigate(R.id.navigateTo_shipmentAddAddressDetailFragment)
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

        mapFragment.getMapAsync { map ->
            // move camera ke current position
            val latLong: LatLng
            if (viewModel.currentLatLng.value == null) {
                latLong = LatLng(-6.186486, 106.834091)
            } else {
                latLong = LatLng(viewModel.currentLatLng.value!!.latitude,
                    viewModel.currentLatLng.value!!.longitude
                )
                val currentFirstLatLng = CurrentLatLng(latitude = viewModel.currentLatLng.value!!.latitude, longitude = viewModel.currentLatLng.value!!.longitude)
                viewModel.setAddressLocation(requireContext(), currentFirstLatLng)
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 18f))

            // get old position camera
            val oldPosition = map.cameraPosition.target

            map.setOnCameraMoveStartedListener { //drag started
                // start animation
                dataBinding.iconMarker.animate().translationY(-50f).start()
                dataBinding.iconMarkerShadow.animate().withStartAction {
                    dataBinding.iconMarkerShadow.setPadding(10, 10, 10, 10)
                }.start()
                dataBinding.locationAddressBottom.visibility = View.GONE
            }

            map.setOnCameraIdleListener {
                //get new position after drag
                val newPosition = map.cameraPosition.target
                if (newPosition != oldPosition) { //drag ended
                    // start animation
                    dataBinding.iconMarker.animate().translationY(0f).start()
                    dataBinding.iconMarkerShadow.animate().withStartAction {
                        dataBinding.iconMarkerShadow.setPadding(0, 0, 0, 0)
                    }.start()

                    val currentLatLng = CurrentLatLng(latitude = newPosition.latitude, longitude = newPosition.longitude)
                    viewModel.setAddressLocation(requireContext(), currentLatLng)
                    viewModel.setCurrentLocation(currentLatLng)
                } else {
                    val viewModelPosition = LatLng(viewModel.currentLatLng.value!!.latitude, viewModel.currentLatLng.value!!.longitude)
                    if (viewModelPosition != oldPosition) {
                        // start animation
                        dataBinding.iconMarker.animate().translationY(0f).start()
                        dataBinding.iconMarkerShadow.animate().withStartAction {
                            dataBinding.iconMarkerShadow.setPadding(0, 0, 0, 0)
                        }.start()

                        val currentLatLng = CurrentLatLng(latitude = viewModel.currentLatLng.value!!.latitude, longitude = viewModel.currentLatLng.value!!.longitude)
                        viewModel.setAddressLocation(requireContext(), currentLatLng)
                        viewModel.setCurrentLocation(currentLatLng)
                    }
                }
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.addressLocation.observe(viewLifecycleOwner, {
            if (it != null) {
                val addressFirstInd = it[0].getAddressLine(0).split(",")[0]
                val addressSecondInd = it[0].getAddressLine(0).split(",")[1]
                var numeric = true

                try {
                    val num = addressFirstInd.toInt()
                } catch (e: NumberFormatException) {
                    numeric = false
                }

                val addressLine = if (numeric) {
                    addressSecondInd
                } else {
                    addressFirstInd
                }
                dataBinding.locationAddressTitle.text = getString(R.string.detail_address_location, addressLine)
                dataBinding.locationAddressDetail.text = getString(R.string.main_address_location, it[0].locality)
                dataBinding.locationAddressBottom.visibility = View.VISIBLE
            }
        })
    }
}