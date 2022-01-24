package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CustomerCourierListResult
import com.tsab.pikapp.models.model.CustomerCourierServiceList
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomerCourierListAdapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CustomerCourierListAdapter.ViewHolder>() {
    private val localeID =  Locale("in", "ID")
    private var courierList: List<CustomerCourierListResult> = ArrayList()
    fun setCourierList(list: List<CustomerCourierListResult>) {
        this.courierList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_courier_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courierObject = courierList[position]
        holder.courierNameAndPrice.text = "${courierObject.name} (Rp. ${formatNumber(courierObject.lower_limit)} - Rp. ${formatNumber(courierObject.upper_limit)})"
        holder.courierDescription.text = courierObject.description
        holder.itemView.setOnClickListener {
            listener.onCourierClick(courierObject.courier_list)
        }
    }

    private fun formatNumber(price: Long) : String {
        val numberFormat = NumberFormat.getInstance(localeID).format(price)
        return numberFormat.toString()
    }

    override fun getItemCount(): Int = courierList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courierNameAndPrice: TextView = itemView.findViewById(R.id.courier_nameAndPrice)
        var courierDescription: TextView = itemView.findViewById(R.id.courier_description)
    }

    interface OnItemClickListener {
        fun onCourierClick(courierServiceList: MutableList<CustomerCourierServiceList>)
    }
}