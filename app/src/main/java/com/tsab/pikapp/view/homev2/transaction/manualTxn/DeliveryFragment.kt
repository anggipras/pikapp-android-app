package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CustomerCourierServiceList
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.fragment_delivery.*

class DeliveryFragment: BottomSheetDialogFragment(), CustomerCourierListAdapter.OnItemClickListener, CustomerCourierServiceListAdapter.OnItemCourierServiceClickListener {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private var selfPickup: Boolean = false
    private var ekspedisiSend: Boolean = false
    private lateinit var recyclerAdapter: CustomerCourierListAdapter
    private lateinit var recyclerAdapterCourierService: CustomerCourierServiceListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        select_shipment_id.visibility = View.VISIBLE
        initRecyclerView()
        initViewModel()
        attachInputListeners()
        observeViewModel()
    }

    private fun initViewModel() {
       viewModel.getLiveDataCourierListObserver().observe(viewLifecycleOwner, {
           if (!it.isNullOrEmpty()) {
               recyclerAdapter.setCourierList(it)
           }
       })

        viewModel.getLiveDataCourierServiceListObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                recyclerAdapterCourierService.setCourierServiceList(it)
            }
        })
    }

    private fun initRecyclerView() {
        recyclerview_customer_courier_list.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = CustomerCourierListAdapter(this)
        recyclerview_customer_courier_list.adapter = recyclerAdapter

        recyclerview_customer_courier_service_list.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapterCourierService = CustomerCourierServiceListAdapter(this)
        recyclerview_customer_courier_service_list.adapter = recyclerAdapterCourierService
    }

    private fun attachInputListeners() {
        pickup.setOnClickListener {
            if(!selfPickup){
                pickup.setBackgroundResource(R.drawable.btn_green)
                pickup_title.setTextColor(Color.parseColor("#4BB7AC"))
                delivery.setBackgroundResource(R.drawable.btn_transparant)
                delivery_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.GONE
                selfPickup = true
                ekspedisiSend = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        delivery.setOnClickListener {
            if(!ekspedisiSend){
                delivery.setBackgroundResource(R.drawable.btn_green)
                delivery_title.setTextColor(Color.parseColor("#4BB7AC"))
                pickup.setBackgroundResource(R.drawable.btn_transparant)
                pickup_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.VISIBLE
                ekspedisiSend = true
                selfPickup = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        dataDeliv.setOnClickListener {
            select_shipment_id.visibility = View.GONE
            courier_list_id.visibility = View.VISIBLE
            viewModel.setDummyData()
        }

        btnSaveCourierService.setOnClickListener {
            val courierName = if (viewModel.selectedCourierService.value?.service_name.isNullOrBlank()) {
                viewModel.selectedCourierService.value?.name.toString()
            } else {
                "${viewModel.selectedCourierService.value?.service_name.toString()} - ${viewModel.selectedCourierService.value?.name.toString()}"
            }
            viewModel.setEkspedisi(courierName, viewModel.selectedCourierService.value?.price.toString())
            dismiss()
        }

        btnSaveExpedition.setOnClickListener {
            if(ekspedisiSend){
                dismiss()
            }else if(selfPickup){
                viewModel.setEkspedisi("Pickup Sendiri", " ")
                dismiss()
            }
        }
    }

    private fun observeViewModel() {

        viewModel.NamaEkspedisi.observe(viewLifecycleOwner, Observer { nama ->
            if (nama != "") {
                if(nama == "Pickup Sendiri"){
                    pickup.setBackgroundResource(R.drawable.btn_green)
                    pickup_title.setTextColor(Color.parseColor("#4BB7AC"))
                    delivery.setBackgroundResource(R.drawable.btn_transparant)
                    delivery_title.setTextColor(Color.parseColor("#000000"))
                    dataDeliv.visibility = View.GONE
                    selfPickup = true
                    ekspedisiSend = false
                    btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
                }else{
                    delivery.setBackgroundResource(R.drawable.btn_green)
                    delivery_title.setTextColor(Color.parseColor("#4BB7AC"))
                    pickup.setBackgroundResource(R.drawable.btn_transparant)
                    pickup_title.setTextColor(Color.parseColor("#000000"))
                    dataDeliv.visibility = View.VISIBLE
                    ekspedisiSend = true
                    selfPickup = false
                    btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
                }
            }
        })

        viewModel.HargaEkspedisi.observe(viewLifecycleOwner, Observer { harga ->
            if (harga != "" && harga != " ") {
                delivery.setBackgroundResource(R.drawable.btn_green)
                delivery_title.setTextColor(Color.parseColor("#4BB7AC"))
                pickup.setBackgroundResource(R.drawable.btn_transparant)
                pickup_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.VISIBLE
                ekspedisiSend = true
                selfPickup = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        courier_list_id.visibility = View.GONE
        courier_serviceList_id.visibility = View.GONE
    }

    override fun onCourierClick(courierServiceList: MutableList<CustomerCourierServiceList>) {
        courier_list_id.visibility = View.GONE
        viewModel.liveDataCourierServiceList.postValue(courierServiceList)
        courier_serviceList_id.visibility = View.VISIBLE
    }

    override fun onCourierServiceClick(courierServiceObject: CustomerCourierServiceList) {
        viewModel.setSelectedCourierService(courierServiceObject)
        btnSaveCourierService.backgroundTintList = requireContext().resources.getColorStateList(R.color.colorGreen)
    }
}