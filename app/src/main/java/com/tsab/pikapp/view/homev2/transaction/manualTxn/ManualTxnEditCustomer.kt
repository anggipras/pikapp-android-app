package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnEditCustomerBinding
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.util.PermissionUtils
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class ManualTxnEditCustomer : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var navController: NavController? = null
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnEditCustomerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_edit_customer, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        navController = Navigation.findNavController(view)

        attachInputListener()
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.custNameTemp.observe(viewLifecycleOwner, Observer { custName ->
            if (custName != ""){
                dataBinding.custName.append(viewModel.custNameTemp.value)
                dataBinding.custPhone.append(viewModel.custPhoneTemp.value?.substringAfter("0"))
                dataBinding.custAddress.append(viewModel.custAddressTemp.value)
                dataBinding.custAddressDetail.append(viewModel.custAddressDetailTemp.value)
            }
        })

        viewModel.addressLocation.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                dataBinding.shipmentAddressTitle.isVisible = false
                dataBinding.shipmentAddressDetail.isVisible = false
                dataBinding.shipmentAddressResult.isVisible = true
                dataBinding.shipmentAddressResult.text = it[0].getAddressLine(0)
            } else {
                dataBinding.shipmentAddressTitle.isVisible = true
                dataBinding.shipmentAddressDetail.isVisible = true
                dataBinding.shipmentAddressResult.isVisible = false
            }
        })
    }

    private fun attachInputListener(){
        topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
        }

        dataBinding.customerShipmentButton.setOnClickListener {
            when {
                PermissionUtils.isLocationEnabled(requireContext()) -> {
                    fetchLocation()
                }
                else -> {
                    PermissionUtils.showGPSNotEnabledDialog(requireContext())
                }
            }
        }

        dataBinding.deleteBtn.setOnClickListener {
            viewModel.deleteCustomer()
            Toast.makeText(context, "Pelanggan berhasil dihapus", Toast.LENGTH_SHORT).show()
            navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
        }
        
        dataBinding.btnNext.setOnClickListener {
            if (dataBinding.custName.text.length < 3){
                dataBinding.nameError.visibility = View.VISIBLE
                dataBinding.custName.backgroundTintList = context?.resources?.getColorStateList(R.color.red)
                dataBinding.nameHeaderText.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.editCustName(dataBinding.custName.text.toString())
                var phone = dataBinding.custPhone.text.toString()
                viewModel.editCustPhone("0$phone")
                viewModel.editCustAddress(dataBinding.custAddress.text.toString())
                viewModel.editCustAddressDetail(dataBinding.custAddressDetail.text.toString())
                viewModel.editCustomer()
                Toast.makeText(context, "Pelanggan berhasil diubah", Toast.LENGTH_SHORT).show()
                navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
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
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                if (viewModel.currentLatLng.value == null) {
                    viewModel.setCurrentLocation(CurrentLatLng(latitude = it.latitude, longitude = it.longitude))
                } else {

                }
                view?.let { v -> Navigation.findNavController(v).navigate(R.id.action_manualTxnEditCustomer_to_customerGetLocationFragment) }
            }
        }
    }
}