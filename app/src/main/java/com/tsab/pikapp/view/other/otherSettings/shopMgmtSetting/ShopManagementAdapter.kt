package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ShopSchedule
import com.tsab.pikapp.models.model.TimeManagementResponse
import kotlinx.android.synthetic.main.shop_schedule_recyclerview.view.*

class ShopManagementAdapter(private val context: Context, val shopScheduleList: MutableList<ShopSchedule>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ShopManagementAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_schedule_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        when (shopScheduleList[position].days) {
            "MONDAY" -> {
                holder.dayOfSchedule.text = "Senin"
            }
            "TUESDAY" -> {
                holder.dayOfSchedule.text = "Selasa"
            }
            "WEDNESDAY" -> {
                holder.dayOfSchedule.text = "Rabu"
            }
            "THURSDAY" -> {
                holder.dayOfSchedule.text = "Kamis"
            }
            "FRIDAY" -> {
                holder.dayOfSchedule.text = "Jumat"
            }
            "SATURDAY" -> {
                holder.dayOfSchedule.text = "Sabtu"
            }
            else -> {
                holder.dayOfSchedule.text = "Minggu"
            }
        }

        if (shopScheduleList[position].openTime == "00:00" && shopScheduleList[position].closeTime == "23:59"){
            holder.openTime.text = "Buka 24 Jam"
            holder.closeTime.text = ""
            holder.connector.text = ""
        } else if (shopScheduleList[position].openTime == "00:00" && shopScheduleList[position].closeTime == "00:00"){
            holder.openTime.text = "Tutup"
            holder.closeTime.text = ""
            holder.connector.text = ""
        } else{
            holder.openTime.text = shopScheduleList[position].openTime
            holder.closeTime.text = shopScheduleList[position].closeTime
        }
    }

    override fun getItemCount(): Int {
        return shopScheduleList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var dayOfSchedule: TextView = itemView.day_of_schedule
        var openTime: TextView = itemView.open_time
        var closeTime: TextView = itemView.close_time
        var connector: TextView = itemView.connector

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, shopScheduleList[position].days, shopScheduleList[position].closeTime, shopScheduleList[position].openTime)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, shopScheduleDay: String?, closeTime: String?, openTime: String?)
    }

}