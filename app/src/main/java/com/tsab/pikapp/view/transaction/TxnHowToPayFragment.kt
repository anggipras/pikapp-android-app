package com.tsab.pikapp.view.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnHowToPayBinding

class TxnHowToPayFragment : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentTxnHowToPayBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_txn_cart_choose_type, container, false)
        return inflater.inflate(R.layout.fragment_txn_how_to_pay, container, false)
    }
}