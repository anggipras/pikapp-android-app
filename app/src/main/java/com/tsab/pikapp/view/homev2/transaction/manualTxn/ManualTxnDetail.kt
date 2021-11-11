package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnCartPageBinding
import com.tsab.pikapp.databinding.FragmentManualTxnDetailBinding
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import java.text.NumberFormat
import java.util.*

class ManualTxnDetail : RoundedBottomSheetDialogFragment() {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private val localeID =  Locale("in", "ID")
    private lateinit var dataBinding: FragmentManualTxnDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.totalItems.observe(viewLifecycleOwner, androidx.lifecycle.Observer { totalItems ->
            dataBinding.totalPriceText.text = "Total Harga ($totalItems Item)"
        })

        viewModel.totalCart.observe(viewLifecycleOwner, androidx.lifecycle.Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalPrice.text = "Rp. $numberFormat"
            dataBinding.finalPrice.text = "Rp. $numberFormat"
        })
    }

}