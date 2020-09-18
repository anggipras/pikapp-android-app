package com.bejohen.pikapp.view.categoryProduct

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentAddToCartBinding
import com.bejohen.pikapp.util.MERCHANT_ID
import com.bejohen.pikapp.util.PRODUCT_ID
import com.bejohen.pikapp.viewmodel.categoryProduct.AddToCartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_add_to_cart.*

class AddToCartFragment : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentAddToCartBinding
    private lateinit var viewModel: AddToCartViewModel
    var qty = 1
    var pid = ""
    var mid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_to_cart, container, false)
        viewModel = ViewModelProviders.of(this).get(AddToCartViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mArgs = arguments
        pid = mArgs!!.getString(PRODUCT_ID).toString()
        mid = mArgs!!.getString(MERCHANT_ID).toString()

        dataBinding.buttonDecrement.setOnClickListener {
            if (qty == 1) {

            } else {
                viewModel.decreaseQty(qty)
            }
        }

        dataBinding.buttonIncrement.setOnClickListener {
            viewModel.increaseQty(qty)
        }

        dataBinding.buttonAddToCart.setOnClickListener {

        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.quantity.observe(this, Observer { q ->
            qty?.let {
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
    }


}