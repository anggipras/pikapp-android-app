package com.tsab.pikapp.view.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnPaymentPendingBinding
import com.tsab.pikapp.view.TransactionActivity
import com.tsab.pikapp.viewmodel.transaction.TxnCartViewModel

class TxnPaymentPendingFragment : Fragment() {

    lateinit var dataBinding: FragmentTxnPaymentPendingBinding
    lateinit var viewModel: TxnCartViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_txn_payment_pending, container, false)
        viewModel = ViewModelProviders.of(this).get(TxnCartViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPaymentType()
        dataBinding.buttonGoHome.setOnClickListener {
            (activity as TransactionActivity).finish()
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun observeViewModel() {

        viewModel.paymentType.observe(this, Observer {
            if (it == "WALLET_OVO") {
                //take away
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_ovo)
                    textPaymentType.text = "OVO"
                    textHowToPay.text = "Silakan cek notifikasi Anda atau buka aplikasi OVO Anda untuk menyelesaikan pembayaran"
                }
            } else if(it == "WALLET_DANA") {
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_dana)
                    textPaymentType.text = "DANA"
                }
            } else if(it == "PAY_BY_CASHIER") {
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_cashier)
                    textPaymentType.text = "Bayar di kasir"
                    textHowToPay.text = "Silakan tunjukkan transaksi ini ke kasir untuk melakukan pembayaran"
                }
            }
        })
    }



}