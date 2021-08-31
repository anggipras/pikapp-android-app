package com.tsab.pikapp.view


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ShopSchedule
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class TransactionMenuAdapter(private val context: Context, val shopScheduleList: MutableList<ShopSchedule>, private val listener: OnItemClickListener) : RecyclerView.Adapter<TransactionMenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //ini diganti sesuai api

        holder.itemNumber.text = "Buka 24 Jam"
        holder.menuName.text = ""
        holder.orderNote.text = ""
    }

    override fun getItemCount(): Int {
        //ganti yang ini sama di parameter
        return shopScheduleList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var itemNumber: TextView = itemView.itemCount
        var menuName: TextView = itemView.menuName
        var orderNote: TextView = itemView.orderNote

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                //diganti list nya sesuai api
                listener.onItemClick(position, shopScheduleList[position].days, shopScheduleList[position].closeTime, shopScheduleList[position].openTime)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, shopScheduleDay: String?, closeTime: String?, openTime: String?)
    }

}