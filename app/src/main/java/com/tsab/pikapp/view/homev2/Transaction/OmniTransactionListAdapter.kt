package com.tsab.pikapp.view.homev2.Transaction

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.delete_etalase_popup.view.*
import kotlinx.android.synthetic.main.omni_tokped_popup.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.acceptButton
import kotlinx.android.synthetic.main.transaction_list_items.view.lastOrder
import kotlinx.android.synthetic.main.transaction_list_items.view.loadingOverlay
import kotlinx.android.synthetic.main.transaction_list_items.view.menuCount
import kotlinx.android.synthetic.main.transaction_list_items.view.orderDate
import kotlinx.android.synthetic.main.transaction_list_items.view.paymentStatus
import kotlinx.android.synthetic.main.transaction_list_items.view.recyclerview_menu
import kotlinx.android.synthetic.main.transaction_list_items.view.rejectButton
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice2
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class OmniTransactionListAdapter (
        private val context: Context,
        private var omniList: MutableList<OrderDetailOmni>,
        private val omniDetailList: MutableList<List<ProductDetailOmni>>,
        private val sessionManager: SessionManager,
        private val supportFragmentManager: FragmentManager,
        private val prefHelper: SharedPreferencesUtil,
        private val recyclerView: RecyclerView,
        private val activity: Activity,
        private val logisticList: MutableList<LogisticsDetailOmni>,
        private val empty: ConstraintLayout
) : RecyclerView.Adapter<OmniTransactionListAdapter.ViewHolder>() {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var omniAdapter: OmniTransactionListAdapter
    var orderResult = ArrayList<OrderDetailOmni>()
    var jumlah = 0
    var price = 0
    var str : String = ""
    val reasonsheet = CancelReasonFragment()
    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var biz: String = ""
    var isLoading: Boolean = false
    var prosesList = ArrayList<OrderDetailOmni>()
    var batalList = ArrayList<OrderDetailOmni>()
    var doneList = ArrayList<OrderDetailOmni>()
    var menuList = ArrayList<ArrayList<ProductDetailOmni>>()
    var menuList1 = ArrayList<ArrayList<ProductDetailOmni>>()
    var menuList2 = ArrayList<ArrayList<ProductDetailOmni>>()
    var deliveryMethod: String = ""
    var deliveryStatus: String = ""
    val gson = Gson()
    val type = object : TypeToken<AcceptOrderTokopediaResponse>() {}.type

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_items_omni, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setMenu(holder.rView, omniDetailList[position] as MutableList<ProductDetailOmni>)
        sessionManager.setHomeNav(0)
        if(omniList[position].channel == "TOKOPEDIA"){
            setTokopediaCardView(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return omniList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderDate: TextView = itemView.orderDateOmni
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var price: TextView = itemView.totalPrice
        var price2: TextView = itemView.totalPrice2
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu_omni
        var lastOrder: TextView = itemView.lastOrder
        var loadingOverlay: View = itemView.loadingOverlay
        var deliveryStatus: TextView = itemView.deliveryStatus
        var deliveryMethod: TextView = itemView.deliveryMethod
    }

    private fun setTokopediaCardView(holder: ViewHolder, position: Int){
        if (omniList[position].status == "PAYMENT_VERIFIED") {
            setDate(position)
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.visibility = View.GONE
            holder.price.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
            holder.acceptBtn.text = "Siap Dikirim"
            holder.acceptBtn.setOnClickListener {
                updateTransaction(omniList[position].channel.toString(), omniList[position].orderId.toString(), "Proses", holder)
            }
            holder.rejectBtn.setOnClickListener {
                Log.e("msg", "clicked")
                openDialogTokopedia(position)
            }
            timeAgo(omniList[position].orderTime.toString(), holder.lastOrder)
        } else if (omniList[position].status == "SELLER_ACCEPT_ORDER"){
            setDate(position)
            holder.paymentStatus.text = "Diproses"
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.visibility = View.GONE
            holder.price.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
            holder.acceptBtn.text = "Upload Resi"
            holder.acceptBtn.setOnClickListener {
                openDialogTokopedia(position)
            }
            holder.rejectBtn.visibility = View.GONE
            timeAgo(omniList[position].orderTime.toString(), holder.lastOrder)
        } else if (omniList[position].status == "WAITING_FOR_PICKUP"){
            setDate(position)
            holder.paymentStatus.text = "Diproses"
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.visibility = View.GONE
            holder.price.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
            holder.acceptBtn.text = "Atur Pengiriman"
            holder.acceptBtn.setOnClickListener {
                openDialogTokopedia(position)
            }
            holder.rejectBtn.visibility = View.GONE
            timeAgo(omniList[position].orderTime.toString(), holder.lastOrder)
        } else if (omniList[position].status == "SELLER_CANCEL_ORDER" || omniList[position].status == "ORDER_REJECTED_BY_SELLER"){
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Gagal"
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "ORDER_DELIVERED" || omniList[position].status == "ORDER_FINISHED"){
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Selesai"
            holder.paymentStatus.setBackgroundResource(R.drawable.button_green_square)
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "ORDER_SHIPMENT" || omniList[position].status == "DELIVERED_TO_PICKUP_POINT"){
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Dikirim"
            holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "BUYER_OPEN_A_CASE_TO_FINISH_AN_ORDER"){
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Dikomplain"
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")?.substringBefore("T") + bulan + omniList[position].orderTime.toString().substringAfter("T")?.substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        }
    }

    private fun openDialogTokopedia(position: Int){
        if (omniList[position].status == "PAYMENT_VERIFIED" || omniList[position].status == "PAYMENT_CONFIRMATION") {
            val mDialogView = LayoutInflater.from(activity).inflate(R.layout.omni_tokped_popup, null)
            val mBuilder = AlertDialog.Builder(activity)
                    .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                            activity,
                            R.drawable.dialog_background
                    )
            )
            mDialogView.dialog_back.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.dialog_close.setOnClickListener {
                mAlertDialog.dismiss()
            }
        } else if (omniList[position].status == "SELLER_ACCEPT_ORDER"){
            val mDialogView = LayoutInflater.from(activity).inflate(R.layout.omni_tokped_popup, null)
            val mBuilder = AlertDialog.Builder(activity)
                    .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                            activity,
                            R.drawable.dialog_background
                    )
            )
            mDialogView.dialog_back.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.dialog_close.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.dialog_text.text = "Mohon upload resi di aplikasi e-\ncommerce sesuai pesanan untuk\nmenyelesaikan order ini."
        } else if (omniList[position].status == "WAITING_FOR_PICKUP"){
            val mDialogView = LayoutInflater.from(activity).inflate(R.layout.omni_tokped_popup, null)
            val mBuilder = AlertDialog.Builder(activity)
                    .setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window?.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                            activity,
                            R.drawable.dialog_background
                    )
            )
            mDialogView.dialog_back.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.dialog_close.setOnClickListener {
                mAlertDialog.dismiss()
            }
            mDialogView.dialog_text.text = "Mohon Atur Pengiriman di aplikasi e-\ncommerce sesuai pesanan untuk\nmenyelesaikan order ini."
        }
    }

    private fun updateTransaction(channel: String, orderId: String, status: String, holder: ViewHolder){
        holder.loadingOverlay.visibility = View.VISIBLE
        setIsLoading(true)
        postUpdate(channel, orderId)
        getListOmni(context, recyclerView, supportFragmentManager, activity, status, empty, holder)
    }

    private fun setMenu(recyclerView: RecyclerView, omniDetailList: MutableList<ProductDetailOmni>){
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = OmniTransactionProductAdapter(context, omniDetailList)
        recyclerView.adapter = menuList1
    }

    private fun setDate(position: Int){
        bulanTemp = omniList[position].orderTime.toString().substringAfter("-")?.substringBeforeLast("-").toString()
        if(bulanTemp == "01"){
            bulan = " Jan "
        }else if(bulanTemp == "02"){
            bulan = " Feb "
        }else if(bulanTemp == "03"){
            bulan = " Mar "
        }else if(bulanTemp == "04"){
            bulan = " Apr "
        }else if(bulanTemp == "05"){
            bulan = " Mei "
        }else if(bulanTemp == "06"){
            bulan = " Jun "
        }else if(bulanTemp == "07"){
            bulan = " Jul "
        }else if(bulanTemp == "08"){
            bulan = " Ags "
        }else if(bulanTemp == "09"){
            bulan = " Sep "
        }else if(bulanTemp == "10"){
            bulan = " Okt "
        }else if(bulanTemp == "11"){
            bulan = " Nov "
        }else if(bulanTemp == "12"){
            bulan = " Des "
        }

        for(count in omniDetailList[position]){
            jumlah = jumlah + count.quantity!!.toInt()
            if (count.quantity > 1){
                price += count.quantity * count.price!!.toInt()
            } else {
                price += count.price!!.toInt()
            }
        }
    }

    private fun formatNumber(){
        str = NumberFormat.getNumberInstance(Locale.US).format(price)
    }

    private fun timeAgo(time: String, holder: TextView){
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
        var txnTime : Date = format.parse(time)
        var timeNow : Date = Date()
        var seconds : Long = TimeUnit.MILLISECONDS.toSeconds(timeNow.time - txnTime.time)
        var minutes : Long = TimeUnit.MILLISECONDS.toMinutes(timeNow.time - txnTime.time)
        var hours : Long = TimeUnit.MILLISECONDS.toHours(timeNow.time - txnTime.time)
        var days : Long = TimeUnit.MILLISECONDS.toDays(timeNow.time - txnTime.time)

        if(seconds < 60){
            holder.text = "Baru Saja"
        }else if(minutes < 60){
            holder.text = minutes.toString() + " Menit Yang Lalu"
        }else{
            holder.text = hours.toString() + " Jam Yang Lalu"
        }
    }

    private fun postUpdate(channel: String, orderId: String){
        setIsLoading(true)
        val mid = sessionManager.getUserData()!!.mid!!
        var acceptOrderReq = AcceptOrderTokopediaRequest()
        acceptOrderReq.channel = channel
        acceptOrderReq.orderId = orderId
        acceptOrderReq.mid = mid
        Log.e("accept order channel", channel)
        Log.e("accept order id", orderId)
        Log.e("mid", mid)

        PikappApiService().api.acceptOrderTokopedia(
                getUUID(), getTimestamp(), getClientID(), acceptOrderReq
        ).enqueue(object : Callback<AcceptOrderTokopediaResponse>{
            override fun onFailure(call: Call<AcceptOrderTokopediaResponse>, t: Throwable) {
                Toast.makeText(context, "fail: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AcceptOrderTokopediaResponse>, response: Response<AcceptOrderTokopediaResponse>) {
                var orderResponse = response.body()
                var errorResponse: AcceptOrderTokopediaResponse? =
                    gson.fromJson(response.errorBody()!!.charStream(), type)
                Log.e("acceot order err code", orderResponse?.errCode.toString())
                Log.e("acceot order err msg", orderResponse?.errMessage.toString())
                Log.e("acceot order result", orderResponse?.results.toString())
                Log.e("accept error response", errorResponse?.errCode)
                Log.e("accept error response", errorResponse?.errMessage)
                Toast.makeText(context, "error: " + errorResponse?.errMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setIsLoading(value:Boolean) {
        isLoading = value
    }

    fun getListOmni(baseContext: Context, recyclerview_transaction: RecyclerView, support: FragmentManager, activity: Activity, status: String, empty: ConstraintLayout, holder: ViewHolder){
        prefHelper.clearStoreOrderList()
        val mid = sessionManager.getUserData()!!.mid!!
        val page = "0"
        val size = "5"

        PikappApiService().api.getListOrderOmni(
                getUUID(), getTimestamp(), getClientID(), mid, page, size
        ).enqueue(object : Callback<ListOrderOmni> {
            override fun onFailure(call: Call<ListOrderOmni>, t: Throwable) {
                Toast.makeText(baseContext, "error: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ListOrderOmni>, response: Response<ListOrderOmni>) {
                val response = response.body()
                val resultList = response?.results
                orderResult.addAll(resultList as MutableList<OrderDetailOmni>)
                omniList.addAll(orderResult)
                var prosesList = ArrayList<OrderDetailOmni>()
                var batalList = ArrayList<OrderDetailOmni>()
                var doneList = ArrayList<OrderDetailOmni>()
                var productList = ArrayList<ArrayList<ProductDetailOmni>>()
                var producList1 = ArrayList<ArrayList<ProductDetailOmni>>()
                var productList2 = ArrayList<ArrayList<ProductDetailOmni>>()
                if (resultList != null){
                    for (result in resultList){
                        getOrderDetailOmni(result.orderId.toString())
                        if(result.status == "PAYMENT_CONFIRMATION" || result.status == "PAYMENT_VERIFIED" || result.status == "SELLER_ACCEPT_ORDER" || result.status == "WAITING_FOR_PICKUP"){
                            prosesList.add(result)
                            result.producDetails.let { productList.add(it as ArrayList<ProductDetailOmni>) }
                        }else if(result.status == "SELLER_CANCEL_ORDER" || result.status == "ORDER_REJECTED_BY_SELLER"){
                            batalList.add(result)
                            result.producDetails?.let { producList1.add(it as ArrayList<ProductDetailOmni>) }
                        }else{
                            doneList.add(result)
                            result.producDetails?.let { productList2.add(it as ArrayList<ProductDetailOmni>) }
                        }
                    }
                }
                Handler().postDelayed({
                    if(status == "Proses"){
                        empty.isVisible = prosesList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                prosesList as MutableList<OrderDetailOmni>, productList as MutableList<List<ProductDetailOmni>>, sessionManager, support, prefHelper, recyclerview_transaction, activity, logisticList as MutableList<LogisticsDetailOmni>, empty)
                        omniAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = omniAdapter
                        omniAdapter.notifyDataSetChanged()
                    }
                    if(status == "Batal"){
                        empty.isVisible = batalList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                prosesList as MutableList<OrderDetailOmni>, producList1 as MutableList<List<ProductDetailOmni>>, sessionManager, support, prefHelper, recyclerview_transaction, activity, logisticList as MutableList<LogisticsDetailOmni>, empty)
                        omniAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = omniAdapter
                        omniAdapter.notifyDataSetChanged()
                    }
                    if(status == "Done"){
                        empty.isVisible = doneList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                                baseContext,
                                prosesList as MutableList<OrderDetailOmni>, productList2 as MutableList<List<ProductDetailOmni>>, sessionManager, support, prefHelper, recyclerview_transaction, activity, logisticList as MutableList<LogisticsDetailOmni>, empty)
                        omniAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = omniAdapter
                        omniAdapter.notifyDataSetChanged()
                    }
                    setIsLoading(false)
                    holder.loadingOverlay.visibility = View.GONE
                }, 1500)
            }
        })
    }

    fun getOrderDetailOmni(orderId: String){
        var orderId = orderId
        PikappApiService().api.getListOrderDetailOmni(
                getUUID(), getTimestamp(), getClientID(), orderId
        ).enqueue(object : Callback<ListOrderDetailOmni>{
            override fun onFailure(call: Call<ListOrderDetailOmni>, t: Throwable) {
                Log.e("msg", "error: $t")
            }

            override fun onResponse(call: Call<ListOrderDetailOmni>, response: Response<ListOrderDetailOmni>) {
                val orderResponse = response.body()
                val resultList = orderResponse?.results
                val arrayResultLit = ArrayList<OrderDetailDetailOmni>()
                if (resultList != null){
                    arrayResultLit.add(resultList)
                    for (result in arrayResultLit){
                        if (result.logistics != null) {
                            logisticList.add(result.logistics)
                        }
                    }
                }
            }
        })
    }
}