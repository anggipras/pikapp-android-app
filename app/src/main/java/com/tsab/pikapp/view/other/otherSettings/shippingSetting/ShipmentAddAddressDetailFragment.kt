package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentShipmentAddAddressDetailBinding
import com.tsab.pikapp.models.model.CurrentLatLng
import com.tsab.pikapp.util.PermissionUtils
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel
import kotlinx.android.synthetic.main.input_dialog.view.*

class ShipmentAddAddressDetailFragment : Fragment(), CourierServiceListAdapter.OnCheckListener {
    private lateinit var dataBinding: FragmentShipmentAddAddressDetailBinding
    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val sessionManager = SessionManager()
    private lateinit var recyclerAdapter: CourierListAdapter

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

        initRecyclerView()
        initViewModel()

        viewModel.getCourierList(dataBinding.loadingOverlay)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        dataBinding.headerInsideSettings.headerTitle.text = getString(R.string.change_shipping_title)
        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
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

        dataBinding.postalCodeId.setOnClickListener {
            postalCodeDialog()
        }

        dataBinding.selectLocationText.text = viewModel.addressLocation.value?.get(0)?.getAddressLine(0)
        dataBinding.postalCodeContent.text = viewModel.postalCode.value
        dataBinding.addressShippingDetail.text = sessionManager.getMerchantProfile()?.address
        dataBinding.switchShippingMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setShippingMode(isChecked)
        }
        dataBinding.nextButton.setOnClickListener {
            viewModel.openSubmitDialog(requireActivity(), view, dataBinding.loadingOverlay)
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
                view?.let { v -> Navigation.findNavController(v).navigate(R.id.fromShipmentAddAddress_navigateTo_merchantGetLocationFragment) }
            }
        }
    }

    private fun postalCodeDialog() {
        val mDialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.input_dialog, null)
        val mBuilder = AlertDialog.Builder(requireActivity())
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.getWindow()?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireActivity(),
                R.drawable.dialog_background
            )
        )
        mDialogView.input_dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.input_dialog_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.input_dialog_ok.setOnClickListener {
            viewModel.setPostalCode(mDialogView.input_dialog_area.text.toString())
            mAlertDialog.dismiss()
        }
    }

    override fun onCheckClick(courierNameIndex: Int, courierServiceIndex: Int, isChecked: Boolean) {
        viewModel.changeCourierService(courierNameIndex, courierServiceIndex, isChecked)
    }
}