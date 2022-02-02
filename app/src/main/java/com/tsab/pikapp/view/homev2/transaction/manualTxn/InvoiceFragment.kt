package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentInvoiceBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*

class InvoiceFragment : Fragment() {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentInvoiceBinding
    private var navController: NavController? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var invoiceAdapter: InvoiceAdapter
    private val localeID =  Locale("in", "ID")
    var temp: String = ""
    var number: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_invoice, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.menuList.layoutManager = linearLayoutManager
        dataBinding.menuList.setHasFixedSize(false)

        invoiceAdapter = InvoiceAdapter(requireView().context, viewModel.selectedMenuTemp.value as MutableList<AddManualAdvMenu>)
        invoiceAdapter.notifyDataSetChanged()
        dataBinding.menuList.adapter = invoiceAdapter

        dataBinding.namaPelanggan.text = viewModel.mutableCustName.value
        dataBinding.noTelpPelanggan.text = viewModel.mutableCustPhone.value
        dataBinding.alamatPelanggan.text = viewModel.mutableCustAddress.value
        dataBinding.catatanPelanggan.text = viewModel.mutableCustAddressDetail.value

        dataBinding.topAppBar.setNavigationOnClickListener {
            val intent = Intent(activity?.baseContext, HomeActivity::class.java)
            activity?.startActivityForResult(intent, 1)
            activity?.overridePendingTransition(0, 0)
        }

        dataBinding.btnShare.setOnClickListener {
            temp = viewModel.mutableCustPhone.value.toString().substringAfter("0")
            number = "+62 $temp"
            val message = "halo\ntesting buat\nbanyak paragraf\nmessage wa"
            val url = "https://api.whatsapp.com/send?phone=$number"+"&text=" + URLEncoder.encode(message, "UTF-8")
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                    activity?.startActivityForResult(intent, 1)
                    activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
                }
            }
            )

        observeViewModel()
    }

    fun observeViewModel(){
        viewModel.AsalPesanan.observe(viewLifecycleOwner, { nama ->
            if(nama != ""){
                dataBinding.asalPesanan.text = nama
            }
        })

        viewModel.BayarPesanan.observe(viewLifecycleOwner, { nama ->
            if (nama != "") {
                dataBinding.namaBayar.text = nama
            }
        })

        viewModel.payStatus.observe(viewLifecycleOwner,{ status ->
            if(status){
                dataBinding.statusBayar.text = "Sudah Melakukan Pembayaran"
                dataBinding.statusBayar.setTextColor(Color.parseColor("#4BB7AC"))
            }else if(!status){
                dataBinding.statusBayar.text = "Belum Melakukan Pembayaran"
                dataBinding.statusBayar.setTextColor(Color.parseColor("#DC6A84"))
            }
        })

        viewModel.totalQuantity.observe(viewLifecycleOwner, { totalQty ->
            dataBinding.totalHargaTitle.text = "Total Harga ($totalQty Item(s))"
        })

        viewModel.totalCart.observe(viewLifecycleOwner, { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalHarga.text = "Rp. $numberFormat"
        })

        viewModel.NamaEkspedisi.observe(viewLifecycleOwner, { nama ->
            if (nama == "Pickup Sendiri"){
                dataBinding.namaKurir.text = nama
                dataBinding.ongkirHarga.text = "Rp. 0"
            } else {
                val shipmentPrice: Long = viewModel.mutableHargaEkspedisi.value!!.toLong()
                val shipmentPriceFormat = NumberFormat.getInstance(localeID).format(shipmentPrice)
                dataBinding.namaKurir.text = "Dikirim (" + nama + " - Rp " + viewModel.mutableHargaEkspedisi.value + ")"
                dataBinding.ongkirHarga.text = "Rp. $shipmentPriceFormat"
            }
        })

        viewModel.insurancePrice.observe(viewLifecycleOwner, { insurance ->
            if (!insurance.isNullOrEmpty()) {
                if (insurance != "0") {
                    val insuranceFormat = NumberFormat.getInstance(localeID).format(insurance.toLong())
                    dataBinding.insurancePriceTitle.isVisible = true
                    dataBinding.insurancePrice.isVisible = true
                    dataBinding.insurancePrice.text = "Rp. $insuranceFormat"
                } else {
                    dataBinding.insurancePriceTitle.isVisible = false
                    dataBinding.insurancePrice.isVisible = false
                }
            }
        })

        viewModel.invoiceTotalPrice.observe(viewLifecycleOwner, { price ->
            val invoiceTotalPrice: Long = price.toLong()
            val invoiceTotalPriceFormat = NumberFormat.getInstance(localeID).format(invoiceTotalPrice)
            dataBinding.totalHargaBot.text = "Rp. $invoiceTotalPriceFormat"
        })

        viewModel.WaktuPesan.observe(viewLifecycleOwner, { waktu ->
            if(waktu == "Sekarang"){
                dataBinding.tanggalKirim.text = "Sekarang"
            }else{
                dataBinding.tanggalKirim.text = "Custom (" + viewModel.mutableCustomWaktu.value + ")"
            }
        })
    }
}