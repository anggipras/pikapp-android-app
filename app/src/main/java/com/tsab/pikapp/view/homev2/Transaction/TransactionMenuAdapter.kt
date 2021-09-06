package com.tsab.pikapp.view.homev2.Transaction


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.OrderDetailDetail
import com.tsab.pikapp.models.model.ShopSchedule
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
        holder.orderNote.text = transactionList1[position].productNote
    }

    override fun getItemCount(): Int {
        //ganti yang ini sama di parameter
        return transactionList1.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote
    }

/*    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                //diganti list nya sesuai api
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }*/

}