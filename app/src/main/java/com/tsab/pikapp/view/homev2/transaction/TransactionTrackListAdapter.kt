package com.tsab.pikapp.view.homev2.transaction

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.TrackingDetail
import kotlinx.android.synthetic.main.item_transaction_tracking.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionTrackListAdapter() : RecyclerView.Adapter<TransactionTrackListAdapter.ViewHolder>() {
    private var list: List<TrackingDetail> = ArrayList()
    fun setTransactionTrackingList(trackingList: List<TrackingDetail>) {
        this.list = trackingList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionTrackListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_tracking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionTrackListAdapter.ViewHolder, position: Int) {
        holder.trackingDetailContent.text = list[position].note

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDate = SimpleDateFormat("dd/MM/yyyy")
        val outputDate = formatterDate.format(parser.parse(list[position].updated_at))
        holder.trackingDate.text = outputDate

        val formatterTime = SimpleDateFormat("HH:mm")
        val outputTime = formatterTime.format(parser.parse(list[position].updated_at))
        holder.trackingTime.text = outputTime
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var trackingDate: TextView = itemView.trackingDate
        var trackingTime: TextView = itemView.trackingTime
        var trackingDetailContent: TextView = itemView.trackingDetailContent
    }
}