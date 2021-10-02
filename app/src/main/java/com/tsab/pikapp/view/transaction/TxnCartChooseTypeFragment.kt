package com.tsab.pikapp.view.transaction

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnCartChooseTypeBinding
import com.tsab.pikapp.util.SELECTED_TYPE
import com.tsab.pikapp.viewmodel.transaction.TxnCartChooseTypeViewModel


class TxnCartChooseTypeFragment(val dialogDismissInterface: DialogDismissInterface) :
    BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentTxnCartChooseTypeBinding
    private lateinit var viewModel: TxnCartChooseTypeViewModel
    var selected = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_txn_cart_choose_type,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(TxnCartChooseTypeViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mArgs = arguments
        selected = mArgs!!.getString(SELECTED_TYPE).toString()
        if (selected == "DINE_IN") {
            dataBinding.radioDineIn.isChecked = true
        } else if (selected == "TAKE_AWAY") {
            dataBinding.radioTakeAway.isChecked = true
        }

        dataBinding.buttonDineIn.setOnClickListener {
            dataBinding.radioDineIn.isChecked = true
            dataBinding.radioTakeAway.isChecked = false
            selected = "DINE_IN"
        }

        dataBinding.radioDineIn.setOnClickListener {
            dataBinding.radioDineIn.isChecked = true
            dataBinding.radioTakeAway.isChecked = false
            selected = "DINE_IN"
        }

        dataBinding.buttonTakeAway.setOnClickListener {
            dataBinding.radioDineIn.isChecked = false
            dataBinding.radioTakeAway.isChecked = true
            selected = "TAKE_AWAY"
        }

        dataBinding.radioTakeAway.setOnClickListener {
            dataBinding.radioDineIn.isChecked = false
            dataBinding.radioTakeAway.isChecked = true
            selected = "TAKE_AWAY"
        }

        dataBinding.buttonSave.setOnClickListener {
            if (selected.isEmpty()) {
                Toast.makeText(context, "Silakan pilih Tipe pengambilan Anda", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (selected == "TAKE_AWAY") {
                    Toast.makeText(context, "Anda memilih \"Take away\"", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Anda memilih \"Makan di tempat\"", Toast.LENGTH_SHORT)
                        .show()
                }
                viewModel.setType(selected)
            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer {
            if (it) {
                dismiss()
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogDismissInterface.onDialogDismiss()
    }

    interface DialogDismissInterface {
        fun onDialogDismiss()
    }
}