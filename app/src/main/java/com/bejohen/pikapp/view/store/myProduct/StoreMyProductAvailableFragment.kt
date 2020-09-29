package com.bejohen.pikapp.view.store.myProduct

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
import com.bejohen.pikapp.databinding.FragmentStoreMyProductAvailableBinding
import com.bejohen.pikapp.viewmodel.store.StoreProductListViewModel

class StoreMyProductAvailableFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreMyProductAvailableBinding
    private lateinit var viewModel: StoreProductListViewModel
    private val myProductListAdapter = MyProductListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_store_my_product_available,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(StoreProductListViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.myProductAvailList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myProductListAdapter
        }

        viewModel.getProductList()
        observeViewModel()

    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.storeProductListResponse.observe(this, Observer { it ->
            myProductListAdapter.updateProductList(it)
        })

    }
}