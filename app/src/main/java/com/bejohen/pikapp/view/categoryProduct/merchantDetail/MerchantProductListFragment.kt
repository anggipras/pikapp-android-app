package com.bejohen.pikapp.view.categoryProduct.merchantDetail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bejohen.pikapp.R
import kotlinx.android.synthetic.main.fragment_merchant_product_list.view.*

class MerchantProductListFragment : Fragment() {

    private val ARG_LONG = "ARGLONG"
    var myInt = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=  inflater.inflate(R.layout.fragment_merchant_product_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = arguments?.getLong(ARG_LONG, 1)
        Log.d("Debug", "cat id : " + id)
        view.textCoba.text = id.toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val bundle = this.arguments
        bundle?.let {
            myInt = it.getInt("position", 1)
        }
        Log.d("Debug", "position : " + myInt)
    }

    companion object {

        @JvmStatic
        fun newInstance(myLong: Long) = MerchantProductListFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_LONG, myLong)
            }
        }
    }
}