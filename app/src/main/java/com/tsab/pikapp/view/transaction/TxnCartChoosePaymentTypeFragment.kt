package com.tsab.pikapp.view.transaction

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnCartChoosePaymentTypeBinding
import com.tsab.pikapp.util.SELECTED_PAYMENT
import com.tsab.pikapp.viewmodel.transaction.TxnCartChoosePaymentTypeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TxnCartChoosePaymentTypeFragment(val dialogDismissInterface: DialogDismissInterface) : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentTxnCartChoosePaymentTypeBinding
    private lateinit var viewModel: TxnCartChoosePaymentTypeViewModel
    var selected = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_txn_cart_choose_payment_type, container, false)
        viewModel = ViewModelProviders.of(this).get(TxnCartChoosePaymentTypeViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mArgs = arguments
        selected = mArgs!!.getString(SELECTED_PAYMENT).toString()

        if (selected == "WALLET_OVO"){
            dataBinding.radioOvo.isChecked = true
        } else if (selected == "WALLET_DANA") {
            dataBinding.radioDana.isChecked = true
        }

        dataBinding.buttonOvo.setOnClickListener{
            dataBinding.radioOvo.isChecked = true
            dataBinding.radioDana.isChecked = false
            selected = "WALLET_OVO"
        }

        dataBinding.radioOvo.setOnClickListener{
            dataBinding.radioOvo.isChecked = true
            dataBinding.radioDana.isChecked = false
            selected = "WALLET_OVO"
        }

        dataBinding.buttonDana.setOnClickListener{
            dataBinding.radioOvo.isChecked = false
            dataBinding.radioDana.isChecked = true
            selected = "WALLET_DANA"
        }

        dataBinding.radioDana.setOnClickListener{
            dataBinding.radioOvo.isChecked = false
            dataBinding.radioDana.isChecked = true
            selected = "WALLET_DANA"
        }

        dataBinding.buttonSave.setOnClickListener {
            viewModel.setPaymentType(selected)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer {
            if(it) {
                dismiss()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissInterface.onPaymentDialogDismiss()
    }

    interface DialogDismissInterface {
        fun onPaymentDialogDismiss()
    }
}