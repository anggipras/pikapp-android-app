package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_delivery.*
import java.text.NumberFormat


class DeliveryFragment: RoundedBottomSheetDialogFragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private var selfPickup: Boolean = false
    private var ekspedisiSend: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachInputListeners()
        observeViewModel()
    }

    private fun attachInputListeners() {
        pickup.setOnClickListener {
            if(selfPickup == false){
                pickup.setBackgroundResource(R.drawable.btn_green)
                pickup_title.setTextColor(Color.parseColor("#4BB7AC"))
                delivery.setBackgroundResource(R.drawable.btn_transparant)
                delivery_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.GONE
                selfPickup = true
                ekspedisiSend = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }else{
                Log.e("Choosen", "Udah Kepilih")
            }
        }

        delivery.setOnClickListener {
            if(ekspedisiSend == false){
                delivery.setBackgroundResource(R.drawable.btn_green)
                delivery_title.setTextColor(Color.parseColor("#4BB7AC"))
                pickup.setBackgroundResource(R.drawable.btn_transparant)
                pickup_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.VISIBLE
                ekspedisiSend = true
                selfPickup = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }else{
                Log.e("Choosen", "Udah Kepilih")
            }
        }

        btnSaveExpedition.setOnClickListener {
            if(ekspedisiSend){
                viewModel.setEkspedisi(namaEkspedisi.text.toString(), hargaEkspedisi.text.toString())
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
                    namaEkspedisi.setText(nama)
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
                hargaEkspedisi.setText(harga)
                pickup_title.setTextColor(Color.parseColor("#000000"))
                dataDeliv.visibility = View.VISIBLE
                ekspedisiSend = true
                selfPickup = false
                btnSaveExpedition.setBackgroundResource(R.drawable.button_green_square)
            }
        })
    }
}