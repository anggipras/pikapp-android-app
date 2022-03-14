package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionListAdapter
import com.tsab.pikapp.view.homev2.transaction.OmniTransactionProductAdapter
import com.tsab.pikapp.view.homev2.transaction.shipment.ResiTokopediaDialogFragment.Companion.tag
import kotlinx.android.synthetic.main.cancel_dialog.view.*
import kotlinx.android.synthetic.main.cancel_dialog.view.dialog_back
import kotlinx.android.synthetic.main.payment_dialog.view.*
import kotlinx.android.synthetic.main.payment_dialog.view.nama
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items_manual.view.*
import kotlinx.android.synthetic.main.update_payment_done.view.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice as totalPrice1
import androidx.core.content.ContextCompat.startActivity




class ManualTxnListAdapter(
    private val context: Context,
    private val transactionList: MutableList<ManualTransactionResult>,
    private val productList: MutableList<List<ManualProductListResponse>>,
    private val mid: String,
    private val reycler: RecyclerView,
    private val activity: Activity
): RecyclerView.Adapter<ManualTxnListAdapter.ViewHolder>() {

    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var jumlah = 0
    var price = 0
    var str: String = ""
    var temp: String = ""
    var number: String = ""
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
        var callBtn: ImageView = itemView.callBtn

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
            holder.acceptBtn.setOnClickListener {
                updateStatus(
                    UpdateStatusManualTxnRequest(transactionList[position].transaction_id, "DELIVER", transactionList[position].payment_status),
                "ON_PROCESS", mid, position, reycler, context
                )
            }
            holder.orderStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.rejectBtn.setOnClickListener {
                updateStatus(UpdateStatusManualTxnRequest(
                    transactionList[position].transaction_id,
                    "CANCELLED",
                    transactionList[position].payment_status),
                "ON_PROCESS", mid, position, reycler, context)
            }
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
        } else if (transactionList[position].order_status == "DELIVER") {
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Dikirim"
            holder.orderStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.rejectBtn.visibility = View.GONE
            holder.acceptBtn.setOnClickListener {
                updateStatus(
                    UpdateStatusManualTxnRequest(transactionList[position].transaction_id, "FINALIZE", transactionList[position].payment_status),
                    "CLOSE", mid, position, reycler, context
                )
            }
            holder.acceptBtn.text = "Pesanan Tiba"
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
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
                holder.acceptBtn.setOnClickListener {
                    updateStatus(
                        UpdateStatusManualTxnRequest(transactionList[position].transaction_id, "CLOSE", transactionList[position].payment_status),
                        "CLOSE", mid, position, reycler, context
                    )
                }
                holder.acceptBtn.setBackgroundResource(R.drawable.button_green_transparent)
            } else {
                holder.acceptBtn.isEnabled = false
                holder.acceptBtn.setTextColor(context.resources.getColor(R.color.borderSubtle))
                holder.acceptBtn.setBackgroundResource(R.drawable.button_gray_transparent)
            }
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
        } else if (transactionList[position].order_status == "CLOSE"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Selesai"
            holder.orderStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.rejectBtn.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
        } else if (transactionList[position].order_status == "CANCELLED"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Batal"
            holder.orderStatus.setBackgroundResource(R.drawable.button_red_square)
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            if (transactionList[position].payment_status == "PAID"){
                holder.statusPayment.setTextColor(context.resources.getColor(R.color.green))
                holder.statusPayment.text = "Sudah Bayar"
                holder.updatePaymentBtn.text = "Refund ke Pelanggan"
                holder.updatePaymentBtn.visibility = View.VISIBLE
                holder.updatePaymentBtn.setOnClickListener {
                    updateStatus(UpdateStatusManualTxnRequest(
                        transactionList[position].transaction_id,
                        transactionList[position].order_status,
                        "REFUNDED"),
                        "CANCELLED", mid, position, reycler, context)
                }
            } else if (transactionList[position].payment_status == "CANCELLED"){
                holder.statusPayment.text = "Dibatalkan"
                holder.statusPayment.setTextColor(context.resources.getColor(R.color.red))
                holder.updatePaymentBtn.visibility = View.GONE
            } else if (transactionList[position].payment_status == "REFUNDED"){
                holder.statusPayment.text = "Dana Dikembalikan"
                holder.statusPayment.setTextColor(context.resources.getColor(R.color.orange))
                holder.updatePaymentBtn.visibility = View.GONE
            } else {
                Timber.tag(tag).d("Invalid status payment")
            }
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
        } else if (transactionList[position].order_status == "FAILED"){
            setDate(position)
            setUpCard(holder, position)
            holder.orderStatus.text = "Gagal"
            holder.orderStatus.setBackgroundResource(R.drawable.button_red_square)
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.callBtn.setOnClickListener {
                temp = ""
                number = ""
                temp = transactionList[position].customer?.phone_number.toString().substringAfter("0")
                number = "+62 $temp"
                openWhatsapp(number)
            }
        } else {
            Timber.tag(tag).d("Invalid Status Order")
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
        } else if (transactionList[position].payment_status == "UNPAID") {
            holder.statusPayment.text = "Belum Bayar"
            holder.statusPayment.setTextColor(context.resources.getColor(R.color.red))
            holder.updatePaymentBtn.setOnClickListener {
                var tabStatus = ""
                if(transactionList[position].order_status == "ON_PROCESS"){
                    tabStatus = "ON_PROCESS"
                }else{
                    tabStatus = "CLOSE"
                }
                openCancelDialog(transactionList[position].customer?.customer_name.toString(), transactionList[position].order_platform.toString(),
                    transactionList[position].transaction_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                            transactionList[position].transaction_time.toString().substringBefore("-") +", "+ transactionList[position].transaction_time.toString().substringAfter(" ").substringBeforeLast(":"),
                transactionList[position].shipping?.shipping_method.toString(),
                transactionList[position].transaction_id.toString(), transactionList[position].order_status.toString(), tabStatus, position)
            }
        } else if (transactionList[position].payment_status == "FAILED"){
            holder.statusPayment.setTextColor(context.resources.getColor(R.color.red))
            holder.statusPayment.text = "Gagal"
            holder.updatePaymentBtn.visibility = View.GONE
        }
    }

    private fun setUpLogo(holder: ViewHolder, position: Int){
        if (transactionList[position].order_platform == "Instagram" ||
            transactionList[position].order_platform == "INSTAGRAM"
        ){
            holder.logo.setImageResource(R.drawable.logo_ig)
        } else if (
            transactionList[position].order_platform == "Whatsapp" ||
            transactionList[position].order_platform == "WHATSAPP"
        ){
            holder.logo.setImageResource(R.drawable.logo_wa)
        } else if (
            transactionList[position].order_platform == "Telepon" ||
            transactionList[position].order_platform == "PHONE_CALL"
        ){
            holder.logo.setImageResource(R.drawable.logo_phone)
        } else if (transactionList[position].order_platform == "PIKAPP"){
            holder.logo.setImageResource(R.drawable.logo_pikapp_square)
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

    private fun openWhatsapp(number: String){
        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        activity.startActivity(i)
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

    private fun updateStatus(status: UpdateStatusManualTxnRequest, tabStatus: String, mid: String, position: Int,recycler: RecyclerView, basecontext: Context){
        PikappApiService().api.postUpdateManualTransaction(status).enqueue(object : Callback<UpdateStatusManualResponse>{
            override fun onResponse(
                call: Call<UpdateStatusManualResponse>,
                response: Response<UpdateStatusManualResponse>
            ) {
                if (response.code() == 200){
                    callApi(tabStatus, mid, position, recycler, basecontext)
                }
            }

            override fun onFailure(call: Call<UpdateStatusManualResponse>, t: Throwable) {
                Log.e("Fail", t.message.toString())
            }
        })
    }

    private fun callApi(status: String, mid: String, position: Int, recycler: RecyclerView, base: Context){
        lateinit var manualTxnAdapter: ManualTxnListAdapter
        PikappApiService().api.getManualTransactionList(
            size = 1000,
            page = 0,
            mid,
            status
        ).enqueue(object : Callback<GetManualTransactionResp>{
            override fun onResponse(
                call: Call<GetManualTransactionResp>,
                response: Response<GetManualTransactionResp>
            ) {
                val response = response.body()
                val result = response?.results
                val productList = ArrayList<ArrayList<ManualProductListResponse>>()
                if(result != null){
                    if(response.results.isEmpty()){
                        recycler.visibility = View.GONE
                        val productList1 = ArrayList<ArrayList<ManualProductListResponse>>()
                        manualTxnAdapter = ManualTxnListAdapter(
                            base,
                            ArrayList(),
                            ArrayList(),
                            mid,
                            reycler,
                            activity
                        )
                        recycler.adapter = manualTxnAdapter
                        manualTxnAdapter.notifyItemRemoved(position)
                    }
                    recycler.visibility = View.VISIBLE
                    for (r in result){
                        r.productList.let { productList.add(it as ArrayList<ManualProductListResponse>) }
                        manualTxnAdapter = ManualTxnListAdapter(
                            base,
                            result as MutableList<ManualTransactionResult>,
                            productList as MutableList<List<ManualProductListResponse>>,
                            mid,
                            reycler,
                            activity
                        )
                        recycler.adapter = manualTxnAdapter
                        manualTxnAdapter.notifyDataSetChanged()
                    }
                } else {
                    recycler.visibility = View.GONE
                    val productList1 = ArrayList<ArrayList<ManualProductListResponse>>()
                    manualTxnAdapter = ManualTxnListAdapter(
                        base,
                        ArrayList(),
                        ArrayList(),
                        mid,
                        reycler,
                        activity
                    )
                    recycler.adapter = manualTxnAdapter
                    manualTxnAdapter.notifyItemRemoved(position)
                    Timber.tag(tag).d("Result is null")
                }
            }

            override fun onFailure(call: Call<GetManualTransactionResp>, t: Throwable) {
                Timber.tag(tag).d("Failed to show transaction : ${t.message.toString()}")
            }

        })
    }

    private fun openCancelDialog(nama: String, platform: String, tanggal: String, ekspedisi: String, id: String, txnStatus: String, tabStatus: String, position: Int) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.payment_dialog, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )

        mDialogView.nama.text = "Pesanan atas nama $nama melalui $platform tanggal $tanggal dan dikirim melalui $ekspedisi"
        mDialogView.dialog_back.setOnClickListener {
            mAlertDialog.dismiss()
        }

        mDialogView.dialog_update.setOnClickListener {
            updateStatus(
                UpdateStatusManualTxnRequest(transactionList[position].transaction_id, txnStatus, "PAID"),
                tabStatus, mid, position, reycler, context
            )
            openDoneDialog(nama)
            mAlertDialog.dismiss()
        }
    }

    private fun openDoneDialog(nama: String) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.update_payment_done, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )

        mDialogView.nama.text = "Status pembayaran untuk pesanan atas nama $nama berubah menjadi sudah dibayar"
        mDialogView.btnDone.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }


}
