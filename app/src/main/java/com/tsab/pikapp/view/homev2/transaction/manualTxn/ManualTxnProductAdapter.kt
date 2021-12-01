package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ManualProductListResponse
import com.tsab.pikapp.models.model.ProductDetailOmni
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionProductAdapter
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class ManualTxnProductAdapter(
    private val context: Context,
    private val productList: MutableList<ManualProductListResponse>
    ) : RecyclerView.Adapter<ManualTxnProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.menuName.text = productList[position].product_name
        holder.itemNumber.text = productList[position].quantity.toString() + "x"
        if(productList[position].notes == ""){
            holder.orderNote.visibility = View.GONE
        } else {
            holder.orderNote.text = productList[position].notes
        }
        if (position == productList.size - 1){
            holder.divider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var divider: View = itemView.divider
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote
    }
}