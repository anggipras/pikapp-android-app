package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ManualProductListResponse
import com.tsab.pikapp.models.model.ManualTransactionResult
import com.tsab.pikapp.models.model.ProductDetailOmni
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionProductAdapter
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items_manual.view.*
import org.w3c.dom.Text
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice as totalPrice1

class ManualTxnListAdapter(
    private val context: Context,
    private val transactionList: MutableList<ManualTransactionResult>,
    private val productList: MutableList<List<ManualProductListResponse>>
): RecyclerView.Adapter<ManualTxnListAdapter.ViewHolder>() {

    var bulan: String = " Jun "
    var bulanTemp: String = ""
    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.omniChannelIcon
        var orderDate: TextView = itemView.orderDateOmni
        var shippingMethod: TextView = itemView.shippingMethod
        var custName: TextView = itemView.custName
        var custPhone: TextView = itemView.custPhone
        var custAddress: TextView = itemView.custAddress
        var shippingDate: TextView = itemView.shippingDate
        var statusPayment: TextView = itemView.statusPayment
        var updateStatusBtn: Button = itemView.updateStatus
        var rView: RecyclerView = itemView.recyclerview_menu_omni
        var itemCount: TextView = itemView.itemCount
        var totalPrice: TextView = itemView.totalPriceManual
        var acceptBtn: Button = itemView.acceptButtonManual
        var rejectBtn: Button = itemView.rejectButtonManual

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ManualTxnListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list_items_manual, parent, false)
        return ViewHolder(view)    }

    override fun onBindViewHolder(holder: ManualTxnListAdapter.ViewHolder, position: Int) {
        setMenu(holder.rView, productList[position] as MutableList<ManualProductListResponse>)
        setDate(position)
        holder.logo.setImageResource(R.drawable.logo_wa)
        holder.orderDate.text = transactionList[position].transaction_time.toString().substringBefore("-") + bulan
        holder.totalPrice.text = transactionList[position].total_payment.toString()
        holder.shippingMethod.text = transactionList[position].shipping?.shipping_method
        holder.shippingDate.text = transactionList[position].shipping?.shipping_time
        holder.custName.text = transactionList[position].customer?.customer_name
        holder.custAddress.text = transactionList[position].customer?.address
        holder.custPhone.text = transactionList[position].customer?.phone_number
        holder.statusPayment.text = transactionList[position].payment_status
        /*if (transactionList[position].order_status == "ON_PROCESS"){
        }*/
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    private fun setMenu(
        recyclerView: RecyclerView,
        productList: MutableList<ManualProductListResponse>
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = ManualTxnProductAdapter(context, productList)
        recyclerView.adapter = menuList1
    }

    private fun setDate(position: Int) {
        bulanTemp =
            transactionList[position].transaction_time.toString().substringAfter("-").substringBeforeLast("-")
                .toString()
        if (bulanTemp == "01") {
            bulan = " Jan "
        } else if (bulanTemp == "02") {
            bulan = " Feb "
        } else if (bulanTemp == "03") {
            bulan = " Mar "
        } else if (bulanTemp == "04") {
            bulan = " Apr "
        } else if (bulanTemp == "05") {
            bulan = " Mei "
        } else if (bulanTemp == "06") {
            bulan = " Jun "
        } else if (bulanTemp == "07") {
            bulan = " Jul "
        } else if (bulanTemp == "08") {
            bulan = " Ags "
        } else if (bulanTemp == "09") {
            bulan = " Sep "
        } else if (bulanTemp == "10") {
            bulan = " Okt "
        } else if (bulanTemp == "11") {
            bulan = " Nov "
        } else if (bulanTemp == "12") {
            bulan = " Des "
        }

/*        for (count in productList[position]) {
            jumlah = jumlah + count.quantity!!.toInt()
            if (count.quantity > 1) {
                price += count.quantity * count.price!!.toInt()
            } else {
                price += count.price!!.toInt()
            }
        }*/
    }

}