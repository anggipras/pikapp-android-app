package com.bejohen.pikapp.view.categoryProduct

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentCategoryBinding
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.viewmodel.categoryProduct.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment(), MerchantListAdapter.MerchantClickInterface {

    private lateinit var viewModel: CategoryViewModel
    private var categoryId: Long = 0
    private lateinit var dataBinding: FragmentCategoryBinding
    var isPulled = false
    private val merchantListAdapter = MerchantListAdapter(arrayListOf(), this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

        viewModel.getLocation(activity as HomeActivity)

        viewModel.fetch(categoryId)

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.locationResponse.observe(this, Observer { it ->
            dataBinding.textCurrentLocation.text = it
        })

        viewModel.categoryData.observe(this, Observer { category ->
            merchantList.visibility = View.VISIBLE
            category?.let {
                dataBinding.category = category
                if(!isPulled) viewModel.getMerchant()
            }
        })

        viewModel.merchantResponse.observe(this, Observer { merchant ->
            if (!isPulled) {
                merchantListAdapter.updateMerchantList(merchant)
                isPulled = true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCart()
        val buttonFloat: View? = (activity as HomeActivity).findViewById<View>(R.id.buttonCartContainer)
        buttonFloat?.let { it ->
            if (it.isVisible) {
                val param = it.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,10,30)
                it.layoutParams = param
            }
        }
    }

    override fun onClickMerchant(mid: String) {
        viewModel.goToMerchantDetail(mid, dataBinding.root)
    }

    override fun onClickProductSmall(pid: String) {
        viewModel.goToProductDetail(pid, dataBinding.root)
    }
}