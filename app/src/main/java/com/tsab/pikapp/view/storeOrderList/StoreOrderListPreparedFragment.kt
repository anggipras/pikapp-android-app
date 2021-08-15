package com.tsab.pikapp.view.storeOrderList

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreOrderListPreparedBinding
import com.tsab.pikapp.view.StoreOrderListActivity
import com.tsab.pikapp.viewmodel.storeOrderList.StoreOrderListCohortViewModel

class StoreOrderListPreparedFragment : Fragment(),
    StoreOrderListCohortAdapter.StoreOrderListInterface {

    private lateinit var dataBinding: FragmentStoreOrderListPreparedBinding
    private lateinit var viewModel: StoreOrderListCohortViewModel
    private val storeOrderListCohortAdapter = StoreOrderListCohortAdapter(arrayListOf(), this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_store_order_list_prepared,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(StoreOrderListCohortViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getStoreOrderList()

        dataBinding.storeOrderListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storeOrderListCohortAdapter
        }

        dataBinding.storeOrderListRefreshlayout.setOnRefreshListener {
            viewModel.getStoreOrderList()
            dataBinding.storeOrderListRefreshlayout.isRefreshing = false
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.storeOrderListRetrieve.observe(this, Observer { it ->
            if (it) {
                viewModel.setPrepared()
            } else {
                dataBinding.textError.text = "Belum ada transaksi"
                dataBinding.textError.visibility = View.VISIBLE
            }
        })

        viewModel.prepared.observe(this, Observer {
            storeOrderListCohortAdapter.updateProductList(it)
            if (it.isNotEmpty()) {
                dataBinding.textError.visibility = View.GONE
            } else {
                dataBinding.textError.text = "Belum ada transaksi"
                dataBinding.textError.visibility = View.VISIBLE
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                dataBinding.loadingView.visibility = View.VISIBLE
            } else dataBinding.loadingView.visibility = View.GONE
        })

        viewModel.errorResponse.observe(this, Observer {
            if (it.errCode == "EC0021") {
                Toast.makeText(
                    activity as StoreOrderListActivity,
                    "Kamu login di perangkat lain. Silakan login kembali",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearSession(activity as StoreOrderListActivity)
                viewModel.goToOnboardingFromStoreOrderList(activity as StoreOrderListActivity)
            }
        })

        viewModel.updateStatus.observe(this, Observer {
            if (it) {
                viewModel.getStoreOrderList()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPrepared()
    }

    override fun storeOrderListTapped(txnID: String, tbleNo: String, stts: String) {
        viewModel.updateOrderStatus(txnID, stts)
        viewModel.goToStoreOrderListDetail(txnID, tbleNo, activity as StoreOrderListActivity)
    }
}