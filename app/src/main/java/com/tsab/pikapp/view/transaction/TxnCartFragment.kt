package com.tsab.pikapp.view.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentTxnCartBinding
import com.tsab.pikapp.models.model.CartModel
import com.tsab.pikapp.util.rupiahFormat
import com.tsab.pikapp.view.TransactionActivity
import com.tsab.pikapp.viewmodel.transaction.TxnCartViewModel

class TxnCartFragment : Fragment(), TxnCartProductListAdapter.CartListInterface {

    private val txnCartProductListAdapter = TxnCartProductListAdapter(arrayListOf(), this)
    lateinit var dataBinding: FragmentTxnCartBinding
    lateinit var viewModel: TxnCartViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_txn_cart, container, false)
        viewModel = ViewModelProviders.of(this).get(TxnCartViewModel::class.java)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.validateUser()
        dataBinding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = txnCartProductListAdapter
        }
        dataBinding.buttonPay.isEnabled = false


        dataBinding.selectTypeContainer.setOnClickListener {
            viewModel.goToSelectType(activity as TransactionActivity)
        }

        dataBinding.paymentContainer.setOnClickListener {
            viewModel.goToPaymentType(activity as TransactionActivity)
        }

        dataBinding.buttonPay.setOnClickListener {
            viewModel.doPay(dataBinding.txtTotalPrice.text.toString())
        }
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun observeViewModel() {

        viewModel.userSuccess.observe(this, Observer {
            if (it) {
                viewModel.getCart()
                viewModel.checkType()
                viewModel.getPaymentType()
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                dataBinding.progressBar.visibility = View.VISIBLE
                dataBinding.buttonPay.isEnabled = false
            } else {
                dataBinding.progressBar.visibility = View.GONE
                dataBinding.buttonPay.isEnabled = true
            }

        })

        viewModel.cartType.observe(this, Observer {
            if (it == "TAKE_AWAY") {
                //take away
                dataBinding.apply {
                    imageSelectedType.setImageResource(R.drawable.ic_takeaway)
                    textSelectedType.text = "Bungkus/Take Away"
                    buttonPay.background = ContextCompat.getDrawable(
                        activity as TransactionActivity,
                        R.drawable.button_yellow
                    )
                    buttonPay.isEnabled = true
                    dataBinding.buttonPay.isClickable = true
                }
            } else if (it == "DINE_IN") {
                //dine in
                dataBinding.apply {
                    imageSelectedType.setImageResource(R.drawable.ic_dinein)
                    textSelectedType.text = "Makan di tempat"
                    buttonPay.background = ContextCompat.getDrawable(
                        activity as TransactionActivity,
                        R.drawable.button_yellow
                    )
                    buttonPay.isEnabled = true
                    dataBinding.buttonPay.isClickable = true

                }
            } else if (it == "") {
                dataBinding.buttonPay.isEnabled = false
                dataBinding.buttonPay.isClickable = false
                dataBinding.buttonPay.background = ContextCompat.getDrawable(
                    activity as TransactionActivity,
                    R.drawable.button_gray
                )
            }
        })

        viewModel.errorResponse.observe(this, Observer {
            if (it.errCode == "EC0021") {
                Toast.makeText(
                    activity as TransactionActivity,
                    "Kamu login di perangkat lain. Silakan login kembali",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearSession(activity as TransactionActivity)
                viewModel.goToOnboardingFromTransaction(activity as TransactionActivity)
            } else if (it.errCode == "EC0027") {
                Toast.makeText(
                    activity as TransactionActivity,
                    "Akun Anda belum aktif. Silakan aktifkan akun Anda dengan verifikasi email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.paymentType.observe(this, Observer {
            if (it == "WALLET_OVO") {
                //take away
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_ovo)
                    textPaymentType.text = "OVO"
                }
            } else if (it == "WALLET_DANA") {
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_dana)
                    textPaymentType.text = "DANA"
                }
            } else if (it == "PAY_BY_CASHIER") {
                dataBinding.apply {
                    imagePaymentType.setImageResource(R.drawable.ic_cashier)
                    textPaymentType.text = "Bayar di kasir"
                }
            }
        })

        viewModel.phoneNumber.observe(this, Observer {
            if (it.isNotEmpty()) {
                dataBinding.apply {
                    textPhoneNumber.visibility = View.VISIBLE
                    textPhoneNumber.text = "Nomor Anda : $it "
                }
            } else
                dataBinding.textPhoneNumber.visibility = View.GONE
        })

        viewModel.merchantDetailResponse.observe(this, Observer {
            dataBinding.merchantName.text = it.merchantName
            dataBinding.merchantAddress.text = it.merchantAddress
            dataBinding.merchantDistance.text = it.merchantDistance
        })

        viewModel.cartList.observe(this, Observer { cartList ->
            if (cartList.isNotEmpty()) {
                viewModel.getMerchant()
                txnCartProductListAdapter.updateProductList(cartList)
                setTotalPrice(cartList)
            } else {
                viewModel.setCartEmpty()
                dataBinding.cartContainer.visibility = View.GONE
                dataBinding.textError.visibility = View.VISIBLE
                dataBinding.textError.text = "Pesanan kamu kosong, silakan kembali"
            }
        })

        viewModel.transactionSucccess.observe(this, Observer {
            viewModel.goToPaymentPending(dataBinding.root)
        })

        viewModel.txnFail.observe(this, Observer {
            if (it.errCode == "EC0021") {
                Toast.makeText(
                    activity as TransactionActivity,
                    "Kamu login di perangkat lain. Silakan login kembali",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.clearSession(activity as TransactionActivity)
                viewModel.goToOnboardingFromTransaction(activity as TransactionActivity)
            } else if (it.errCode == "EC0027") {
                Toast.makeText(
                    activity as TransactionActivity,
                    "Akun Anda belum aktif. Silakan aktifkan akun Anda dengan verifikasi email",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setTotalPrice(cartList: List<CartModel>) {
        var totalPrice = 0
        for (cart in cartList) {
            cart.totalPrice?.let {
                totalPrice += it
            }
        }
        dataBinding.txtTotalPrice.text = totalPrice.toString()
        dataBinding.textTotalPrice.text = rupiahFormat(totalPrice.toLong())
    }

    override fun increaseQty(product: CartModel) {
        viewModel.increaseQty(product)
    }

    override fun decreaseQty(product: CartModel) {
        viewModel.decreaseQty(product)
    }

    override fun editNote(product: CartModel) {

    }

    override fun deleteProduct(product: CartModel) {
        val builder = AlertDialog.Builder(activity as TransactionActivity)
        builder.apply {
            setTitle("Hapus Pesanan")
            setMessage("Anda yakin akan menghapus pesanan \"${product.productName}\"?")
            setPositiveButton("Hapus") { dialog, which ->
                // Do something when user press the positive button
                viewModel.deleteProduct(product)
            }
            builder.setNegativeButton("Kepencet") { dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}