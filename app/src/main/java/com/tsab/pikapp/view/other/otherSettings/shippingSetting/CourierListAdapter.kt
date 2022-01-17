package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CourierList
import com.tsab.pikapp.models.model.CourierServiceList

class CourierListAdapter(
    private val context: Context,
    private val listener: CourierServiceListAdapter.OnCheckListener
) : RecyclerView.Adapter<CourierListAdapter.ViewHolder>() {
    private var listOfCourier: MutableList<CourierList> = ArrayList()
    lateinit var linearLayoutManager: LinearLayoutManager

    fun setCourierListAdapter(courierList: MutableList<CourierList>) {
        this.listOfCourier = courierList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var courierName: TextView = itemView.findViewById(R.id.courier_parentName)
        var courierServiceList: RecyclerView = itemView.findViewById(R.id.recyclerview_courierService_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_courier_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.courierName.text = listOfCourier[position].courier_name
        setCourierServiceList(holder.courierServiceList, listOfCourier[position].services_list, listOfCourier.indexOf(listOfCourier[position]))
    }

    override fun getItemCount(): Int = listOfCourier.size

    private fun setCourierServiceList(
        recyclerView: RecyclerView,
        courierService: MutableList<CourierServiceList>,
        courierNameIndex: Int
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var courierServiceList = CourierServiceListAdapter(courierService, listener, courierNameIndex)
        recyclerView.adapter = courierServiceList
    }
}