package com.tsab.pikapp.view.homev2.transaction

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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.transaction.shipment.ResiTokopediaDialogFragment
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.omni_tokped_popup.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.acceptButton
import kotlinx.android.synthetic.main.transaction_list_items.view.lastOrder
import kotlinx.android.synthetic.main.transaction_list_items.view.loadingOverlay
import kotlinx.android.synthetic.main.transaction_list_items.view.menuCount
import kotlinx.android.synthetic.main.transaction_list_items.view.paymentStatus
import kotlinx.android.synthetic.main.transaction_list_items.view.rejectButton
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TransactionListV2Adapter(
    private val context: Context,
    private val activity: Activity,
    private val supportFragmentManager: FragmentManager,
    private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_DINE_IN = 0
        const val VIEW_TYPE_OMNICHANNEL = 1
        const val VIEW_TYPE_DELIVERY = 2
    }

    private var list: List<TransactionListV2Data> = ArrayList()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()
    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var biz: String = ""
    var jumlah = 0
    var menuPrice = 0
    var str: String = ""
    val reasonsheet = CancelReasonFragment()
    var isLoading: Boolean = false
    private val sessionManager = SessionManager()
    private val prefHelper = SharedPreferencesUtil()

    fun setTransactionList(transactionList: List<TransactionListV2Data>) {
        this.list = transactionList
    }

    private inner class ViewHolderDineIn(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tableStatus: TextView = itemView.tableStatus
        var tableNumber: TextView = itemView.tableNumber
        var orderDate: TextView = itemView.orderDate
        var orderStatus: TextView = itemView.orderStatus
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var totalPrice: TextView = itemView.totalPrice
        var totalPrice2: TextView = itemView.totalPrice2
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu
        var lastOrder: TextView = itemView.lastOrder
        var loadingOverlay: View = itemView.loadingOverlay
        fun bind(position: Int) {
            val recyclerViewDineIn = list[position]
            val orderStatusDineIn = recyclerViewDineIn.order_status

            setMenu(rView, recyclerViewDineIn.products)

            if (orderStatusDineIn == "OPEN" || orderStatusDineIn == "PAID" || orderStatusDineIn == "ON_PROCESS") {
                setDate(position)
                when (orderStatusDineIn) {
                    "PAID" -> {
                        orderStatus.visibility = View.GONE
                        paymentStatus.text = "NEW"
                        acceptBtn.text = "Terima"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransaction(txnId, "ON_PROCESS", "Proses", loadingOverlay)
                        }
                        rejectBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("TransactionID", recyclerViewDineIn.transaction_id)
                            reasonsheet.arguments = bundle
                            reasonsheet.show(supportFragmentManager, "show")
                        }
                    }
                    "OPEN" -> {
                        orderStatus.text = "NEW"
                        acceptBtn.text = "Terima"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransaction(txnId, "ON_PROCESS", "Proses", loadingOverlay)
                        }
                        rejectBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("TransactionID", recyclerViewDineIn.transaction_id)
                            reasonsheet.arguments = bundle
                            reasonsheet.show(supportFragmentManager, "show")
                        }
                    }
                    "ON_PROCESS" -> {
                        orderStatus.visibility = View.GONE
                        acceptBtn.text = "Pesanan Siap"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransaction(txnId, "DELIVER", "Proses", loadingOverlay)
                        }
                        rejectBtn.visibility = View.GONE
                    }
                }
                timeAgo(recyclerViewDineIn.transaction_time.toString(), lastOrder)
                tableNumber.text = "Meja " + recyclerViewDineIn.table_no
                tableStatus.text = biz
                orderDate.text = recyclerViewDineIn.transaction_time?.substringAfterLast("-")
                    ?.substringBefore(" ") + bulan + recyclerViewDineIn.transaction_time?.substringAfter(
                    " "
                )?.substringBeforeLast(":")
                menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                totalPrice.text = "Rp " + str
                menuPrice = 0
            } else if (orderStatusDineIn == "FAILED" || orderStatusDineIn == "ERROR") {
                setDate(position)
                orderStatus.visibility = View.GONE
                rView.visibility = View.GONE
                acceptBtn.visibility = View.GONE
                rejectBtn.visibility = View.GONE
                paymentStatus.text = "Gagal"
                tableNumber.text = "Meja " + recyclerViewDineIn.table_no
                tableStatus.text = biz
                orderDate.textSize = 10F
                orderDate.text =
                    "ID Transaksi: " + recyclerViewDineIn.transaction_id?.substringBefore("-")
                lastOrder.textSize = 11F
                lastOrder.text =
                    recyclerViewDineIn.transaction_time?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + recyclerViewDineIn.transaction_time?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                totalPrice2.text = "Rp " + str
                menuPrice = 0
                totalPrice.visibility = View.GONE
            } else if (orderStatusDineIn == "DELIVER" || orderStatusDineIn == "CLOSE" || orderStatusDineIn == "FINALIZE") {
                setDate(position)
                when (orderStatusDineIn) {
                    "DELIVER" -> {
                        acceptBtn.text = "Selesai"
                        paymentStatus.text = "Dikirim"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransaction(txnId, "CLOSE", "Done", loadingOverlay)
                        }
                    }
                    else -> {
                        acceptBtn.visibility = View.GONE
                        paymentStatus.text = "Selesai"
                    }
                }
                orderStatus.visibility = View.GONE
                totalPrice.visibility = View.GONE
                rView.visibility = View.GONE
                rejectBtn.visibility = View.GONE
                paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                tableNumber.text = "Meja " + recyclerViewDineIn.table_no
                tableStatus.text = biz
                orderDate.textSize = 10F
                orderDate.text = "ID Transaksi: " + recyclerViewDineIn.transaction_id?.substringBefore("-")
                lastOrder.textSize = 11F
                lastOrder.text =
                    recyclerViewDineIn.transaction_time?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + recyclerViewDineIn.transaction_time?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                menuPrice = 0
                formatNumber()
                totalPrice2.text = "Rp " + str
            }
        }
    }

    private inner class ViewHolderOmnichannel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderDate: TextView = itemView.orderDateOmni
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var totalPrice: TextView = itemView.totalPrice
        var totalPrice2: TextView = itemView.totalPriceOmni
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu_omni
        var lastOrder: TextView = itemView.lastOrder
        var loadingOverlay: View = itemView.loadingOverlay
        var deliveryStatus: TextView = itemView.deliveryStatus
        var deliveryMethod: TextView = itemView.deliveryMethod
        var omniLogo: ImageView = itemView.omniChannelIcon
        fun bind(position: Int) {
            val recyclerViewOmni = list[position]

            val omniChannelName = recyclerViewOmni.channel
            val orderStatusChannel = recyclerViewOmni.order_status
            val logisticChannel = recyclerViewOmni.shipping

            setDate(position)
            if (omniChannelName ==  "TOKOPEDIA") { // THIS CONDITION IS FOR TOKOPEDIA OMNICHANNEL (ALL STATUS WOULD BE APPEAR HERE)
                if (logisticChannel != null) {
                    deliveryMethod.text = logisticChannel.shipping_method
                    deliveryStatus.text = logisticChannel.shipping_service
                }
                orderDate.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                    .substringBefore("T") + bulan + recyclerViewOmni.transaction_time.toString()
                    .substringAfter("T").substringBeforeLast(":")
                formatNumber()
                if (orderStatusChannel == "PAYMENT_VERIFIED") {
                    totalPrice2.visibility = View.GONE
                    totalPrice.text = "Rp. $str"
                    acceptBtn.text = "Siap Dikirim"
                    acceptBtn.setOnClickListener {
                        updateTransactionChannel(
                            recyclerViewOmni.channel.toString(),
                            recyclerViewOmni.order_id.toString(),
                            "Proses",
                            loadingOverlay
                        )
                    }
                    rejectBtn.setOnClickListener {
                        /*rejectDialog(position)*/
                        openDialogTokopedia(position)
                    }
                    timeAgo(recyclerViewOmni.transaction_time.toString(), lastOrder)
                } else if (orderStatusChannel == "SELLER_ACCEPT_ORDER") {
                    paymentStatus.text = "Diproses"
                    paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                    totalPrice2.visibility = View.GONE
                    totalPrice.text = "Rp. $str"
                    acceptBtn.text = "Upload Resi"
                    acceptBtn.setOnClickListener {
                        openDialogTokopedia(position)
                    }
                    rejectBtn.visibility = View.GONE
                    timeAgo(recyclerViewOmni.transaction_time.toString(), lastOrder)
                } else if (orderStatusChannel == "WAITING_FOR_PICKUP") {
                    paymentStatus.text = "Diproses"
                    paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                    totalPrice2.visibility = View.GONE
                    totalPrice.text = "Rp. $str"
                    acceptBtn.text = "Atur Pengiriman"
                    acceptBtn.setOnClickListener {
                        openDialogTokopedia(position)
                    }
                    rejectBtn.visibility = View.GONE
                    timeAgo(recyclerViewOmni.transaction_time.toString(), lastOrder)
                } else if (orderStatusChannel == "SELLER_CANCEL_ORDER" || orderStatusChannel == "ORDER_REJECTED_BY_SELLER") {
                    rView.visibility = View.GONE
                    acceptBtn.visibility = View.GONE
                    rejectBtn.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                    paymentStatus.text = "Gagal"
                    orderDate.text = "ID Transaksi: " + recyclerViewOmni.order_id
                    totalPrice2.text = "Rp. $str"
                } else if (orderStatusChannel == "ORDER_DELIVERED" || orderStatusChannel == "ORDER_FINISHED") {
                    rView.visibility = View.GONE
                    acceptBtn.visibility = View.GONE
                    rejectBtn.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                    paymentStatus.text = "Selesai"
                    paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                    orderDate.text = "ID Transaksi: " + recyclerViewOmni.order_id
                    totalPrice.text = "Rp. $str"
                } else if (orderStatusChannel == "ORDER_DELIVERED" || orderStatusChannel == "ORDER_FINISHED") {
                    rView.visibility = View.GONE
                    acceptBtn.visibility = View.GONE
                    rejectBtn.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                    paymentStatus.text = "Dikirim"
                    paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                    orderDate.text = "ID Transaksi: " + recyclerViewOmni.order_id
                    totalPrice2.text = "Rp. $str"
                } else if (orderStatusChannel == "BUYER_OPEN_A_CASE_TO_FINISH_AN_ORDER") {
                    rView.visibility = View.GONE
                    acceptBtn.visibility = View.GONE
                    rejectBtn.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                    paymentStatus.text = "Dikomplain"
                    orderDate.text = "ID Transaksi: " + recyclerViewOmni.order_id
                    totalPrice2.text = "Rp. $str"
                }
                menuPrice = 0
                menuCount.text = "Total $jumlah items:"
                jumlah = 0
            } else if (omniChannelName ==  "GRAB") { // THIS CONDITION IS FOR GRAB OMNICHANNEL (ALL STATUS WOULD BE APPEAR HERE)
                omniLogo.setImageResource(R.drawable.grabfood)
                deliveryMethod.text = recyclerViewOmni.transaction_id
                deliveryStatus.visibility = View.GONE
                formatNumber()
                if (orderStatusChannel == "DRIVER_ALLOCATED" || orderStatusChannel == "DRIVER_ARRIVED") {
                    paymentStatus.text = "Diproses"
                    paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                    orderDate.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                        .substringBefore("T") + bulan + recyclerViewOmni.transaction_time.toString()
                        .substringAfter("T").substringBeforeLast(":")
                    totalPrice2.visibility = View.GONE
                    totalPrice.text = "Rp. $str"
                    timeAgo(recyclerViewOmni.transaction_time.toString(), lastOrder)
                } else {
                    orderDate.visibility = View.GONE
                    lastOrder.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                        .substringBefore("T") + bulan + recyclerViewOmni.transaction_time.toString()
                        .substringAfter("T").substringBeforeLast(":")
                    if (orderStatusChannel == "DELIVERED") {
                        paymentStatus.text = "Selesai"
                        paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                    } else if (orderStatusChannel == "COLLECTED") {
                        paymentStatus.text = "Dikirim"
                        paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                    } else if (orderStatusChannel == "CANCELLED" || orderStatusChannel == "FAILED") {
                        paymentStatus.text = "Gagal"
                    }
                    rView.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                    totalPrice2.text = "Rp. $str"
                }
                acceptBtn.visibility = View.GONE
                rejectBtn.visibility = View.GONE
                menuPrice = 0
                menuCount.text = "Total $jumlah items:"
                jumlah = 0
            }
        }
    }

    private inner class ViewHolderDelivery(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val recyclerViewDelivery = list[position]
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_OMNICHANNEL) {
            return ViewHolderOmnichannel(
                LayoutInflater.from(context).inflate(R.layout.transaction_list_items_omni, parent, false)
            )
        } else if (viewType == VIEW_TYPE_DELIVERY) {
            return ViewHolderDelivery(
                LayoutInflater.from(context).inflate(R.layout.transaction_list_items_manual, parent, false)
            )
        }
        return ViewHolderDineIn(
            LayoutInflater.from(context).inflate(R.layout.transaction_list_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (list[position].viewType) {
            VIEW_TYPE_OMNICHANNEL -> (holder as ViewHolderOmnichannel).bind(position)
            VIEW_TYPE_DELIVERY -> (holder as ViewHolderDelivery).bind(position)
            else -> (holder as ViewHolderDineIn).bind(position)
        }
//        if (viewTypePosition == VIEW_TYPE_OMNICHANNEL) {
//            (holder as ViewHolderOmnichannel).bind(position)
//        } else if (viewTypePosition == VIEW_TYPE_DELIVERY) {
//            (holder as ViewHolderDelivery).bind(position)
//        } else {
//            (holder as ViewHolderDineIn).bind(position)
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    /* OTHER FUNCTION */
    private fun updateTransaction(
        txnId: String,
        status: String,
        orderStatus: String,
        loadingOverlay: View
    ) {
//        loadingOverlay.visibility = View.VISIBLE
//        setIsLoading(true)
//        postUpdate(txnId, status)
        listener.onItemClickTransaction(txnId, status)
        Handler().postDelayed({
//            getStoreOrderList(status, orderStatus, loadingOverlay)
            notifyDataSetChanged()
        }, 2000)
    }

//    fun getStoreOrderList(deliveryStatus: String, status: String, loadingOverlay: View) {
//        setIsLoading(true)
//        prefHelper.clearStoreOrderList()
//        val email = sessionManager.getUserData()!!.email!!
//        val token = sessionManager.getUserToken()!!
//        val mid = sessionManager.getUserData()!!.mid!!
//
//        disposable.add(
//            pikappService.getTransactionListMerchant(email, token, mid)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : DisposableSingleObserver<GetStoreOrderListResponse>() {
//                    override fun onSuccess(t: GetStoreOrderListResponse) {
//                        val result = t.results
//                        orderResult.addAll(result as MutableList<StoreOrderList>)
//                        transactionList.addAll(orderResult)
//                        sortOrderList(result)
//                        setProcessOrder(
//                            context,
//                            recyclerView,
//                            status,
//                            supportFragmentManager,
//                            listener
//                        )
//                        setIsLoading(false)
//                        loadingOverlay.visibility = View.GONE
//                    }
//
//                    override fun onError(e: Throwable) {
//                        var errorResponse: ErrorResponse
//                        Toast.makeText(context, "failed: " + e.message, Toast.LENGTH_SHORT).show()
//                    }
//                })
//        )
//    }

    private fun setMenu(
        recyclerView: RecyclerView,
        productList: List<ProductDetailV2Data>?
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList = TransactionProductListV2Adapter(productList)
        recyclerView.adapter = menuList
    }

    private fun timeAgo(time: String, holder: TextView) {
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
        var txnTime: Date = format.parse(time)
        var timeNow: Date = Date()
        var seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeNow.time - txnTime.time)
        var minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timeNow.time - txnTime.time)
        var hours: Long = TimeUnit.MILLISECONDS.toHours(timeNow.time - txnTime.time)
        var days: Long = TimeUnit.MILLISECONDS.toDays(timeNow.time - txnTime.time)

        if (seconds < 60) {
            holder.text = "Baru Saja"
        } else if (minutes < 60) {
            holder.text = minutes.toString() + " Menit Yang Lalu"
        } else {
            holder.text = hours.toString() + " Jam Yang Lalu"
        }
    }

    private fun formatNumber() {
        str = NumberFormat.getNumberInstance(Locale.US).format(this.menuPrice)
    }

    private fun setDate(position: Int) {
        val transactionPositionList = list[position]
        bulanTemp = transactionPositionList.transaction_time?.substringAfter("-")?.substringBeforeLast("-").toString()
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

        if (transactionPositionList.biz_type == "TAKE_AWAY") {
            biz = "Bungkus/Takeaway"
        } else if (transactionPositionList.biz_type == "DINE_IN") {
            biz = "Makan di Tempat"
        }

        for (count in transactionPositionList.products!!) {
            jumlah += count.quantity!!.toInt()
            if (count.quantity > 1) {
                this.menuPrice += count.quantity * count.quantity
            } else {
                this.menuPrice += count.quantity
            }
        }
    }

    private fun openDialogTokopedia(position: Int) {
        val recyclerViewOmni = list[position].order_status
        if (recyclerViewOmni == "PAYMENT_VERIFIED" || recyclerViewOmni == "PAYMENT_CONFIRMATION") {
            val mDialogView =
                LayoutInflater.from(activity).inflate(R.layout.omni_tokped_popup, null)
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
        } else if (recyclerViewOmni == "SELLER_ACCEPT_ORDER") {
            ResiTokopediaDialogFragment().show(supportFragmentManager, ResiTokopediaDialogFragment.tag)
        } else if (recyclerViewOmni == "WAITING_FOR_PICKUP") {
            val mDialogView =
                LayoutInflater.from(activity).inflate(R.layout.omni_tokped_popup, null)
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
            mDialogView.dialog_text.text =
                "Mohon Atur Pengiriman di aplikasi e-\ncommerce sesuai pesanan untuk\nmenyelesaikan order ini."
        }
    }

    private fun updateTransactionChannel(
        channel: String,
        orderId: String,
        status: String,
        loadingOverlay: View
    ) {
        loadingOverlay.visibility = View.VISIBLE
        setIsLoading(true)
        postUpdateChannel(channel, orderId)
//        Handler().postDelayed({
//            getListOmni(
//                context,
//                recyclerView,
//                supportFragmentManager,
//                activity,
//                status,
//                empty,
//                holder
//            )
//            notifyDataSetChanged()
//        }, 2000)
    }

    private fun postUpdateChannel(channel: String, orderId: String) {
        setIsLoading(true)
        val mid = sessionManager.getUserData()!!.mid!!
        var acceptOrderReq = AcceptOrderTokopediaRequest()
        acceptOrderReq.channel = channel
        acceptOrderReq.order_id = orderId
        acceptOrderReq.mid = mid

        PikappApiService().api.acceptOrderTokopedia(
            getUUID(), getTimestamp(), getClientID(), acceptOrderReq
        ).enqueue(object : Callback<AcceptOrderTokopediaResponse> {
            override fun onFailure(call: Call<AcceptOrderTokopediaResponse>, t: Throwable) {
                Toast.makeText(context, "fail: $t", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<AcceptOrderTokopediaResponse>,
                response: Response<AcceptOrderTokopediaResponse>
            ) {
                Toast.makeText(context, "Transaksi Berhasil Di Update", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setIsLoading(value: Boolean) {
        isLoading = value
    }

    interface OnItemClickListener {
        fun onItemClickTransaction(txnId: String, status: String)
    }
}