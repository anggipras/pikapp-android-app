package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CourierServiceList

class CourierServiceListAdapter(
    private val courierService: MutableList<CourierServiceList>,
    private val listener: OnCheckListener,
    private val courierNameIndex: Int
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
        holder.courierServiceCheckBox.text = courierService[position].courier_services_name
        holder.courierServiceDesc.text = courierService[position].description

        holder.courierServiceCheckBox.isChecked = courierService[position].courier_service_type
        holder.courierServiceCheckBox.setOnCheckedChangeListener { _, isChecked ->
            listener.onCheckClick(courierNameIndex, courierService.indexOf(courierService[position]), isChecked)
        }
    }

    override fun getItemCount(): Int = courierService.size

    interface OnCheckListener {
        fun onCheckClick(courierNameIndex: Int, courierServiceIndex: Int, isChecked: Boolean)
    }
}