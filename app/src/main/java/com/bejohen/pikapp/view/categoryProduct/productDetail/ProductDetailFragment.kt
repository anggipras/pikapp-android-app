package com.bejohen.pikapp.view.categoryProduct.productDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentProductDetailBinding
import com.bejohen.pikapp.util.rupiahFormat
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.categoryProduct.ProductClickListener
import com.bejohen.pikapp.viewmodel.categoryProduct.ProductDetailViewModel

class ProductDetailFragment : Fragment(), ProductClickListener {

    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var dataBinding: FragmentProductDetailBinding
    var pid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        dataBinding.listener = this
        viewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            pid = ProductDetailFragmentArgs.fromBundle(it).pid
        }
        viewModel.getProductDetail(pid)
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.productDetailResponse.observe(this, Observer { productDetail ->
            productDetail?.let {
                dataBinding.productDetail = productDetail
                dataBinding.toolbar.setTitle(productDetail.productName)
                dataBinding.textProductPrice.text = rupiahFormat(productDetail.productPrice!!)
            }
        })
    }

    override fun onProductClicked(v: View) {
        val mid = dataBinding.listProductMerchantID.text.toString()
        viewModel.onAddProduct(pid, mid, context as HomeActivity)
    }
}