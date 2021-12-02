package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.ManualProductListResponse
import com.tsab.pikapp.models.model.ManualTransactionResult
import com.tsab.pikapp.models.model.ProductDetailOmni
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionProductAdapter
import com.tsab.pikapp.view.homev2.transaction.shipment.ResiTokopediaDialogFragment.Companion.tag
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items_manual.view.*
import org.w3c.dom.Text
import timber.log.Timber
import java.text.NumberFormat
import java.util.*
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice as totalPrice1

class ManualTxnListAdapter(
    private val context: Context,
    private val transactionList: MutableList<ManualTransactionResult>,
    private val productList: MutableList<List<ManualProductListResponse>>
): RecyclerView.Adapter<ManualTxnListAdapter.ViewHolder>() {

    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var jumlah = 0
    var price = 0
    var str: String = ""
    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logo: ImageView = itemView.omniChannelIcon
        var orderDate: TextView = itemView.orderDateManual
        var shippingMethod: TextView = itemView.shippingMethod
        var custName: TextView = itemView.custName
        var custPhone: TextView = itemView.custPhone
        var custAddress: TextView = itemView.custAddress
        var shippingDate: TextView = itemView.shippingDate
        var statusPayment: TextView = itemView.statusPayment
        var orderStatus: TextView = itemView.paymentStatusManual
        var updatePaymentBtn: Button = itemView.updateStatus
        var rView: RecyclerView = itemView.recyclerview_menu_omni
        var itemCount: TextView = itemView.itemCount
        var totalPrice: TextView = itemView.totalPriceManual
        var acceptBtn: Button = itemView.acceptButtonManual
        var rejectBtn: Button = itemView.rejectButtonManual
        var transactionId: TextView = itemView.transactionIdManual

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
        if (transactionList[position].order_status == "ON_PROCESS" || transactionList[position].order_status == "OPEN"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Diproses"
            holder.orderStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.rejectBtn.visibility = View.GONE
        } else if (transactionList[position].order_status == "DELIVER") {
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Dikirim"
            holder.orderStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.rejectBtn.visibility = View.GONE
            holder.acceptBtn.text = "Pesanan Tiba"
        } else if (transactionList[position].order_status == "FINALIZE"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Sampai"
            holder.orderStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.rejectBtn.visibility = View.GONE
            holder.acceptBtn.text = "Pesanan Selesai"
            if (transactionList[position].payment_status == "PAID"){
                holder.acceptBtn.isEnabled = true
                holder.acceptBtn.setTextColor(context.resources.getColor(R.color.green))
                holder.acceptBtn.setBackgroundResource(R.drawable.button_green_transparent)
            } else {
                holder.acceptBtn.isEnabled = false
                holder.acceptBtn.setTextColor(context.resources.getColor(R.color.borderSubtle))
                holder.acceptBtn.setBackgroundResource(R.drawable.button_gray_transparent)
            }
        } else if (transactionList[position].order_status == "CLOSE"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Selesai"
            holder.orderStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.rejectBtn.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
        } else if (transactionList[position].order_status == "CANCELLED"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Batal"
            holder.orderStatus.setBackgroundResource(R.drawable.button_red_transparent)
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
        }else {
            Toast.makeText(context, "Invalid Status Order", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    private fun setUpCard(holder: ViewHolder, position: Int){
        setUpLogo(holder, position)
        holder.transactionId.text = "ID Transaksi: " + transactionList[position].transaction_id.toString().substring(0, 10)
        holder.orderDate.text = transactionList[position].transaction_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                transactionList[position].transaction_time.toString().substringBefore("-") +", "+ transactionList[position].transaction_time.toString().substringAfter(" ").substringBeforeLast(":")
        holder.itemCount.text = "Total $jumlah items:"
        jumlah = 0
        formatNumber()
        holder.totalPrice.text = "Rp. " + str
        price = 0
        holder.shippingMethod.text = transactionList[position].shipping?.shipping_method
        holder.shippingDate.text = transactionList[position].shipping?.shipping_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                transactionList[position].shipping?.shipping_time.toString().substringBefore("-") +", "+ transactionList[position].shipping?.shipping_time.toString().substringAfter(" ").substringBeforeLast(":")
        holder.custName.text = transactionList[position].customer?.customer_name
        holder.custAddress.text = transactionList[position].customer?.address
        holder.custPhone.text = "(" + transactionList[position].customer?.phone_number + ")"
        if (transactionList[position].payment_status == "PAID"){
            holder.statusPayment.setTextColor(context.resources.getColor(R.color.green))
            holder.statusPayment.text = "Sudah Bayar"
            holder.updatePaymentBtn.visibility = View.GONE
        } else {
            holder.statusPayment.text = "Belum Bayar"
            holder.statusPayment.setTextColor(context.resources.getColor(R.color.red))
        }
    }

    private fun setUpLogo(holder: ViewHolder, position: Int){
        if (transactionList[position].order_platform == "Instagram"){
            holder.logo.setImageResource(R.drawable.logo_ig)
        } else if (transactionList[position].order_platform == "Whatsapp"){
            holder.logo.setImageResource(R.drawable.logo_wa)
        } else if (transactionList[position].order_platform == "Telepon"){
            holder.logo.setImageResource(R.drawable.logo_phone)
        } else {
            Timber.tag(tag).d("Invalid Order Platform")
        }
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

        for (count in productList[position]) {
            jumlah = jumlah + count.quantity!!.toInt()
            price = transactionList[position].total_payment!!.toInt()
/*            if (count.quantity > 1) {
                price += count.quantity * count.price!!.toInt()
            } else {
                price += count.price!!.toInt()
            }*/
        }
    }

    private fun formatNumber() {
        str = NumberFormat.getNumberInstance(Locale.US).format(price)
    }

}
