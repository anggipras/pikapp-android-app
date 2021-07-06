package com.tsab.pikapp.view.categoryProduct.productDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProductDetailBinding
import com.tsab.pikapp.util.rupiahFormat
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.categoryProduct.ProductClickListener
import com.tsab.pikapp.viewmodel.categoryProduct.ProductDetailViewModel

class ProductDetailFragment : Fragment(), ProductClickListener {

    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var dataBinding: FragmentProductDetailBinding
    private var pid = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        dataBinding.listener = this
        viewModel = ViewModelProviders.of(this).get(ProductDetailViewModel::class.java)
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
                dataBinding.toolbar.setTitle("${productDetail.productName} | ${productDetail.merchantName}")
                dataBinding.textProductPrice.text = rupiahFormat(productDetail.productPrice!!.toLong())
            }
        })
        viewModel.cart.observe(this, Observer { cart ->
            setButtonCart(cart)
        })
    }

    override fun onProductClicked(v: View) {
        val mid = dataBinding.listProductMerchantID.text.toString()
        val pName = dataBinding.listProductName.text.toString()
        val pImage = dataBinding.listProductProductImage.text.toString()
        val pPrice = dataBinding.listProductProductPrice.text.toString()
        viewModel.onAddProduct(pid, mid, pName, pImage, pPrice, context as HomeActivity)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCart()
        val buttonFloat: View? = (activity as HomeActivity).findViewById<View>(R.id.buttonCartContainer)
        buttonFloat?.let { it ->
            if (it.isVisible) {
                val param = it.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,10,200)
                it.layoutParams = param
            }
        }
    }

    private fun setButtonCart(status: Boolean) {
        val buttonFloat: View? = (activity as HomeActivity).findViewById<View>(R.id.buttonCartContainer)
        buttonFloat?.let {
            if(status) {
                buttonFloat.visibility = View.VISIBLE
                val param = it.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,10,200)
                it.layoutParams = param
            } else {
                buttonFloat.visibility = View.GONE
            }
        }
    }
}