package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CustomerCourierServiceList
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomerCourierServiceListAdapter(
    private val listener: OnItemCourierServiceClickListener
) : RecyclerView.Adapter<CustomerCourierServiceListAdapter.ViewHolder>() {
    var clickedPosition = 0
    var clicked = false
    private val localeID =  Locale("in", "ID")
    private var courierServiceList: List<CustomerCourierServiceList> = ArrayList()
    fun setCourierServiceList(list: List<CustomerCourierServiceList>) {
        this.courierServiceList = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_customer_courier_service_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val courierServiceObject = courierServiceList[position]
        if (courierServiceObject.service_name.isNullOrEmpty()) {
            holder.courierServiceNameAndPrice.text = "${courierServiceObject.name} (Rp ${formatNumber(courierServiceObject.price)})"
        } else {
            holder.courierServiceNameAndPrice.text = "${courierServiceObject.name} - ${courierServiceObject.service_name} (Rp ${formatNumber(courierServiceObject.price)})"
        }
        holder.courierServiceDescription.text = courierServiceObject.description

        holder.itemView.setOnClickListener {
            clickedPosition = position
            clicked = true
            listener.onCourierServiceClick(courierServiceObject)
            notifyDataSetChanged()
        }

        if (clicked) {
            holder.courierServiceChecked.isVisible = position == clickedPosition
        }
    }

    private fun formatNumber(price: Long) : String {
        val numberFormat = NumberFormat.getInstance(localeID).format(price)
        return numberFormat.toString()
    }

    override fun getItemCount(): Int = courierServiceList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courierServiceNameAndPrice: TextView = itemView.findViewById(R.id.courier_service_nameAndPrice)
        var courierServiceDescription: TextView = itemView.findViewById(R.id.courier_service_description)
        var courierServiceChecked: ImageView = itemView.findViewById(R.id.customer_courier_checked)
    }

    interface OnItemCourierServiceClickListener {
        fun onCourierServiceClick(courierServiceObject: CustomerCourierServiceList)
    }
}