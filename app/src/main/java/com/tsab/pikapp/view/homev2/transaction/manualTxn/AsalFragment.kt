package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.fragment_asal.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_delivery.*
import java.text.NumberFormat

class AsalFragment : RoundedBottomSheetDialogFragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private var instagramStatus: Boolean = false
    private var whatsappStatus: Boolean = false
    private var teleponStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachInputListeners()
        observeViewModel()
    }

    private fun attachInputListeners() {
        instagram.setOnClickListener {
            if(!instagramStatus){
                instagram.setBackgroundResource(R.drawable.btn_green)
                instagram_title.setTextColor(Color.parseColor("#4BB7AC"))
                whatsapp.setBackgroundResource(R.drawable.btn_transparant)
                whatsapp_title.setTextColor(Color.parseColor("#000000"))
                telepon.setBackgroundResource(R.drawable.btn_transparant)
                telepon_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = true
                whatsappStatus = false
                teleponStatus = false
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }else{
                Log.e("Choosen", "Udah Kepilih")
            }
        }

        whatsapp.setOnClickListener {
            if(!whatsappStatus){
                whatsapp.setBackgroundResource(R.drawable.btn_green)
                whatsapp_title.setTextColor(Color.parseColor("#4BB7AC"))
                instagram.setBackgroundResource(R.drawable.btn_transparant)
                instagram_title.setTextColor(Color.parseColor("#000000"))
                telepon.setBackgroundResource(R.drawable.btn_transparant)
                telepon_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = false
                whatsappStatus = true
                teleponStatus = false
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }else{
                Log.e("Choosen", "Udah Kepilih")
            }
        }

        telepon.setOnClickListener {
            if(!teleponStatus){
                telepon.setBackgroundResource(R.drawable.btn_green)
                telepon_title.setTextColor(Color.parseColor("#4BB7AC"))
                whatsapp.setBackgroundResource(R.drawable.btn_transparant)
                whatsapp_title.setTextColor(Color.parseColor("#000000"))
                instagram.setBackgroundResource(R.drawable.btn_transparant)
                instagram_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = false
                whatsappStatus = false
                teleponStatus = true
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }else{
                Log.e("Choosen", "Udah Kepilih")
            }
        }

        btnSaveAsal.setOnClickListener {
            if(instagramStatus){
                viewModel.setAsal("Instagram")
                dismiss()
            }else if(whatsappStatus){
                viewModel.setAsal("Whatsapp")
                dismiss()
            }else if(teleponStatus){
                viewModel.setAsal("Telepon")
                dismiss()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.AsalPesanan.observe(viewLifecycleOwner, Observer { nama ->
            if(nama == "Instagram"){
                instagram.setBackgroundResource(R.drawable.btn_green)
                instagram_title.setTextColor(Color.parseColor("#4BB7AC"))
                whatsapp.setBackgroundResource(R.drawable.btn_transparant)
                whatsapp_title.setTextColor(Color.parseColor("#000000"))
                telepon.setBackgroundResource(R.drawable.btn_transparant)
                telepon_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = true
                whatsappStatus = false
                teleponStatus = false
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }else if(nama == "Whatsapp"){
                whatsapp.setBackgroundResource(R.drawable.btn_green)
                whatsapp_title.setTextColor(Color.parseColor("#4BB7AC"))
                instagram.setBackgroundResource(R.drawable.btn_transparant)
                instagram_title.setTextColor(Color.parseColor("#000000"))
                telepon.setBackgroundResource(R.drawable.btn_transparant)
                telepon_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = false
                whatsappStatus = true
                teleponStatus = false
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }else if(nama == "Telepon"){
                telepon.setBackgroundResource(R.drawable.btn_green)
                telepon_title.setTextColor(Color.parseColor("#4BB7AC"))
                whatsapp.setBackgroundResource(R.drawable.btn_transparant)
                whatsapp_title.setTextColor(Color.parseColor("#000000"))
                instagram.setBackgroundResource(R.drawable.btn_transparant)
                instagram_title.setTextColor(Color.parseColor("#000000"))
                instagramStatus = false
                whatsappStatus = false
                teleponStatus = true
                btnSaveAsal.setBackgroundResource(R.drawable.button_green_square)
            }
        })
    }
}