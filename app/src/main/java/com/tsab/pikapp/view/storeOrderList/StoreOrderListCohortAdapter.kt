package com.tsab.pikapp.view.storeOrderList

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemStoreOrderListCohortBinding
import com.tsab.pikapp.models.model.OrderDetailDetail
import com.tsab.pikapp.models.model.StoreOrderList


class StoreOrderListCohortAdapter(
    private val storeOrderList: ArrayList<StoreOrderList>,
    private val storeOrderListInterface: StoreOrderListInterface
) : RecyclerView.Adapter<StoreOrderListCohortAdapter.ProductViewHolder>() {

    class ProductViewHolder(var view: ItemStoreOrderListCohortBinding) :
        RecyclerView.ViewHolder(view.root)

    private val productListAdapter = StoreOrderListDetailProductAdapter(arrayListOf())

    fun updateProductList(newOrderList: List<StoreOrderList>) {
        storeOrderList.clear()
        storeOrderList.addAll(newOrderList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemStoreOrderListCohortBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StoreOrderListCohortAdapter.ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_store_order_list_cohort, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = storeOrderList.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.storeOrderList = storeOrderList[position]

        if (storeOrderList[position].status == "PAID") holder.view.textNew.visibility =
            View.VISIBLE else holder.view.textNew.visibility = View.GONE
        holder.view.productRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productListAdapter
        }

        holder.view.productRecyclerView.suppressLayout(true)

        storeOrderList[position].detailProduct?.let {
            val plist: ArrayList<OrderDetailDetail> = arrayListOf()
            plist.addAll(it)
            productListAdapter.productList = plist
        }

        if (storeOrderList[position].bizType == "DINE_IN") {
            holder.view.imageBizType.setImageResource(R.drawable.ic_dinein)
            holder.view.textTableNo.setTextColor(Color.parseColor("#000000"))
            holder.view.textBizType.text = "Makan Di Tempat"
            holder.view.textTableNo.visibility = View.VISIBLE
            holder.view.imageCashier.visibility = View.GONE
            if (storeOrderList[position].tableNo != "0") {
                holder.view.textTableNo.text = "Meja ${storeOrderList[position].tableNo}"
            } else {
                holder.view.textTableNo.text = "Belum dapat nomor meja"
            }
        } else if (storeOrderList[position].bizType == "TAKE_AWAY") {
            holder.view.imageCashier.visibility = View.GONE
            holder.view.imageBizType.setImageResource(R.drawable.ic_takeaway)
            holder.view.textBizType.text = "Bungkus/Take Away"
            holder.view.textTableNo.visibility = View.INVISIBLE
        }

        //belumbayar
        if (storeOrderList[position].status == "OPEN" && storeOrderList[position].paymentWith == "PAY_BY_CASHIER") {
            holder.view.textTableNo.visibility = View.VISIBLE
            holder.view.textTableNo.text = "Belum Bayar"
            holder.view.textTableNo.setTextColor(Color.parseColor("#FF4057"))
            holder.view.imageCashier.visibility = View.VISIBLE
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
