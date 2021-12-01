package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionProductAdapter
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items_manual.view.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice as totalPrice1

class ManualTxnListAdapter(
    private val context: Context,
    private val transactionList: MutableList<ManualTransactionResult>?,
    private val productList: MutableList<List<ManualProductListResponse>>,
    private val mid: String
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


    override fun getItemCount(): Int {
        if (transactionList != null) { return transactionList.size}
        return 0
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
            transactionList?.get(position)?.transaction_time.toString().substringAfter("-").substringBeforeLast("-")
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setMenu(holder.rView, productList[position] as MutableList<ManualProductListResponse>)
        setDate(position)
        holder.updateStatusBtn.setOnClickListener {
            PikappApiService().api.postUpdateManualTransaction(UpdateStatusManualTxnRequest(
                transactionList?.get(position)?.customer?.shipping_cost?.toString(),
                mid, transactionList?.get(position)?.order_id, transactionList?.get(position)?.order_status, "Paid"
            )).enqueue(object : Callback<UpdateStatusManualResponse>{
                override fun onResponse(
                    call: Call<UpdateStatusManualResponse>,
                    response: Response<UpdateStatusManualResponse>
                ) {
                    Log.e("Success", response.code().toString())
                    if(response.code() == 200){
                        notifyItemChanged(holder.position)
                    }
                }

                override fun onFailure(call: Call<UpdateStatusManualResponse>, t: Throwable) {
                    Log.e("Fail", t.message.toString())
                }

            })
        }
        holder.logo.setImageResource(R.drawable.logo_wa)
        holder.orderDate.text = transactionList?.get(position)?.transaction_time.toString().substringBefore("-") + bulan
        holder.totalPrice.text = transactionList?.get(position)?.total_payment.toString()
        holder.shippingMethod.text = transactionList?.get(position)?.shipping?.shipping_method
        holder.shippingDate.text = transactionList?.get(position)?.shipping?.shipping_time
        holder.custName.text = transactionList?.get(position)?.customer?.customer_name
        holder.custAddress.text = transactionList?.get(position)?.customer?.address
        holder.custPhone.text = transactionList?.get(position)?.customer?.phone_number
        holder.statusPayment.text = transactionList?.get(position)?.payment_status
        /*if (transactionList[position].order_status == "ON_PROCESS"){
        }*/
    }

}