package com.tsab.pikapp.view.other.otherSettings.shopMgmtSetting

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ShopSchedule
import kotlinx.android.synthetic.main.shop_schedule_recyclerview.view.*

class ShopManagementAdapter(private val context: Context, private val shopScheduleList: MutableList<ShopSchedule>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ShopManagementAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.shop_schedule_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dayOfSchedule.text = shopScheduleList[position].days
    }

    override fun getItemCount(): Int {
        return shopScheduleList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var dayOfSchedule: TextView = itemView.day_of_schedule

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