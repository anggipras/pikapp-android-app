package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCheckoutBinding
import com.tsab.pikapp.databinding.FragmentPaymentBinding
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.fragment_asal.*
import kotlinx.android.synthetic.main.fragment_payment.*

class PaymentFragment : Fragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentPaymentBinding
    private var navController: NavController? = null
    private var gopayStatus: Boolean = false
    private var ovoStatus: Boolean = false
    private var danaStatus: Boolean = false
    private var shopeeStatus: Boolean = false
    private var linkajaStatus: Boolean = false
    private var mandiriStatus: Boolean = false
    private var bcaStatus: Boolean = false
    private var bniStatus: Boolean = false
    private var briStatus: Boolean = false
    private var cimbStatus: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.topAppBar.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        gopay.setOnClickListener {
            if(!gopayStatus){
                gopay.setBackgroundResource(R.drawable.btn_green)
                gopay_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = true
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        ovo.setOnClickListener {
            if(!ovoStatus){
                ovo.setBackgroundResource(R.drawable.btn_green)
                ovo_title.setTextColor(Color.parseColor("#4BB7AC"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = true
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        dana.setOnClickListener {
            if(!danaStatus){
                dana.setBackgroundResource(R.drawable.btn_green)
                dana_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = true
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        shopee.setOnClickListener {
            if(!shopeeStatus){
                shopee.setBackgroundResource(R.drawable.btn_green)
                shopee_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = true
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        linkaja.setOnClickListener {
            if(!linkajaStatus){
                linkaja.setBackgroundResource(R.drawable.btn_green)
                linkaja_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = true
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        mandiri.setOnClickListener {
            if(!mandiriStatus){
                mandiri.setBackgroundResource(R.drawable.btn_green)
                mandiri_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = true
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        cimb.setOnClickListener {
            if(!cimbStatus){
                cimb.setBackgroundResource(R.drawable.btn_green)
                cimb_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = true
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        bca.setOnClickListener {
            if(!bcaStatus){
                bca.setBackgroundResource(R.drawable.btn_green)
                bca_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = true
                bniStatus = false
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        bni.setOnClickListener {
            if(!bniStatus){
                bni.setBackgroundResource(R.drawable.btn_green)
                bni_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                bri.setBackgroundResource(R.drawable.btn_transparant)
                bri_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = true
                briStatus = false
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        bri.setOnClickListener {
            if(!briStatus){
                bri.setBackgroundResource(R.drawable.btn_green)
                bri_title.setTextColor(Color.parseColor("#4BB7AC"))
                ovo.setBackgroundResource(R.drawable.btn_transparant)
                ovo_title.setTextColor(Color.parseColor("#000000"))
                dana.setBackgroundResource(R.drawable.btn_transparant)
                dana_title.setTextColor(Color.parseColor("#000000"))
                shopee.setBackgroundResource(R.drawable.btn_transparant)
                shopee_title.setTextColor(Color.parseColor("#000000"))
                linkaja.setBackgroundResource(R.drawable.btn_transparant)
                linkaja_title.setTextColor(Color.parseColor("#000000"))
                mandiri.setBackgroundResource(R.drawable.btn_transparant)
                mandiri_title.setTextColor(Color.parseColor("#000000"))
                bca.setBackgroundResource(R.drawable.btn_transparant)
                bca_title.setTextColor(Color.parseColor("#000000"))
                bni.setBackgroundResource(R.drawable.btn_transparant)
                bni_title.setTextColor(Color.parseColor("#000000"))
                gopay.setBackgroundResource(R.drawable.btn_transparant)
                gopay_title.setTextColor(Color.parseColor("#000000"))
                cimb.setBackgroundResource(R.drawable.btn_transparant)
                cimb_title.setTextColor(Color.parseColor("#000000"))
                gopayStatus = false
                ovoStatus = false
                danaStatus = false
                shopeeStatus = false
                cimbStatus = false
                linkajaStatus = false
                mandiriStatus = false
                bcaStatus = false
                bniStatus = false
                briStatus = true
                dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
            }
        }

        btnSaveBayar.setOnClickListener {
            if(gopayStatus){
                viewModel.setBayar(gopay_title.text.toString())
                navController?.navigateUp()
            }else if(ovoStatus){
                viewModel.setBayar(ovo_title.text.toString())
                navController?.navigateUp()
            }else if(danaStatus){
                viewModel.setBayar(dana_title.text.toString())
                navController?.navigateUp()
            }else if(shopeeStatus){
                viewModel.setBayar(shopee_title.text.toString())
                navController?.navigateUp()
            }else if(linkajaStatus){
                viewModel.setBayar(linkaja_title.text.toString())
                navController?.navigateUp()
            }else if(mandiriStatus){
                viewModel.setBayar(mandiri_title.text.toString())
                navController?.navigateUp()
            }else if(bcaStatus){
                viewModel.setBayar(bca_title.text.toString())
                navController?.navigateUp()
            }else if(bniStatus){
                viewModel.setBayar(bni_title.text.toString())
                navController?.navigateUp()
            }else if(briStatus){
                viewModel.setBayar(bri_title.text.toString())
                navController?.navigateUp()
            }else if(cimbStatus){
                viewModel.setBayar(cimb_title.text.toString())
                navController?.navigateUp()
            }
        }
    }

    private fun observeViewModel() {
       viewModel.BayarPesanan.observe(viewLifecycleOwner, Observer { nama->
           if(nama == "Gopay"){
               gopay.setBackgroundResource(R.drawable.btn_green)
               gopay_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = true
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "OVO"){
               ovo.setBackgroundResource(R.drawable.btn_green)
               ovo_title.setTextColor(Color.parseColor("#4BB7AC"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = true
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "DANA"){
               dana.setBackgroundResource(R.drawable.btn_green)
               dana_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = true
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "LinkAja"){
               linkaja.setBackgroundResource(R.drawable.btn_green)
               linkaja_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = true
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "ShopeePay"){
               shopee.setBackgroundResource(R.drawable.btn_green)
               shopee_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = true
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "Mandiri"){
               mandiri.setBackgroundResource(R.drawable.btn_green)
               mandiri_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = true
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "BCA"){
               bca.setBackgroundResource(R.drawable.btn_green)
               bca_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = true
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "BNI"){
               bni.setBackgroundResource(R.drawable.btn_green)
               bni_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = true
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "BRI"){
               bri.setBackgroundResource(R.drawable.btn_green)
               bri_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               cimb.setBackgroundResource(R.drawable.btn_transparant)
               cimb_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = false
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = true
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }else if(nama == "CIMB Niaga"){
               cimb.setBackgroundResource(R.drawable.btn_green)
               cimb_title.setTextColor(Color.parseColor("#4BB7AC"))
               ovo.setBackgroundResource(R.drawable.btn_transparant)
               ovo_title.setTextColor(Color.parseColor("#000000"))
               dana.setBackgroundResource(R.drawable.btn_transparant)
               dana_title.setTextColor(Color.parseColor("#000000"))
               shopee.setBackgroundResource(R.drawable.btn_transparant)
               shopee_title.setTextColor(Color.parseColor("#000000"))
               linkaja.setBackgroundResource(R.drawable.btn_transparant)
               linkaja_title.setTextColor(Color.parseColor("#000000"))
               mandiri.setBackgroundResource(R.drawable.btn_transparant)
               mandiri_title.setTextColor(Color.parseColor("#000000"))
               bca.setBackgroundResource(R.drawable.btn_transparant)
               bca_title.setTextColor(Color.parseColor("#000000"))
               bni.setBackgroundResource(R.drawable.btn_transparant)
               bni_title.setTextColor(Color.parseColor("#000000"))
               bri.setBackgroundResource(R.drawable.btn_transparant)
               bri_title.setTextColor(Color.parseColor("#000000"))
               gopay.setBackgroundResource(R.drawable.btn_transparant)
               gopay_title.setTextColor(Color.parseColor("#000000"))
               gopayStatus = false
               ovoStatus = false
               danaStatus = false
               shopeeStatus = false
               cimbStatus = true
               linkajaStatus = false
               mandiriStatus = false
               bcaStatus = false
               bniStatus = false
               briStatus = false
               dataBinding.btnSaveBayar.setBackgroundResource(R.drawable.button_green_square)
           }
       })
    }

}