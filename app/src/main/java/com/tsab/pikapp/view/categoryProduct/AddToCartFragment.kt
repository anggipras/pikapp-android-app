package com.tsab.pikapp.view.categoryProduct

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentAddToCartBinding
import com.tsab.pikapp.models.model.CartModel
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.viewmodel.categoryProduct.AddToCartViewModel
import kotlinx.android.synthetic.main.fragment_add_to_cart.*

class AddToCartFragment(val dialogDismissInterface: DialogDismissInterface) :
    BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentAddToCartBinding
    private lateinit var viewModel: AddToCartViewModel
    var qty = 1
    var pid = ""
    var pName = ""
    var pPrice = ""
    var mid = ""
    var pImage = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_to_cart, container, false)
        viewModel = ViewModelProviders.of(this).get(AddToCartViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mArgs = arguments
        mArgs?.let {
            pid = it.getString(PRODUCT_ID).toString()
            pName = it.getString(PRODUCT_NAME).toString()
            mid = it.getString(MERCHANT_ID).toString()
            pImage = it.getString(PRODUCT_IMAGE).toString()
            pPrice = it.getString(PRODUCT_PRICE).toString()
        }
        viewModel.getCart(pid)

        dataBinding.buttonDecrement.setOnClickListener {
            if (qty != 1) viewModel.decreaseQty(qty)
        }

        dataBinding.buttonIncrement.setOnClickListener {
            viewModel.increaseQty(qty)
        }

        dataBinding.buttonAddToCart.setOnClickListener {
            viewModel.validateCart(
                mid,
                pid,
                pName,
                pImage,
                qty,
                pPrice,
                dataBinding.editTextMultiLine.text.trim().toString()
            )
        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.quantity.observe(this, Observer { q ->
            qty.let {
                qty = q
                dataBinding.textQuantity.text = qty.toString()
                if (qty > 1) {
                    buttonDecrement.isEnabled = true
                    buttonDecrement.setBackgroundResource(R.drawable.button_green_rounded)
                    buttonDecrement.setTextColor(Color.parseColor("#ffffff"))
                } else {
                    buttonDecrement.isEnabled = false
                    buttonDecrement.setBackgroundResource(R.drawable.button_darkgray_rounded)
                    buttonDecrement.setTextColor(Color.parseColor("#000000"))
                }
            }
        })

        viewModel.note.observe(this, Observer { note ->
            dataBinding.editTextMultiLine.setText(note)
        })

        viewModel.differentCart.observe(this, Observer {
            if (it.isNotEmpty()) {
                requestPermission(it[0])
            }
        })

        viewModel.addedToCart.observe(this, Observer {
            if (it) {
                viewModel.createToastShort(
                    (activity as HomeActivity).application,
                    "Barang berhasil masuk ke keranjang"
                )
                this.dismiss()
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

    private fun requestPermission(cartModel: CartModel) {
        val builder = AlertDialog.Builder(activity as HomeActivity)
        builder.apply {
            setTitle("Wah merchantnya beda nih")
            setMessage("Kamu yakin mau ganti merchant?")
            setPositiveButton("Ganti") { dialog, which ->
                // Do something when user press the positive button
                viewModel.resetCart(cartModel)
            }
            builder.setNegativeButton("Kepencet") { dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}