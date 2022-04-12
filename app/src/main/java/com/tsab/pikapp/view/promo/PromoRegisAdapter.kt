package com.tsab.pikapp.view.promo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.PromoRegisListData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PromoRegisAdapter(
    val context: Context,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_REGULAR = 0
        const val VIEW_TYPE_SEE_ALL = 1
    }

    private var listOfPromoRegis: MutableList<PromoRegisListData> = ArrayList()
    private val id = Locale("in", "ID")

    @SuppressLint("NotifyDataSetChanged")
    fun setPromoListAdapter(promoList: MutableList<PromoRegisListData>) {
        this.listOfPromoRegis = promoList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_REGULAR) {
            return RegularViewHolder(
                LayoutInflater.from(context).inflate(R.layout.promo_regis_list_items, parent, false)
            )
        }
        return AllPromoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.promo_all_regis_list_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (listOfPromoRegis[position].viewType) {
            VIEW_TYPE_REGULAR -> (holder as RegularViewHolder).bind(position)
            else -> (holder as AllPromoViewHolder).bind(position)
        }
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

    override fun getItemCount(): Int = listOfPromoRegis.size

    private inner class RegularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var voucherTitle: TextView = itemView.findViewById(R.id.voucher_title)
        var voucherImg: ImageView = itemView.findViewById(R.id.voucher_img)
        var voucherDatePeriod: TextView = itemView.findViewById(R.id.voucher_period)
        var voucherRegisDeadlinePeriod: TextView = itemView.findViewById(R.id.voucher_deadline_period)
        var voucherRegisButton: Button = itemView.findViewById(R.id.voucher_regis_button)

        fun bind(position: Int) {
            val promoRegisValue = listOfPromoRegis[position]
            voucherTitle.text = promoRegisValue.campaign_name
            Picasso.get().load(promoRegisValue.campaign_image).into(voucherImg)
            dateFormatter(voucherDatePeriod, voucherRegisDeadlinePeriod, promoRegisValue.campaign_start_date, promoRegisValue.campaign_end_date, promoRegisValue.campaign_regis_deadline_date)
            itemView.setOnClickListener {
                listener.onItemRegisPromoClick(promoRegisValue)
            }
            voucherRegisButton.setOnClickListener {
                listener.onItemRegisPromoClick(promoRegisValue)
            }
        }
    }

    private inner class AllPromoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var voucherTitle: TextView = itemView.findViewById(R.id.voucher_title)
        var voucherImg: ImageView = itemView.findViewById(R.id.voucher_img)
        var voucherDatePeriod: TextView = itemView.findViewById(R.id.voucher_period)
        var voucherRegisDeadlinePeriod: TextView = itemView.findViewById(R.id.voucher_deadline_period)
        var voucherRegisButton: Button = itemView.findViewById(R.id.voucher_regis_button)

        fun bind(position: Int) {
            val promoRegisValue = listOfPromoRegis[position]
            voucherTitle.text = promoRegisValue.campaign_name
            Picasso.get().load(promoRegisValue.campaign_image).into(voucherImg)
            dateFormatter(voucherDatePeriod, voucherRegisDeadlinePeriod, promoRegisValue.campaign_start_date, promoRegisValue.campaign_end_date, promoRegisValue.campaign_regis_deadline_date)
            itemView.setOnClickListener {
                listener.onItemRegisPromoClick(promoRegisValue)
            }
            voucherRegisButton.setOnClickListener {
                listener.onItemRegisPromoClick(promoRegisValue)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listOfPromoRegis[position].viewType
    }

    interface OnItemClickListener {
        fun onItemRegisPromoClick(promoRegisValue: PromoRegisListData)
    }

    private fun reusablePromo() {
//        if (promoRegisValue.discount_amt_type == "ABSOLUTE") {
//            val nominalDiscountDivided = promoRegisValue.discount_amt?.toDouble()?.div(1000) ?: 1
//            val nominalDiscount = nominalDiscountDivided.toLong()
//            val formattedDouble = String.format("%.1f", nominalDiscountDivided)
//            val doubleTimesTen = (formattedDouble.toDouble() * 10).toLong()
//            val checkLastDigit = (doubleTimesTen % 10).toString()
//            if (checkLastDigit == "0") {
//                voucherDiscPercentage.text = "${nominalDiscount}rb"
//            } else {
//                voucherDiscPercentage.text = "${formattedDouble}rb"
//            }
//        } else {
//            voucherDiscPercentage.text = "${promoRegisValue.discount_amt}%"
//        }
    }
}