package com.tsab.pikapp.view.orderList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemOrderListCohortBinding
import com.tsab.pikapp.models.model.OrderList
import com.tsab.pikapp.util.rupiahFormat

class OrderListCohortAdapter(private val orderList: ArrayList<OrderList>, private val orderListInterface: OrderListInterface): RecyclerView.Adapter<OrderListCohortAdapter.ProductViewHolder>() {

    class ProductViewHolder(var view: ItemOrderListCohortBinding) : RecyclerView.ViewHolder(view.root)

    fun updateProductList(newOrderList: List<OrderList>) {
        orderList.clear()
        orderList.addAll(newOrderList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemOrderListCohortBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_order_list_cohort, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = orderList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.orderList = orderList[position]
        val rupiah = rupiahFormat(orderList[position].totalPrice!!)
        holder.view.textOrderDetail.text = "${orderList[position].totalProduct} Produk - ${rupiah}"
        if(orderList[position].bizType == "DINE_IN") {
            holder.view.imageBizType.setImageResource(R.drawable.ic_dinein)
            holder.view.textBizType.text = "Makan Di Tempat"
        } else {
            holder.view.imageBizType.setImageResource(R.drawable.ic_takeaway)
            holder.view.textBizType.text = "Bungkus/Take Away"
        }

        if (orderList[position].paymentWith == "WALLET_OVO") {
            holder.view.imagePaymentType.setImageResource(R.drawable.ic_ovo)
            holder.view.textPaymentType.text = "OVO"
        } else {
            holder.view.imagePaymentType.setImageResource(R.drawable.ic_dana)
            holder.view.textPaymentType.text = "DANA"
        }

        holder.view.orderContainer.setOnClickListener {
            orderListInterface.orderContainerTapped(holder.view.textTransactionID.text.toString())
        }
    }

    interface OrderListInterface {
        fun orderContainerTapped(txnId: String)
    }
}