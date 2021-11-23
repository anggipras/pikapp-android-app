package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel
import java.text.NumberFormat
import java.util.*

class CheckoutFragment : Fragment() {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentCheckoutBinding
    private var navController: NavController? = null
    private var bool: Boolean = true
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

        viewModel.totalCart.observe(viewLifecycleOwner, Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalHarga.text = "Rp. $numberFormat"
            dataBinding.hargaBottom.text = "Rp. $numberFormat"
        })

        viewModel.custName.observe(viewLifecycleOwner, Observer { name ->
            if (name != ""){
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
        
        dataBinding.kirimBtn.setOnClickListener {
            DeliveryFragment().show(requireActivity().supportFragmentManager, "show")
        }
        dataBinding.pelanggan.setOnClickListener {
            navController?.navigate(R.id.action_checkoutFragment_to_manualTxnCustomerPage)
        }
    }
}