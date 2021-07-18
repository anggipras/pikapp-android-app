package com.tsab.pikapp.view.store

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
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreHomeBinding
import com.tsab.pikapp.view.HomeActivity
import com.tsab.pikapp.view.StoreActivity
import com.tsab.pikapp.view.UserExclusiveActivity
import com.tsab.pikapp.viewmodel.store.StoreHomeViewModel

class StoreHomeFragment : Fragment() {

    private lateinit var dataBinding: FragmentStoreHomeBinding
    private lateinit var viewModel : StoreHomeViewModel
    private var isFirstTime = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_home, container, false)
        viewModel = ViewModelProviders.of(this).get(StoreHomeViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMerchantDetail()
        viewModel.checkNotification()

        dataBinding.buttonPrepare.setOnClickListener {
            viewModel.goToOrderList(activity as StoreActivity, 0)
        }
        dataBinding.buttonPesananSiap.setOnClickListener {
            viewModel.goToOrderList(activity as StoreActivity, 1)
        }
        dataBinding.buttonPesananSelesai.setOnClickListener {
            viewModel.goToOrderList(activity as StoreActivity, 2)
        }
        dataBinding.buttonMyProduct.setOnClickListener {
            val action = StoreHomeFragmentDirections.actionToStoreMyProductFragment()
            Navigation.findNavController(view).navigate(action)
        }
        dataBinding.logoutBtn.setOnClickListener {
            viewModel.logout(activity as StoreActivity)
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

        viewModel.notificationActive.observe(this, Observer {
            if (it) {
                isFirstTime = false
                viewModel.goToOrderList(activity as StoreActivity)
            }
        })

        viewModel.logoutResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.clearSessionExclusive(activity as StoreActivity)
            }
        })

        viewModel.errorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })

    }
}