package com.tsab.pikapp.view.homev2.transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ProductDetailV2Data
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class TransactionProductListV2Adapter(
    private val productList: List<ProductDetailV2Data>?
) : RecyclerView.Adapter<TransactionProductListV2Adapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionProductListV2Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TransactionProductListV2Adapter.ViewHolder,
        position: Int
    ) {
        holder.menuName.text = productList?.get(position)?.product_name ?: ""
        holder.itemNumber.text = productList?.get(position)?.quantity.toString() + "x"
//        if (productList?.get(position)?.extra_menus?.isNotEmpty() == true){
//            holder.stripMenu.visibility = View.GONE
//            holder.extraMenu.visibility = View.GONE
//        }
//        else {
//            holder.stripMenu.visibility = View.VISIBLE
//            holder.extraMenu.visibility = View.VISIBLE
//            holder.extraMenu.text = productList[position].extra_menus
//        }
        if(productList?.get(position)?.notes == ""){
            holder.orderNote.visibility = View.GONE
        } else {
            holder.orderNote.text = productList?.get(position)?.notes ?: ""
        }
        if (productList != null) {
            if (position == productList.size - 1){
                holder.divider.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return productList?.size ?: 0
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var divider: View = itemView.divider
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote
        var stripMenu: TextView = itemView.stripMenu
        var extraMenu: TextView = itemView.extraMenu
    }
}