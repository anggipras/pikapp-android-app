package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnAddCustomerBinding
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.util.PermissionUtils

class ManualTxnAddCustomer : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var navController: NavController? = null
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnAddCustomerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_add_customer, container, false)
        return dataBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        navController = Navigation.findNavController(view)

        observeField()
        attachInputListener()
        observeViewModel()
    }

    private fun observeViewModel() {
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

        viewModel.customerPostalCode.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                dataBinding.custPostalCodeDetail.setText(it)
            }
        })
    }

    private fun observeField(){
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

        dataBinding.custName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        dataBinding.custPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        dataBinding.custAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validate(name: String, phone: String, address: String){
        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(address)){
            dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
            dataBinding.btnNext.isEnabled = true
        } else {
            dataBinding.btnNext.setBackgroundResource(R.drawable.button_gray_square)
            dataBinding.btnNext.isEnabled = false
        }
    }

    private fun attachInputListener(){
        dataBinding.btnNext.setOnClickListener {
            if (dataBinding.custName.text.length < 3){
                dataBinding.nameError.visibility = View.VISIBLE
                dataBinding.custName.backgroundTintList = context?.resources?.getColorStateList(R.color.red)
                dataBinding.nameHeaderText.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.addCustName(dataBinding.custName.text.toString())
                val phone = dataBinding.custPhone.text.toString()
                viewModel.addCustPhone("0$phone")
                viewModel.addCustAddress(dataBinding.custAddress.text.toString())
                viewModel.addCustAddressDetail(dataBinding.custAddressDetail.text.toString())
                viewModel.setPostalCode(dataBinding.custPostalCodeDetail.text.toString())
                view?.let { it1 -> viewModel.addCustomer(requireContext(), it1) }
            }
        }

        topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_manualTxnAddCustomer_to_manualTxnCustomerPage)
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
                Log.e("ASYIKK", CurrentLatLng(latitude = it.latitude, longitude = it.longitude).toString())
                viewModel.setCurrentLocation(CurrentLatLng(latitude = it.latitude, longitude = it.longitude))
                view?.let { v -> Navigation.findNavController(v).navigate(R.id.action_manualTxnAddCustomer_to_customerGetLocationFragment) }
            }
        }
    }
}