package com.bejohen.pikapp.view.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentTxnCartBinding
import com.bejohen.pikapp.models.model.CartModel
import com.bejohen.pikapp.util.rupiahFormat
import com.bejohen.pikapp.view.StoreActivity
import com.bejohen.pikapp.view.TransactionActivity
import com.bejohen.pikapp.viewmodel.transaction.TxnCartViewModel

class TxnCartFragment : Fragment(), TxnCartProductListAdapter.CartListInterface {

    lateinit var dataBinding: FragmentTxnCartBinding
    lateinit var viewModel: TxnCartViewModel
    private val txnCartProductListAdapter = TxnCartProductListAdapter(arrayListOf(), this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_txn_cart, container, false)
        viewModel = ViewModelProviders.of(this).get(TxnCartViewModel::class.java)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = txnCartProductListAdapter
        }
        viewModel.getCart()
        viewModel.getMerchant()
        viewModel.checkType()
        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun observeViewModel() {
        viewModel.tableNumber.observe(this, Observer {
            if(it < 0) {
                //default
            } else if (it == 0) {
                //take away
                dataBinding.apply {
                    imageSelectedType.setImageResource(R.drawable.bghome)
                    textSelectedType.text = "Take Away"
                }
            } else if(it > 0) {
                //dine in
                dataBinding.apply {
                    imageSelectedType.setImageResource(R.drawable.bghome)
                    textSelectedType.text = "Dine"
                }
            }
        })
        viewModel.merchantDetailResponse.observe(this, Observer {
            dataBinding.merchantName.text = it.merchantName
            dataBinding.merchantAddress.text = it.merchantAddress
            dataBinding.merchantDistance.text = it.merchantDistance
        })
        viewModel.cartList.observe(this, Observer { cartList ->
            if(cartList.isNotEmpty()) {
                txnCartProductListAdapter.updateProductList(cartList)
                setTotalPrice(cartList)
            } else {
                viewModel.setCartEmpty()
                dataBinding.cartContainer.visibility = View.GONE
                dataBinding.textError.visibility = View.VISIBLE
                dataBinding.textError.text = "Pesanan kamu kosong, silakan kembali"
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