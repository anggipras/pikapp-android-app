package com.tsab.pikapp.view.orderList.orderListDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemOrderListProductBinding
import com.tsab.pikapp.models.model.CartModel
import com.tsab.pikapp.models.model.OrderDetailDetail
import com.tsab.pikapp.util.rupiahFormat

class OrderListDetailProductAdapter(private val productList: ArrayList<OrderDetailDetail>) : RecyclerView.Adapter<OrderListDetailProductAdapter.ProductViewHolder>() {

    fun updateProductList(newProductList: List<OrderDetailDetail>) {
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemOrderListProductBinding

    class ProductViewHolder(var view: ItemOrderListProductBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_order_list_product, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.productList = productList[position]
        holder.view.productName.text = productList[position].productName
        holder.view.productQty.text = "x${productList[position].productQty}"
        holder.view.productNote.text = "Catatan : ${productList[position].productNote}"
//        holder.view.productPrice.text = rupiahFormat(productList[position].totalPrice!!.toLong())
    }
}