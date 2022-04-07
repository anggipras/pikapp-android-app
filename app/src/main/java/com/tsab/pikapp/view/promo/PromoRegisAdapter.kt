package com.tsab.pikapp.view.promo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.PromoRegisListModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PromoRegisAdapter(
    val context: Context,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<PromoRegisAdapter.ViewHolder>() {
    private var listOfPromoRegis: MutableList<PromoRegisListModel> = ArrayList()
    private val id = Locale("in", "ID")

    @SuppressLint("NotifyDataSetChanged")
    fun setPromoListAdapter(promoList: MutableList<PromoRegisListModel>) {
        this.listOfPromoRegis = promoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PromoRegisAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.promo_regis_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromoRegisAdapter.ViewHolder, position: Int) {
        val promoRegisValue = listOfPromoRegis[position]
        holder.voucherTitle.text = promoRegisValue.campaign_name
        holder.voucherQuota.text = context.getString(R.string.voucher_quota, promoRegisValue.campaign_quota)
        if (promoRegisValue.discount_amt_type == "ABSOLUTE") {
            val nominalDiscountDivided = promoRegisValue.discount_amt?.toDouble()?.div(1000) ?: 1
            val nominalDiscount = nominalDiscountDivided.toLong()
            val formattedDouble = String.format("%.1f", nominalDiscountDivided)
            val doubleTimesTen = (formattedDouble.toDouble() * 10).toLong()
            val checkLastDigit = (doubleTimesTen % 10).toString()
            if (checkLastDigit == "0") {
                holder.voucherDiscPercentage.text = "${nominalDiscount}rb"
            } else {
                holder.voucherDiscPercentage.text = "${formattedDouble}rb"
            }
        } else {
            holder.voucherDiscPercentage.text = "${promoRegisValue.discount_amt}%"
        }

        dateFormatter(holder, promoRegisValue.campaign_start_date, promoRegisValue.campaign_end_date, promoRegisValue.campaign_regis_deadline_date)
        holder.voucherRegisButton.setOnClickListener {
            listener.onItemRegisPromoClick(promoRegisValue)
        }
    }

    private fun dateFormatter(holder: ViewHolder, startDate: String?, endDate: String?, deadlineDate: String?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDatePeriod = SimpleDateFormat("dd MMM yyyy", id)
        val formatterDateDeadline = SimpleDateFormat("dd MMMM yyyy", id)
        val outputStartDate = formatterDatePeriod.format(parser.parse(startDate))
        val outputEndDate = formatterDatePeriod.format(parser.parse(endDate))
        val outputDeadlineDate = formatterDateDeadline.format(parser.parse(deadlineDate))

        holder.voucherDatePeriod.text = context.getString(R.string.voucher_period, outputStartDate, outputEndDate)
        holder.voucherRegisDeadlinePeriod.text = context.getString(R.string.voucher_deadline_period, outputDeadlineDate)
    }

    override fun getItemCount(): Int = listOfPromoRegis.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var voucherTitle: TextView = itemView.findViewById(R.id.voucher_title)
        var voucherQuota: TextView = itemView.findViewById(R.id.voucher_quota)
        var voucherDiscPercentage: TextView = itemView.findViewById(R.id.voucher_disc_amt)
        var voucherDatePeriod: TextView = itemView.findViewById(R.id.voucher_period)
        var voucherRegisDeadlinePeriod: TextView = itemView.findViewById(R.id.voucher_deadline_period)
        var voucherRegisButton: Button = itemView.findViewById(R.id.voucher_regis_button)
    }

    interface OnItemClickListener {
        fun onItemRegisPromoClick(promoRegisValue: PromoRegisListModel)
    }
}