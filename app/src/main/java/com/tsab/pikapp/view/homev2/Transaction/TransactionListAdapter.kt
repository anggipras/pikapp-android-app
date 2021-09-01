package com.tsab.pikapp.view.homev2.Transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.models.model.StoreOrderList
import com.tsab.pikapp.view.homev2.menu.MenuListAdapter
import kotlinx.android.synthetic.main.transaction_list_items.view.*

class TransactionListAdapter(private val context: Context, private val transactionList: MutableList<StoreOrderList>, val menuList: MutableList<CategoryListResult>, private val listener: OnItemClickListener) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    lateinit var linearLayoutManager: LinearLayoutManager
    var menuResult = ArrayList<CategoryListResult>()

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //ini diganti sesuai api

        holder.tableNumer.text = transactionList[position].tableNo
        holder.tableStatus.text = transactionList[position].bizType
        holder.orderDate.text = transactionList[position].transactionTime
        //holder.orderTime.text = ""
        holder.orderStatus.text = transactionList[position].status
        //holder.paymentStatus.text = ""
        //holder.menuCount.text = transactionList[position].
        holder.price.text = ""
        //setMenu(holder.rView, menuList)
    }

    override fun getItemCount(): Int {
        //ganti yang ini sama di parameter
        return transactionList.size
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
        var rView: RecyclerView = itemView.recyclerview_menu

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

    private fun setMenu(recyclerView: RecyclerView, menuList: MutableList<CategoryListResult>){
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = TransactionMenuAdapter(context, menuList)
        recyclerView.adapter = menuList1
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}