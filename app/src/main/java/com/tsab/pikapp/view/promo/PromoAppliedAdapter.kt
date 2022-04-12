package com.tsab.pikapp.view.promo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.PromoAppliedListData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PromoAppliedAdapter(
    val context: Context,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_REGULAR = 0
    }

    private var listOfPromoApplied: MutableList<PromoAppliedListData> = ArrayList()
    private val id = Locale("in", "ID")

    @SuppressLint("NotifyDataSetChanged")
    fun setPromoListAdapter(promoList: MutableList<PromoAppliedListData>) {
        this.listOfPromoApplied = promoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegularViewHolder(
            LayoutInflater.from(context).inflate(R.layout.promo_applied_list_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (listOfPromoApplied[position].viewType) {
            VIEW_TYPE_REGULAR -> (holder as RegularViewHolder).bind(position)
        }
    }

    override fun getItemCount(): Int = listOfPromoApplied.size

    override fun getItemViewType(position: Int): Int {
        return listOfPromoApplied[position].viewType
    }

    private fun dateFormatter(voucherDatePeriod: TextView, voucherRegisDeadlinePeriod: TextView, startDate: String?, endDate: String?, deadlineDate: String?) {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatterDatePeriod = SimpleDateFormat("dd MMM yyyy", id)
        val formatterDateDeadline = SimpleDateFormat("dd MMMM yyyy", id)
        val outputStartDate = formatterDatePeriod.format(parser.parse(startDate))
        val outputEndDate = formatterDatePeriod.format(parser.parse(endDate))
        val outputDeadlineDate = formatterDateDeadline.format(parser.parse(deadlineDate))

        voucherDatePeriod.text = context.getString(R.string.voucher_period, outputStartDate, outputEndDate)
        voucherRegisDeadlinePeriod.text = context.getString(R.string.voucher_deadline_period, outputDeadlineDate)
    }

    private inner class RegularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var voucherTitle: TextView = itemView.findViewById(R.id.applied_voucher_title)
        var voucherQuota: TextView = itemView.findViewById(R.id.applied_voucher_quota)
        var voucherDiscPercentage: TextView = itemView.findViewById(R.id.applied_voucher_disc_amt)
        var voucherDatePeriod: TextView = itemView.findViewById(R.id.applied_voucher_period)
        var voucherRegisDeadlinePeriod: TextView = itemView.findViewById(R.id.applied_voucher_deadline_period)
        var voucherRegisButton: Button = itemView.findViewById(R.id.applied_voucher_regis_button)
        var appliedPromoStatus: TextView = itemView.findViewById(R.id.applied_promo_status)

        fun bind(position: Int) {
            val promoAppliedValue = listOfPromoApplied[position]
            voucherRegisButton.isVisible = false
            appliedPromoStatus.isVisible = true
            voucherTitle.text = promoAppliedValue.campaign_name
            voucherQuota.text = context.getString(R.string.voucher_quota, promoAppliedValue.campaign_quota)
            if (promoAppliedValue.discount_amt_type == "ABSOLUTE") {
                val nominalDiscountDivided = promoAppliedValue.discount_amt?.toDouble()?.div(1000) ?: 1
                val nominalDiscount = nominalDiscountDivided.toLong()
                val formattedDouble = String.format("%.1f", nominalDiscountDivided)
                val doubleTimesTen = (formattedDouble.toDouble() * 10).toLong()
                val checkLastDigit = (doubleTimesTen % 10).toString()
                if (checkLastDigit == "0") {
                    voucherDiscPercentage.text = "${nominalDiscount}rb"
                } else {
                    voucherDiscPercentage.text = "${formattedDouble}rb"
                }
            } else {
                voucherDiscPercentage.text = "${promoAppliedValue.discount_amt}%"
            }

            if (promoAppliedValue.campaign_status == "ONGOING") {
                appliedPromoStatus.background.setColorFilter(ContextCompat.getColor(context, R.color.colorLightGreen), PorterDuff.Mode.SRC_IN)
                appliedPromoStatus.setTextColor(context.resources.getColor(R.color.colorGreen))
                appliedPromoStatus.text = "Berlangsung"
            } else {
                appliedPromoStatus.background.setColorFilter(ContextCompat.getColor(context, R.color.lightOrange), PorterDuff.Mode.SRC_IN)
                appliedPromoStatus.setTextColor(context.resources.getColor(R.color.orange))
                appliedPromoStatus.text = "Dalam Proses"
            }

            dateFormatter(voucherDatePeriod, voucherRegisDeadlinePeriod, promoAppliedValue.campaign_start_date, promoAppliedValue.campaign_end_date, promoAppliedValue.campaign_regis_deadline_date)
            itemView.setOnClickListener {
                listener.onItemAppliedPromoClick(promoAppliedValue)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemAppliedPromoClick(promoAppliedValue: PromoAppliedListData)
    }
}