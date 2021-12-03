package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCheckoutBinding
import com.tsab.pikapp.databinding.FragmentInvoiceBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import java.text.NumberFormat
import java.util.*

class InvoiceFragment : Fragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentInvoiceBinding
    private var navController: NavController? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var invoiceAdapter: InvoiceAdapter
    private val localeID =  Locale("in", "ID")

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

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Fragment back pressed invoked")
                    val intent = Intent(activity?.baseContext, HomeActivity::class.java)
                    activity?.startActivityForResult(intent, 1)
                    activity?.overridePendingTransition(0, 0)
                }
            }
            )

        observeViewModel()
    }

    fun observeViewModel(){
        viewModel.AsalPesanan.observe(viewLifecycleOwner, Observer { nama ->
            if(nama != ""){
                dataBinding.asalPesanan.text = nama
            }
        })

        viewModel.BayarPesanan.observe(viewLifecycleOwner, Observer { nama ->
            if (nama != "") {
                dataBinding.namaBayar.text = nama
            }
        })

        viewModel.payStatus.observe(viewLifecycleOwner, Observer{ status ->
            if(status){
                dataBinding.statusBayar.text = "Sudah Melakukan Pembayaran"
                dataBinding.statusBayar.setTextColor(Color.parseColor("#4BB7AC"))
            }else if(!status){
                dataBinding.statusBayar.text = "Belum Melakukan Pembayaran"
                dataBinding.statusBayar.setTextColor(Color.parseColor("#DC6A84"))
            }
        })

        viewModel.totalQuantity.observe(viewLifecycleOwner, Observer { totalQty ->
            dataBinding.totalHargaTitle.text = "Total Harga ($totalQty Item(s))"
            dataBinding.totalHargaTitleBot.text = "Total Harga ($totalQty Item(s))"
        })

        viewModel.totalCart.observe(viewLifecycleOwner, Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalHarga.text = "Rp. $numberFormat"
        })

        viewModel.NamaEkspedisi.observe(viewLifecycleOwner, Observer { nama ->
            val thePrice: Long = viewModel.mutableCartPrice.value!!.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            if (nama == "Pickup Sendiri"){
                dataBinding.namaKurir.text = nama
                dataBinding.ongkirHarga.text = "Rp.0"
                dataBinding.totalHargaBot.text = "Rp." + numberFormat
            }else{
                dataBinding.namaKurir.text = "Dikirim (" + nama + " - Rp " + viewModel.mutableHargaEkspedisi.value + ")"
                dataBinding.ongkirHarga.text = "Rp." + viewModel.mutableHargaEkspedisi.value
                dataBinding.totalHargaBot.text = "Rp." + (viewModel.mutableCartPrice.value!!.toInt() + viewModel.mutableHargaEkspedisi.value!!.toInt())
            }
        })

        viewModel.WaktuPesan.observe(viewLifecycleOwner, Observer { waktu ->
            if(waktu == "Sekarang"){
                dataBinding.tanggalKirim.text = "Sekarang"
            }else{
                dataBinding.tanggalKirim.text = "Custom (" + viewModel.mutableCustomWaktu.value + ")"
            }
        })
    }
}