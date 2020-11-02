package com.tsab.pikapp.view.storeOrderList

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemStoreOrderListCohortBinding
import com.tsab.pikapp.models.model.StoreOrderList
import com.tsab.pikapp.view.orderList.orderListDetail.OrderListDetailProductAdapter


class StoreOrderListCohortAdapter(private val storeOrderList: ArrayList<StoreOrderList>, private val storeOrderListInterface: StoreOrderListInterface): RecyclerView.Adapter<StoreOrderListCohortAdapter.ProductViewHolder>() {

    class ProductViewHolder(var view: ItemStoreOrderListCohortBinding) : RecyclerView.ViewHolder(view.root)

    private val productListAdapter = OrderListDetailProductAdapter(arrayListOf())

    fun updateProductList(newOrderList: List<StoreOrderList>) {
        storeOrderList.clear()
        storeOrderList.addAll(newOrderList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemStoreOrderListCohortBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreOrderListCohortAdapter.ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_store_order_list_cohort, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = storeOrderList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.storeOrderList = storeOrderList[position]

        if (storeOrderList[position].status == "PAID") holder.view.textNew.visibility = View.VISIBLE else holder.view.textNew.visibility = View.GONE
        holder.view.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productListAdapter
        }

        holder.view.productRecyclerView.suppressLayout(true)

        storeOrderList[position].detailProduct?.let {
            productListAdapter.updateProductList(it)
        }

        if(storeOrderList[position].bizType == "DINE_IN") {
            holder.view.imageBizType.setImageResource(R.drawable.ic_dinein)
            holder.view.textBizType.text = "Makan Di Tempat"
            holder.view.textTableNo.visibility = View.VISIBLE
            if(storeOrderList[position].tableNo != "0") {
                holder.view.textTableNo.text = "Meja ${storeOrderList[position].tableNo}"
            } else {
                holder.view.textTableNo.text = "Belum dapat nomor meja"
            }
        } else {
            holder.view.imageBizType.setImageResource(R.drawable.ic_takeaway)
            holder.view.textBizType.text = "Bungkus/Take Away"
            holder.view.textTableNo.visibility = View.INVISIBLE
        }
        holder.view.container.setOnClickListener {
            val txnID = holder.view.textTransactionID.text.toString()
            val tableNo = holder.view.textTransactionTableNo.text.toString()
            val status = holder.view.textTransactionStatus.text.toString()
            storeOrderListInterface.storeOrderListTapped(txnID, tableNo, status)
        }
    }

    interface StoreOrderListInterface {
        fun storeOrderListTapped(txnID: String, tbleNo: String, stts: String)
    }
}
