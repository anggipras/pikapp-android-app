package com.bejohen.pikapp.view.categoryProduct

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentCategoryBinding
import com.bejohen.pikapp.models.model.ItemHomeCategory
import com.bejohen.pikapp.viewmodel.categoryProduct.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private var categoryId: Long = 0
    private lateinit var dataBinding: FragmentCategoryBinding
    private val merchantListAdapter = MerchantListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            categoryId = CategoryFragmentArgs.fromBundle(it).categoryid
        }
        dataBinding.merchantList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = merchantListAdapter
        }

        dataBinding.categoryRefreshLayout.setOnRefreshListener{
            dataBinding.merchantList.visibility = View.GONE
//            dataBinding.homeBannerSliderLoadingShimmerView.visibility = View.VISIBLE
//            dataBinding.homeCategoryLoadingShimmerView.visibility = View.VISIBLE

            viewModel.fetch(categoryId)
            dataBinding.categoryRefreshLayout.isRefreshing = false
        }

        viewModel.fetch(categoryId)
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.categoryData.observe(this, Observer { category ->
            merchantList.visibility = View.VISIBLE
            category?.let {
                dataBinding.category = category
                viewModel.getMerchant()
            }
        })

        viewModel.merchantResponse.observe(this, Observer { merchant ->
            merchantListAdapter.updateMerchantList(merchant)
        })
    }
}