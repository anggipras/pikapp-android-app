package com.bejohen.pikapp.view.categoryProduct.merchantDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentMerchantDetailBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.home.ItemHomeCategoryDecoration
import com.bejohen.pikapp.viewmodel.categoryProduct.MerchantDetailViewModel


class MerchantDetailFragment : Fragment(), ProductListAdapter.ProductAddInterface {

    private lateinit var viewModel: MerchantDetailViewModel
    private lateinit var dataBinding: FragmentMerchantDetailBinding
    var mid = ""
    private val merchantProductListAdapter = ProductListAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_merchant_detail, container, false)
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
                merchantProductListAdapter.updateMerchantProductList(productList)
            }
        })
    }

    override fun onAdd(pid: String) {
        viewModel.onAddProduct(pid, context as HomeActivity)
    }
}