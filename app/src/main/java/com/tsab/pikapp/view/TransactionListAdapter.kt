package com.tsab.pikapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ShopSchedule
import kotlinx.android.synthetic.main.transaction_list_items.view.*

class TransactionListAdapter(private val context: Context, val shopScheduleList: MutableList<ShopSchedule>, private val listener: OnItemClickListener) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //ini diganti sesuai api

        holder.tableNumer.text = "Buka 24 Jam"
        holder.tableStatus.text = ""
        holder.orderDate.text = ""
        holder.orderTime.text = ""
        holder.orderStatus.text = ""
        holder.paymentStatus.text = ""
        holder.menuCount.text = ""
        holder.price.text = ""
    }

    override fun getItemCount(): Int {
        //ganti yang ini sama di parameter
        return shopScheduleList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var tableNumer: TextView = itemView.tableNumber
        var tableStatus: TextView = itemView.tableStatus
        var orderDate: TextView = itemView.orderDate
        var orderTime: TextView = itemView.orderTime
        var orderStatus: TextView = itemView.orderStatus
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var price: TextView = itemView.totalPrice
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                //diganti list nya sesuai api
                listener.onItemClick(position, shopScheduleList[position].days, shopScheduleList[position].closeTime, shopScheduleList[position].openTime)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, shopScheduleDay: String?, closeTime: String?, openTime: String?)
    }

}