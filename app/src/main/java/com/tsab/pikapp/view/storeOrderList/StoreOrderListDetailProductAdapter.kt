package com.tsab.pikapp.view.storeOrderList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemOrderListProductNoimageBinding
import com.tsab.pikapp.models.model.OrderDetailDetail
import com.tsab.pikapp.util.rupiahFormat

class StoreOrderListDetailProductAdapter(var productList: ArrayList<OrderDetailDetail>) : RecyclerView.Adapter<StoreOrderListDetailProductAdapter.ProductViewHolder>() {

    fun updateProductList(newProductList: List<OrderDetailDetail>) {
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemOrderListProductNoimageBinding

    class ProductViewHolder(var view: ItemOrderListProductNoimageBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_order_list_product_noimage, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.productList = productList[position]
        holder.view.productName.text = productList[position].productName
        holder.view.productQty.text = "x${productList[position].productQty}"
        productList[position].productNote?.let {
            if(it.isNotEmpty()) {
                holder.view.productNote.visibility = View.VISIBLE
                holder.view.productNote.text = "Catatan : ${productList[position].productNote}"
            } else
                holder.view.productNote.visibility = View.GONE
        }
//        holder.view.productPrice.text = rupiahFormat(productList[position].totalPrice!!.toLong())
    }
}