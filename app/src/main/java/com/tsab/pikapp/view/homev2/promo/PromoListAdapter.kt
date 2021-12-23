package com.tsab.pikapp.view.homev2.promo

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.PromoListModel
import kotlinx.android.synthetic.main.promo_list_items.view.*

class PromoListAdapter(
    val context: Context,
    private var promoList: MutableList<PromoListModel>
    ) : RecyclerView.Adapter<PromoListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var promoID: TextView = itemView.promo_id
        var promoDate: TextView = itemView.promo_date
        var promoStatus: TextView = itemView.promo_status
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.promo_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.promoID.text = context.getString(R.string.promotion_id, promoList[position].promoID)
        holder.promoDate.text = context.getString(R.string.promotion_date, promoList[position].startDate, promoList[position].endDate)
        holder.promoStatus.text = promoList[position].status
        if (promoList[position].status == "Tidak Aktif") {
            holder.promoStatus.setTextColor(Color.parseColor("#DC6A84"))
        } else if (promoList[position].status == "Dihentikan") {
            holder.promoStatus.setTextColor(Color.parseColor("#DC6A84"))
        } else {
            holder.promoStatus.setTextColor(Color.parseColor("#4BB7AC"))
        }
    }

    override fun getItemCount(): Int {
        return promoList.size
    }
}