package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R

class InvoiceAdvAdapter(val context: Context,
                        private var extraList: ArrayList<String>) :
    RecyclerView.Adapter<InvoiceAdvAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemText: TextView = itemView.findViewById(R.id.menuAdv)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InvoiceAdvAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_adv_invoice, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: InvoiceAdvAdapter.ViewHolder, position: Int) {
        holder.itemText.text = extraList[position]
    }

    override fun getItemCount(): Int {
        return extraList.size
    }
}