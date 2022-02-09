package com.tsab.pikapp.view.homev2.transaction.manualTxn

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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCheckoutBinding
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
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
        viewModel.totalQuantity.observe(viewLifecycleOwner, { totalQty ->
            dataBinding.totalHargaTitle.text = "Total Harga ($totalQty Item(s))"
        })

        viewModel.NamaEkspedisi.observe(viewLifecycleOwner, { nama ->
            if(nama != ""){
                kurirStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.dataPengiriman.visibility = View.VISIBLE
                dataBinding.divider1.visibility = View.VISIBLE
                dataBinding.namaKirim.text = nama
            } else {
                dataBinding.dataPengiriman.visibility = View.GONE
                dataBinding.divider1.visibility = View.GONE
            }
        })

        viewModel.AsalPesanan.observe(viewLifecycleOwner, { nama ->
            if(nama != ""){
                asalStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.asalPesanan.visibility = View.VISIBLE
                dataBinding.asalPesan.text = nama
            }
        })

        viewModel.HargaEkspedisi.observe(viewLifecycleOwner, { harga ->
            if(harga != "0"){
                dataBinding.dataPengiriman.visibility = View.VISIBLE
                dataBinding.hargaKirim.visibility = View.VISIBLE
                dataBinding.paymentInsurance.visibility = View.VISIBLE
                val shipmentPrice: Long = harga.toLong()
                val shipmentPriceFormat = NumberFormat.getInstance(localeID).format(shipmentPrice)
                dataBinding.hargaKirim.text = "Rp. $shipmentPriceFormat"
                dataBinding.ongkirHarga.text = "Rp. $shipmentPriceFormat"
                val totalPriceOnCart: Long = viewModel.mutableCartPrice.value!!.toLong() + viewModel.mutableHargaEkspedisi.value!!.toLong() + viewModel.insurancePrice.value!!.toLong()
                val totalPriceFormat = NumberFormat.getInstance(localeID).format(totalPriceOnCart)
                dataBinding.hargaBottom.text = "Rp. $totalPriceFormat"
            } else if(harga == "0"){
                dataBinding.ongkirHarga.text = "Rp. 0"
                val thePrice = viewModel.mutableCartPrice.value!!.toLong()
                val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
                dataBinding.hargaBottom.text = "Rp. $numberFormat"
                dataBinding.hargaKirim.visibility = View.GONE
                dataBinding.paymentInsurance.visibility = View.GONE
            }
        })

        viewModel.insurancePrice.observe(viewLifecycleOwner, { insurance ->
            if (!insurance.isNullOrEmpty()) {
                if (insurance != "0") {
                    val totalPriceWithInsurance: Long = (viewModel.mutableCartPrice.value!!.toLong() + viewModel.mutableHargaEkspedisi.value!!.toLong() + insurance.toLong())
                    val numberFormat = NumberFormat.getInstance(localeID).format(totalPriceWithInsurance)
                    val insuranceFormat = NumberFormat.getInstance(localeID).format(insurance.toLong())
                    dataBinding.insurancePriceTitle.isVisible = true
                    dataBinding.insurancePrice.isVisible = true
                    dataBinding.insurancePrice.text = "Rp. $insuranceFormat"
                    dataBinding.hargaBottom.text = "Rp. $numberFormat"
                } else {
                    val totalPriceWithoutInsurance = if (viewModel.HargaEkspedisi.value != "0") {
                        viewModel.mutableCartPrice.value!!.toLong() + viewModel.mutableHargaEkspedisi.value!!.toLong()
                    } else {
                        viewModel.mutableCartPrice.value!!.toLong()
                    }
                    val numberFormat = NumberFormat.getInstance(localeID).format(totalPriceWithoutInsurance)
                    dataBinding.paymentInsurance.isChecked = false
                    dataBinding.insurancePriceTitle.isVisible = false
                    dataBinding.insurancePrice.isVisible = false
                    dataBinding.hargaBottom.text = "Rp. $numberFormat"
                }
            }
        })

        viewModel.WaktuPesan.observe(viewLifecycleOwner, { waktu ->
            if(waktu != ""){
                dateStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.dataTanggal.visibility = View.VISIBLE
                dataBinding.namaWaktu.text = waktu
            }
        })

        viewModel.WaktuPesanCustom.observe(viewLifecycleOwner, { custom ->
            if(custom != ""){
                dataBinding.dataTanggal.visibility = View.VISIBLE
                dataBinding.customWaktu.text = custom
            }
        })

        viewModel.totalCart.observe(viewLifecycleOwner, { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalHarga.text = "Rp. $numberFormat"
            if(viewModel.mutableHargaEkspedisi.value != "0"){
                val thePrice1: Long = price.toLong() + viewModel.mutableHargaEkspedisi.value!!.toLong() + viewModel.insurancePrice.value!!.toLong()
                val numberFormat1 = NumberFormat.getInstance(localeID).format(thePrice1)
                dataBinding.hargaBottom.text = "Rp. $numberFormat1"
            }else{
                dataBinding.hargaBottom.text = "Rp. $numberFormat"
            }
        })

        viewModel.BayarPesanan.observe(viewLifecycleOwner, { nama ->
            if (nama != "") {
                paymentStat = true
                if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                    dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
                }
                dataBinding.bayarPesanan.visibility = View.VISIBLE
                dataBinding.bayarPesanDengan.text = nama
            }
        })

        viewModel.custName.observe(viewLifecycleOwner, { name ->
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
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController?.navigate(R.id.action_checkoutFragment_to_manualTxnCartPage)
                }
            })

        dataBinding.topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_manualTxnCartPage)
        }

        dataBinding.btnNext.setOnClickListener {
            if (custStat && kurirStat && dateStat && asalStat && paymentStat){
                viewModel.mutablePayStat.value = dataBinding.payStat.isChecked
                var status = navController?.let { it1 ->
                    viewModel.postOrder(
                        dataBinding.payStat.isChecked,
                        it1,
                        requireActivity(),
                        dataBinding.loadingOverlayCheckout
                    )
                }
            }else{
                Log.e("Fail", "Data Kosong")
            }
        }

        dataBinding.paymentInsurance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.countInsurance(true)
            } else {
                viewModel.countInsurance(false)
            }
        }

        dataBinding.pembayaran.setOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_paymentFragment)
        }

        dataBinding.asal.setOnClickListener {
            AsalFragment().show(requireActivity().supportFragmentManager, "show")
        }

        dataBinding.tanggal.setOnClickListener {
            DateFragment().show(requireActivity().supportFragmentManager, "show")
        }

        dataBinding.pengiriman.setOnClickListener {
            DeliveryFragment().show(requireActivity().supportFragmentManager, "show")
        }
        dataBinding.pelanggan.setOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_manualTxnCustomerPage)
        }
    }
}