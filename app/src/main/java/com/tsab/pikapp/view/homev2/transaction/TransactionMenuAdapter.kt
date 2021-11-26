package com.tsab.pikapp.view.homev2.transaction


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.OrderDetailDetail
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class TransactionMenuAdapter(private val context: Context, private val transactionList1: MutableList<OrderDetailDetail>) : RecyclerView.Adapter<TransactionMenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.menuName.text = transactionList1[position].productName
        holder.itemNumber.text = transactionList1[position].productQty.toString() + "x"
        if(transactionList1[position].productNote == ""){
            holder.orderNote.visibility = View.GONE
        } else {
            holder.orderNote.text = transactionList1[position].productNote
        }
        if (position == transactionList1.size - 1){
            holder.divider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return transactionList1.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var divider: View = itemView.divider
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote
    }
}