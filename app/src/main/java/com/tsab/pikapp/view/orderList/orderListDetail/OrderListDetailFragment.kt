package com.tsab.pikapp.view.orderList.orderListDetail

import android.annotation.SuppressLint
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
import com.tsab.pikapp.databinding.FragmentOrderListDetailBinding
import com.tsab.pikapp.models.model.OrderDetail
import com.tsab.pikapp.util.CONST_TRANSACTION_ID
import com.tsab.pikapp.util.rupiahFormat
import com.tsab.pikapp.viewmodel.orderList.OrderListDetailViewModel

class OrderListDetailFragment : BottomSheetDialogFragment() {

    private lateinit var dataBinding: FragmentOrderListDetailBinding
    private lateinit var viewModel: OrderListDetailViewModel

    private val orderListDetailProductAdapter = OrderListDetailProductAdapter(arrayListOf())
    var txnID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_list_detail, container, false)
        viewModel = ViewModelProviders.of(this).get(OrderListDetailViewModel::class.java)
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
            viewModel.getTransactionDetail(txnID)
        } ?: run {
            viewModel.getTransactionDetail()
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.orderDetail.observe(this, Observer { it ->
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

    private fun setUI(it: OrderDetail) {
        dataBinding.textTransactionID.text = it.transactionID
        dataBinding.textTransactionDate.text = it.transactionTime
        dataBinding.textMerchantName.text = it.merchantName
//                dataBinding.merchantAddress.text = it.merchantAddress
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
        } else if(it.paymentWith == "PAY_BY_CASHIER") {
            dataBinding.imagePaymentType.setImageResource(R.drawable.ic_cashier)
            dataBinding.textPaymentType.text = "Bayar di kasir"
        } else {
            dataBinding.imagePaymentType.visibility = View.GONE
            dataBinding.textPaymentType.visibility = View.GONE
        }
        it.products?.let {products ->
            orderListDetailProductAdapter.updateProductList(products)
        }
        dataBinding.textTotalPrice.text = rupiahFormat(it.price!!.toLong())

        when (it.status) {
            "OPEN" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Menunggu Pembayaran"
                if(it.paymentWith == "PAY_BY_CASHIER") {
                    dataBinding.textOrderGuide.visibility = View.VISIBLE
                    dataBinding.textOrderGuide.text = "Silakan tunjukkan transaksi ini ke kasir untuk melakukan pembayaran"
                } else
                    dataBinding.textOrderGuide.visibility = View.GONE
            }
            "PAID" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Menunggu Konfirmasi"
                dataBinding.textOrderGuide.visibility = View.GONE
            }
            "FAILED" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Transaksi Gagal"
                dataBinding.textOrderGuide.visibility = View.VISIBLE
                dataBinding.textOrderGuide.text = "Pembayaran melewati batas waktu"
            }
            "MERCHANT_CONFIRM" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Pesanan Diproses"
                if(it.bizType == "TAKE_AWAY") {
                    dataBinding.textOrderGuide.visibility = View.VISIBLE
                    dataBinding.textOrderGuide.text = "Silakan tunjukan halaman ini ke kasir untuk pengambilan pesanan"
                } else {
                    if(it.tableNo == "0") {
                        dataBinding.textOrderGuide.visibility = View.VISIBLE
                        dataBinding.textOrderGuide.text = "Silakan tunjukan halaman ini ke kasir untuk mendapatkan nomor meja"
                    } else {
                        dataBinding.textOrderGuide.visibility = View.VISIBLE
                        dataBinding.textOrderGuide.text = "Ditunggu yah!"
                    }
                }
            }
            "FINALIZE" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Pesanan Siap"
                if(it.bizType == "TAKE_AWAY") {
                    dataBinding.textOrderGuide.visibility = View.VISIBLE
                    dataBinding.textOrderGuide.text = "Silakan tunjukan halaman ini ke kasir untuk pengambilan pesanan"
                } else {
                    if(it.tableNo == "0") {
                        dataBinding.textOrderGuide.visibility = View.VISIBLE
                        dataBinding.textOrderGuide.text = "Silakan tunjukan halaman ini ke kasir untuk mendapatkan nomor meja"
                    } else {
                        dataBinding.textOrderGuide.visibility = View.GONE
                    }
                }
            }
            "CLOSE" -> {
                dataBinding.buttonPay.visibility = View.GONE
                dataBinding.orderStatusContainer.visibility = View.VISIBLE
                dataBinding.textOrderStatus.text = "Pesanan Selesai"
                dataBinding.textOrderGuide.visibility = View.GONE
            }
        }

    }
}