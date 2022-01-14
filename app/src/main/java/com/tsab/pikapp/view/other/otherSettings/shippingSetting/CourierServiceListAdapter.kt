package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CourierServiceList

class CourierServiceListAdapter(
    private val courierService: MutableList<CourierServiceList>
) : RecyclerView.Adapter<CourierServiceListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courierServiceCheckBox: CheckBox = itemView.findViewById(R.id.courier_service_checkbox)
        var courierServiceDesc: TextView = itemView.findViewById(R.id.courier_service_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_courier_service_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.courierServiceCheckBox.text = courierService[position].service_name
        holder.courierServiceDesc.text = courierService[position].service_desc

        holder.courierServiceCheckBox.isChecked = courierService[position].service_type
    }

    override fun getItemCount(): Int = courierService.size
}