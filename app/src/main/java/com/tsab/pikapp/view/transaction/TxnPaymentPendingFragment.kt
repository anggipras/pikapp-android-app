package com.tsab.pikapp.view.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnCartBinding
import com.tsab.pikapp.databinding.FragmentTxnPaymentPendingBinding
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.view.TransactionActivity
import com.tsab.pikapp.viewmodel.transaction.TxnCartViewModel

class TxnPaymentPendingFragment : Fragment() {

    lateinit var dataBinding: FragmentTxnPaymentPendingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_txn_payment_pending, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.buttonGoHome.setOnClickListener {
            (activity as TransactionActivity).finish()
        }
    }

}