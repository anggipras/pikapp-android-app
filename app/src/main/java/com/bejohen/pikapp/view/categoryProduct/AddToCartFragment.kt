package com.bejohen.pikapp.view.categoryProduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentAddToCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToCartFragment : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentAddToCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_to_cart, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}