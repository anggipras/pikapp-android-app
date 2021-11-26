package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCheckoutBinding
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_proccess.view.*
import java.text.NumberFormat
import java.util.*

class CheckoutFragment : Fragment() {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentCheckoutBinding
    private var navController: NavController? = null
    private var custStat: Boolean = false
    private var kurirStat: Boolean = false
    private var dateStat: Boolean = false
    private var asalStat: Boolean = false
    private var paymentStat: Boolean = false
    private val localeID =  Locale("in", "ID")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel(){
        viewModel.totalQuantity.observe(viewLifecycleOwner, Observer { totalQty ->
            dataBinding.totalHargaTitle.text = "Total Harga ($totalQty Item(s))"
        })

        viewModel.NamaEkspedisi.observe(viewLifecycleOwner, Observer { nama ->
            if(nama != ""){
                kurirStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.dataPengiriman.visibility = View.VISIBLE
                dataBinding.namaKirim.text = nama
            }
        })

        viewModel.AsalPesanan.observe(viewLifecycleOwner, Observer { nama ->
            if(nama != ""){
                asalStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.asalPesanan.visibility = View.VISIBLE
                dataBinding.asalPesan.text = nama
            }
        })

        viewModel.HargaEkspedisi.observe(viewLifecycleOwner, Observer { harga ->
            if(harga != ""){
                dataBinding.dataPengiriman.visibility = View.VISIBLE
                dataBinding.hargaKirim.visibility = View.VISIBLE
                dataBinding.hargaKirim.text = "Rp. $harga"
            }
            if(harga == " "){
                dataBinding.hargaKirim.visibility = View.GONE
            }
        })

        viewModel.WaktuPesan.observe(viewLifecycleOwner, Observer { waktu ->
            if(waktu != ""){
                dateStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.dataTanggal.visibility = View.VISIBLE
                dataBinding.namaWaktu.text = waktu
            }
        })

        viewModel.WaktuPesanCustom.observe(viewLifecycleOwner, Observer { custom ->
            if(custom != ""){
                dataBinding.dataTanggal.visibility = View.VISIBLE
                dataBinding.customWaktu.text = custom
            }
        })

        viewModel.totalCart.observe(viewLifecycleOwner, Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalHarga.text = "Rp. $numberFormat"
            dataBinding.hargaBottom.text = "Rp. $numberFormat"
        })

        viewModel.BayarPesanan.observe(viewLifecycleOwner, Observer { nama ->
            if (nama != "") {
                paymentStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.bayarPesanan.visibility = View.VISIBLE
                dataBinding.bayarPesanDengan.text = nama
            }
        })

        viewModel.custName.observe(viewLifecycleOwner, Observer { name ->
            if (name != ""){
                custStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.dataCust.visibility = View.VISIBLE
                dataBinding.namaCust.text = viewModel.custName.value
                dataBinding.noTelp.text = viewModel.custPhone.value
                dataBinding.alamat.text = viewModel.custAddress.value
                dataBinding.catatan.text = viewModel.custAddressDetail.value
            } else {
                dataBinding.dataCust.visibility = View.GONE
            }
        })
    }

    private fun attachInputListeners() {
        dataBinding.topAppBar.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        dataBinding.btnNext.setOnClickListener {
            if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                viewModel.mutablePayStat.value = dataBinding.payStat.isChecked
                viewModel.postOrder(dataBinding.payStat.isChecked)
                navController?.navigate(R.id.action_checkoutFragment_to_invoiceFragment)
            }else{
                Log.e("Fail", "Data Kosong")
            }
        }

        dataBinding.bayarBtn.setOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_paymentFragment)
        }

        dataBinding.asalBtn.setOnClickListener {
            AsalFragment().show(requireActivity().supportFragmentManager, "show")
        }

        dataBinding.tanggalBtn.setOnClickListener {
            DateFragment().show(requireActivity().supportFragmentManager, "show")
        }

        dataBinding.kirimBtn.setOnClickListener {
            DeliveryFragment().show(requireActivity().supportFragmentManager, "show")
        }
        dataBinding.pelanggan.setOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_manualTxnCustomerPage)
        }
    }
}