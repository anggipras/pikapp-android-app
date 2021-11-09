package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel

class ManualTxnDetail : RoundedBottomSheetDialogFragment() {
    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_txn_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}