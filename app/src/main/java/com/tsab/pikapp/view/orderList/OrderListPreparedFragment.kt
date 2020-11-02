package com.tsab.pikapp.view.orderList

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentOrderListPreparedBinding
import com.tsab.pikapp.databinding.FragmentOrderListUnpaidBinding
import com.tsab.pikapp.view.OrderListActivity
import com.tsab.pikapp.viewmodel.orderList.OrderListCohortViewModel

class OrderListPreparedFragment : Fragment(), OrderListCohortAdapter.OrderListInterface {

    private lateinit var dataBinding: FragmentOrderListPreparedBinding
    private lateinit var viewModel: OrderListCohortViewModel
    private val orderListCohortAdapter = OrderListCohortAdapter(arrayListOf(), this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_list_prepared, container, false)
        viewModel = ViewModelProviders.of(this).get(OrderListCohortViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getOrderList()

        dataBinding.orderListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderListCohortAdapter
        }

        dataBinding.orderListRefreshlayout.setOnRefreshListener {
            viewModel.getOrderList()
            dataBinding.orderListRefreshlayout.isRefreshing = false
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.orderListRetrieve.observe(this, Observer { it ->
            if (it) {
                viewModel.setPrepared()
            } else {
                dataBinding.textError.text = "Wah kosong nih, yuk belanja dulu!"
                dataBinding.textError.visibility = View.VISIBLE
            }
        })

        viewModel.prepared.observe(this, Observer {
            orderListCohortAdapter.updateProductList(it)
            if (it.isNotEmpty()) {
                dataBinding.textError.visibility = View.GONE
            } else {
                dataBinding.textError.text = "Wah kosong nih, yuk belanja dulu!"
                dataBinding.textError.visibility = View.VISIBLE
            }
        })

        viewModel.loading.observe(this, Observer {
            if(it) {
                dataBinding.loadingView.visibility = View.VISIBLE
            } else dataBinding.loadingView.visibility = View.GONE
        })

        viewModel.errorResponse.observe(this, Observer {
            if(it.errCode == "EC0021") {
                Toast.makeText(activity as OrderListActivity, "Kamu login di perangkat lain. Silakan login kembali", Toast.LENGTH_SHORT).show()
                viewModel.clearSession(activity as OrderListActivity)
                viewModel.goToOnboardingFromOrderList(activity as OrderListActivity)
            }
        })
    }

    override fun orderContainerTapped(txnId: String) {
        viewModel.goToOrderListDetail(txnId, activity as OrderListActivity)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setPrepared()
    }
}