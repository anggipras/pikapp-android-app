package com.bejohen.pikapp.view.store

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentStoreHomeBinding
import com.bejohen.pikapp.viewmodel.store.StoreHomeViewModel

class StoreHomeFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreHomeBinding
    private lateinit var viewModel : StoreHomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_store_home, container, false)
        viewModel = ViewModelProviders.of(this).get(StoreHomeViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMerchantDetail()

        dataBinding.buttonMyProduct.setOnClickListener {
            val action = StoreHomeFragmentDirections.actionToStoreMyProductFragment()
            Navigation.findNavController(view).navigate(action)
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.merchantDetailResponse.observe(this, Observer { merchantDetail ->
            merchantDetail?.let {
                dataBinding.merchantDetail = merchantDetail
                dataBinding.toolbar.title = merchantDetail.merchantName
            }
        })
    }
}