package com.tsab.pikapp.view.storeOrderList

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentStoreOrderListDetailBinding
import com.tsab.pikapp.models.model.OrderDetail
import com.tsab.pikapp.models.model.StoreOrderList
import com.tsab.pikapp.util.CONST_TABLE_NO
import com.tsab.pikapp.util.CONST_TRANSACTION_ID
import com.tsab.pikapp.util.rupiahFormat
import com.tsab.pikapp.view.orderList.orderListDetail.OrderListDetailProductAdapter
import com.tsab.pikapp.viewmodel.orderList.OrderListDetailViewModel
import com.tsab.pikapp.viewmodel.storeOrderList.StoreOrderListDetailViewModel

class StoreOrderListDetailFragment(val storeOrderListDetailInterface: StoreOrderListDetailInterface) : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentStoreOrderListDetailBinding
    private lateinit var viewModel: StoreOrderListDetailViewModel

    private val orderListDetailProductAdapter = OrderListDetailProductAdapter(arrayListOf())
    var txnID = ""
    var tableNo = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_order_list_detail, container, false)
        viewModel = ViewModelProviders.of(this).get(StoreOrderListDetailViewModel::class.java)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderListDetailProductAdapter
        }

        val mArgs = arguments
        mArgs?.let {
            txnID = it.getString(CONST_TRANSACTION_ID).toString()
            tableNo = it.getString(CONST_TABLE_NO).toString()
            viewModel.getTransactionDetail(txnID, tableNo)
        }

        dataBinding.buttonUpdateStatusReady.setOnClickListener {
            storeOrderListDetailInterface.changeOrderStatus(txnID, "MERCHANT_CONFIRM")
            dismiss()
        }

        dataBinding.buttonUpdateStatusFinish.setOnClickListener {
            storeOrderListDetailInterface.changeOrderStatus(txnID, "FINALIZE")
            dismiss()
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.storeOrderDetail.observe(this, Observer { it ->
            if (it.transactionID!!.isNotEmpty()) {
                setUI(it)
            }
        })
        viewModel.loading.observe(this, Observer {
            if(it) {
                dataBinding.containerAll.visibility = View.INVISIBLE
            } else dataBinding.containerAll.visibility = View.VISIBLE
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        storeOrderListDetailInterface.onDialogDismiss()
    }

    private fun setUI(it: StoreOrderList) {
        dataBinding.textTransactionID.text = it.transactionID
        dataBinding.textTransactionDate.text = it.transactionTime
        dataBinding.textMerchantName.text = it.customerName

        dataBinding.textBizType.text = it.bizType
        if(it.bizType == "DINE_IN") {
            dataBinding.tableContainer.visibility = View.VISIBLE
            dataBinding.textBizType.text = "Makan di Tempat"
            dataBinding.imageBizType.setImageResource(R.drawable.ic_dinein)
            dataBinding.TableNo.text = it.tableNo
        } else {
            dataBinding.tableContainer.visibility = View.GONE
            dataBinding.textBizType.text = "Bungkus/Take Away"
            dataBinding.imageBizType.setImageResource(R.drawable.ic_takeaway)
        }
        if (it.paymentWith == "WALLET_DANA") {
            dataBinding.imagePaymentType.setImageResource(R.drawable.ic_dana)
            dataBinding.textPaymentType.text = "DANA"
        } else if(it.paymentWith == "WALLET_OVO") {
            dataBinding.imagePaymentType.setImageResource(R.drawable.ic_ovo)
            dataBinding.textPaymentType.text = "OVO"
        } else {
            dataBinding.imagePaymentType.visibility = View.GONE
            dataBinding.textPaymentType.visibility = View.GONE
        }
        it.detailProduct?.let {products ->
            orderListDetailProductAdapter.updateProductList(products)
        }
        dataBinding.detailPaymentContainer.visibility = View.GONE
        dataBinding.spacePayment.visibility = View.GONE

        if(it.status == "PAID") {
            dataBinding.buttonUpdateStatusReady.visibility = View.GONE
            dataBinding.buttonUpdateStatusFinish.visibility = View.GONE
        } else if(it.status == "MERCHANT_CONFIRM") {
            dataBinding.buttonUpdateStatusReady.visibility = View.VISIBLE
            dataBinding.buttonUpdateStatusFinish.visibility = View.GONE
        } else if(it.status == "FINALIZE") {
            dataBinding.buttonUpdateStatusReady.visibility = View.GONE
            dataBinding.buttonUpdateStatusFinish.visibility = View.VISIBLE
        }
    }

    interface StoreOrderListDetailInterface {
        fun changeOrderStatus(txnID: String, stts: String)
        fun onDialogDismiss()
    }
}