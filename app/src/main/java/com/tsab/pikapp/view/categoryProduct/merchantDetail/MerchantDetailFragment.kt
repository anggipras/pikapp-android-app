package com.tsab.pikapp.view.categoryProduct.merchantDetail

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentMerchantDetailBinding
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.home.ItemHomeCategoryDecoration
import com.tsab.pikapp.viewmodel.categoryProduct.MerchantDetailViewModel

class MerchantDetailFragment : Fragment(), ProductListAdapter.ProductAddInterface {

    private lateinit var viewModel: MerchantDetailViewModel
    private lateinit var dataBinding: FragmentMerchantDetailBinding
    var mid = ""
    var ispulled = false
    private val merchantProductListAdapter = ProductListAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_merchant_detail, container, false)
        viewModel = ViewModelProviders.of(this).get(MerchantDetailViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            mid = MerchantDetailFragmentArgs.fromBundle(it).mid
        }
        dataBinding.merchantProductList.apply {
            val gridLayoutManager =
                GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
            val spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing)
            addItemDecoration(ItemHomeCategoryDecoration(spacingInPixels))
            adapter = merchantProductListAdapter
        }
        viewModel.getMerchantDetail(mid)
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.merchantDetailResponse.observe(this, Observer { merchantDetail ->
            merchantDetail?.let {
                dataBinding.merchantDetail = merchantDetail
                dataBinding.toolbar.setTitle(merchantDetail.merchantName)
            }
        })

        viewModel.productListResponse.observe(this, Observer { productList ->
            productList?.let {
                if (!ispulled) {
                    merchantProductListAdapter.updateMerchantProductList(productList)
                    ispulled = true
                }
            }
        })

        viewModel.cart.observe(this, Observer {status ->
            val buttonFloat: View? = (activity as HomeActivity).findViewById<View>(R.id.buttonCartContainer)
            buttonFloat?.let {
                if (status) {
                    buttonFloat.visibility= View.VISIBLE
                    setButtonCartPosition()
                } else buttonFloat.visibility= View.GONE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCart()
        setButtonCartPosition()
    }

    private fun setButtonCartPosition() {
        val buttonFloat: View? =
            (activity as HomeActivity).findViewById<View>(R.id.buttonCartContainer)
        buttonFloat?.let { it ->
            if (it.isVisible) {
                val param = it.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0, 0, 10, 160)
                it.layoutParams = param
            }
        }
    }
    override fun onAdd(mid: String, pid: String, pName: String, pImage: String, pPrice: String) {
        viewModel.onAddProduct(mid, pid, pName, pImage, pPrice, context as HomeActivity)
    }
}