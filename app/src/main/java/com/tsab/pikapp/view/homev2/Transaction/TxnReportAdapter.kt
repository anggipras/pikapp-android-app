package com.tsab.pikapp.view.homev2.Transaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import kotlinx.android.synthetic.main.list_report_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.*

class TxnReportAdapter(private val txnList: ArrayList<String>): RecyclerView.Adapter<TxnReportAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.logo
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): TxnReportAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_report_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(txnList[position] == "shopee"){
            holder.logo.setImageResource(R.drawable.shopee)
        }else if(txnList[position] == "tokopedia"){
            holder.logo.setImageResource(R.drawable.tokopedia)
        }else if(txnList[position] == "grab"){
            holder.logo.setImageResource(R.drawable.grabfood)
        }else if(txnList[position] == "pikapp"){
            holder.logo.setImageResource(R.drawable.pikapptxn)
        }
    }

    override fun getItemCount(): Int {
        return  txnList.size
    }

}