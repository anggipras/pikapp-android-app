package com.tsab.pikapp.view.homev2.transaction

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.tsab.pikapp.view.homev2.transaction.shipment.ResiTokopediaDialogFragment
import kotlinx.android.synthetic.main.omni_tokped_popup.view.dialog_back
import kotlinx.android.synthetic.main.omni_tokped_popup.view.dialog_close
import kotlinx.android.synthetic.main.omni_tokped_popup.view.dialog_text
import kotlinx.android.synthetic.main.payment_dialog.view.*
import kotlinx.android.synthetic.main.payment_dialog.view.nama
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import kotlinx.android.synthetic.main.transaction_list_items.view.acceptButton
import kotlinx.android.synthetic.main.transaction_list_items.view.lastOrder
import kotlinx.android.synthetic.main.transaction_list_items.view.loadingOverlay
import kotlinx.android.synthetic.main.transaction_list_items.view.menuCount
import kotlinx.android.synthetic.main.transaction_list_items.view.paymentStatus
import kotlinx.android.synthetic.main.transaction_list_items.view.rejectButton
import kotlinx.android.synthetic.main.transaction_list_items.view.totalPrice
import kotlinx.android.synthetic.main.transaction_list_items_manual.view.*
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.*
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.omniChannelIcon
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.recyclerview_menu_omni
import kotlinx.android.synthetic.main.transaction_list_items_omni.view.totalPriceOmni
import kotlinx.android.synthetic.main.update_payment_done.view.*
import timber.log.Timber
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
    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var biz: String = ""
    var jumlah = 0
    var menuPrice = 0
    var str: String = ""
    val reasonsheet = CancelReasonFragment()

    /* GET LIST OF TRANSACTION FROM LIVEDATA MVVM */
    fun setTransactionList(transactionList: List<TransactionListV2Data>) {
        this.list = transactionList
        notifyDataSetChanged()
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
        var dividerTop: View = itemView.dividerTop
        fun bind(position: Int) {
            val recyclerViewDineIn = list[position]
            val orderStatusDineIn = recyclerViewDineIn.order_status

            setMenu(rView, recyclerViewDineIn.products)

            if (orderStatusDineIn == "OPEN" || orderStatusDineIn == "PAID" || orderStatusDineIn == "ON_PROCESS") {
                setDate(position, recyclerViewDineIn)
                when (orderStatusDineIn) {
                    "PAID" -> {
                        orderStatus.visibility = View.GONE
                        paymentStatus.text = "NEW"
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Terima"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransactionTxn(txnId, "ON_PROCESS", loadingOverlay)
                        }
                        rejectBtn.visibility = View.GONE
                        rejectBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("TransactionID", recyclerViewDineIn.transaction_id)
                            reasonsheet.arguments = bundle
                            reasonsheet.show(supportFragmentManager, "show")
                        }
                    }
                    "OPEN" -> {
                        paymentStatus.text = "UNPAID"
                        paymentStatus.setBackgroundResource(R.drawable.button_red_square)
                        orderStatus.visibility = View.VISIBLE
                        orderStatus.text = "NEW"
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Sudah Bayar"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransactionTxn(txnId, "ON_PROCESS", loadingOverlay)
                        }
                        rejectBtn.visibility = View.VISIBLE
                        rejectBtn.text = "Tolak"
                        rejectBtn.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("TransactionID", recyclerViewDineIn.transaction_id)
                            reasonsheet.arguments = bundle
                            reasonsheet.show(supportFragmentManager, "show")
                        }
                    }
                    "ON_PROCESS" -> {
                        orderStatus.visibility = View.GONE
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Pesanan Siap"
                        paymentStatus.text = "Diproses"
                        paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransactionTxn(txnId, "DELIVER", loadingOverlay)
                        }
                        rejectBtn.visibility = View.GONE
                    }
                }
                recyclerViewDineIn.transaction_time?.let { timeAgo(it, lastOrder) }
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
                setDate(position, recyclerViewDineIn)
                orderStatus.visibility = View.GONE
                rView.visibility = View.GONE
                dividerTop.visibility = View.GONE
                acceptBtn.visibility = View.GONE
                rejectBtn.visibility = View.GONE
                paymentStatus.text = "Gagal"
                tableNumber.text = "Meja " + recyclerViewDineIn.table_no
                tableStatus.text = biz
                orderDate.text = "ID Transaksi: " + recyclerViewDineIn.transaction_id?.substringBefore("-")?.substring(0, 10)
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
                setDate(position, recyclerViewDineIn)
                when (orderStatusDineIn) {
                    "DELIVER" -> {
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Selesai"
                        paymentStatus.text = "Dikirim"
                        acceptBtn.setOnClickListener {
                            val txnId = recyclerViewDineIn.transaction_id.toString()
                            updateTransactionTxn(txnId, "CLOSE", loadingOverlay)
                        }
                        formatNumber()
                        totalPrice.visibility = View.VISIBLE
                        totalPrice.text = "Rp " + str
                    }
                    else -> {
                        formatNumber()
                        totalPrice2.text = "Rp " + str
                        acceptBtn.visibility = View.GONE
                        paymentStatus.text = "Selesai"
                        totalPrice.visibility = View.GONE
                    }
                }
                orderStatus.visibility = View.GONE
                dividerTop.visibility = View.GONE
                rView.visibility = View.GONE
                rejectBtn.visibility = View.GONE
                paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                tableNumber.text = "Meja " + recyclerViewDineIn.table_no
                tableStatus.text = biz
                orderDate.text = "ID Transaksi: " + recyclerViewDineIn.transaction_id?.substringBefore("-")?.substring(0, 10)
                lastOrder.text =
                    recyclerViewDineIn.transaction_time?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + recyclerViewDineIn.transaction_time?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                menuCount.text = "Total " + jumlah + " Items:"
                jumlah = 0
                menuPrice = 0
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

            setMenu(rView, recyclerViewOmni.products)
            setDate(position, recyclerViewOmni)
            if (omniChannelName ==  "TOKOPEDIA") { // THIS CONDITION IS FOR TOKOPEDIA OMNICHANNEL (ALL STATUS WOULD BE APPEAR HERE)
                omniLogo.setImageResource(R.drawable.tokopedia)
                deliveryStatus.visibility = View.VISIBLE
                if (logisticChannel != null) {
                    deliveryMethod.text = logisticChannel.shipping_method
                    deliveryStatus.text = logisticChannel.shipping_service
                }
                orderDate.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                    .substringBefore(" ") + bulan + recyclerViewOmni.transaction_time.toString()
                    .substringAfter(" ").substringBeforeLast(":")
                formatNumber()
                when(orderStatusChannel) {
                    "PAYMENT_VERIFIED" -> {
                        paymentStatus.text = "NEW"
                        paymentStatus.setBackgroundResource(R.drawable.button_red_square)
                        totalPrice2.visibility = View.GONE
                        totalPrice.text = "Rp. $str"
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Siap Dikirim"
                        acceptBtn.setOnClickListener {
                            updateTransactionChannel(
                                recyclerViewOmni.channel.toString(),
                                recyclerViewOmni.order_id.toString(),
                                loadingOverlay
                            )
                        }
                        rejectBtn.visibility = View.VISIBLE
                        rejectBtn.setOnClickListener {
                            /* rejectDialog(position) */
                            openDialogTokopedia(position)
                        }
                        recyclerViewOmni.transaction_time?.let { timeAgo(it, lastOrder) }
                    }
                    "SELLER_ACCEPT_ORDER" -> {
                        paymentStatus.text = "Diproses"
                        paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                        totalPrice2.visibility = View.GONE
                        totalPrice.text = "Rp. $str"
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Upload Resi"
                        acceptBtn.setOnClickListener {
                            openDialogTokopedia(position)
                        }
                        rejectBtn.visibility = View.GONE
                        recyclerViewOmni.transaction_time?.let { timeAgo(it, lastOrder) }
                    }
                    "WAITING_FOR_PICKUP" -> {
                        paymentStatus.text = "Diproses"
                        paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                        totalPrice2.visibility = View.GONE
                        totalPrice.text = "Rp. $str"
                        acceptBtn.visibility = View.VISIBLE
                        acceptBtn.text = "Atur Pengiriman"
                        acceptBtn.setOnClickListener {
                            openDialogTokopedia(position)
                        }
                        rejectBtn.visibility = View.GONE
                        recyclerViewOmni.transaction_time?.let { timeAgo(it, lastOrder) }
                    }
                    else -> {
                        rView.visibility = View.GONE
                        acceptBtn.visibility = View.GONE
                        rejectBtn.visibility = View.GONE
                        totalPrice.visibility = View.GONE
                        orderDate.text = "ID Transaksi: " + recyclerViewOmni.order_id
                        lastOrder.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                            .substringBefore(" ") + bulan + recyclerViewOmni.transaction_time.toString()
                            .substringAfter(" ").substringBeforeLast(":")
                        if (orderStatusChannel == "SELLER_CANCEL_ORDER" || orderStatusChannel == "ORDER_REJECTED_BY_SELLER") {
                            paymentStatus.text = "Gagal"
                            paymentStatus.setBackgroundResource(R.drawable.button_red_square)
                        } else if (orderStatusChannel == "ORDER_DELIVERED" || orderStatusChannel == "ORDER_FINISHED") {
                            paymentStatus.text = "Selesai"
                            paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                        } else if (orderStatusChannel == "ORDER_SHIPMENT" || orderStatusChannel == "DELIVERED_TO_PICKUP_POINT") {
                            paymentStatus.text = "Dikirim"
                            paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                        } else if (orderStatusChannel == "BUYER_OPEN_A_CASE_TO_FINISH_AN_ORDER") {
                            paymentStatus.text = "Dikomplain"
                        }
                        totalPrice2.text = "Rp. $str"
                    }
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
                        .substringBefore(" ") + bulan + recyclerViewOmni.transaction_time.toString()
                        .substringAfter(" ").substringBeforeLast(":")
                    totalPrice2.visibility = View.GONE
                    totalPrice.text = "Rp. $str"
                    recyclerViewOmni.transaction_time?.let { timeAgo(it, lastOrder) }
                } else {
                    orderDate.visibility = View.INVISIBLE
                    lastOrder.text = recyclerViewOmni.transaction_time.toString().substringAfterLast("-")
                        .substringBefore(" ") + bulan + recyclerViewOmni.transaction_time.toString()
                        .substringAfter(" ").substringBeforeLast(":")
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
        fun bind(position: Int) {
            val recyclerViewDelivery = list[position]
            val orderStatusManual = recyclerViewDelivery.order_status

            setMenu(rView, recyclerViewDelivery.products)
            setDate(position, recyclerViewDelivery)
            setUpCard(recyclerViewDelivery, position)
            if (orderStatusManual == "ON_PROCESS" || orderStatusManual == "OPEN") {
                orderStatus.text = "Diproses"
                orderStatus.setBackgroundResource(R.drawable.button_orange_square)
                acceptBtn.visibility = View.VISIBLE
                acceptBtn.text = "Pesanan Siap"
                acceptBtn.setOnClickListener {
                    listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, "DELIVER", recyclerViewDelivery.payment_status))
                }
                rejectBtn.setBackgroundResource(R.drawable.button_red_transparent)
                rejectBtn.text = "Batalkan"
                rejectBtn.setTextColor(context.resources.getColor(R.color.colorRed))
                rejectBtn.setOnClickListener {
                    listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, "CANCELLED", recyclerViewDelivery.payment_status))
                }
            } else if (orderStatusManual == "DELIVER") {
                orderStatus.text = "Dikirim"
                orderStatus.setBackgroundResource(R.drawable.button_green_square)
                rejectBtn.visibility = View.GONE
                if (recyclerViewDelivery.payment_status == "PAID" && recyclerViewDelivery.biz_type == "DELIVERY") {
                    rejectBtn.visibility = View.VISIBLE //This is change for Lacak button (not rejectButton anymore)
                    rejectBtn.setBackgroundResource(R.drawable.button_purple_transparent)
                    rejectBtn.text = "Lacak"
                    rejectBtn.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
                    rejectBtn.setOnClickListener {
                        val intent = Intent(activity.baseContext, TransactionTrackingActivity::class.java)
                        if (recyclerViewDelivery.shipping?.awb.isNullOrEmpty()) {
                            Toast.makeText(context, "Menunggu mendapatkan kurir, mohon refresh page", Toast.LENGTH_SHORT).show()
                        } else {
                            intent.putExtra(TransactionTrackingActivity.WAYBILL_ID,
                                recyclerViewDelivery.shipping?.awb
                            )
                            intent.putExtra(TransactionTrackingActivity.COURIER_NAME,
                                recyclerViewDelivery.shipping?.shipping_method
                            )
                            activity.startActivity(intent)
                        }
                    }
                }
                acceptBtn.visibility = View.VISIBLE
                acceptBtn.text = "Pesanan Tiba"
                acceptBtn.setOnClickListener {
                    listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, "FINALIZE", recyclerViewDelivery.payment_status))
                }
            } else if (orderStatusManual == "FINALIZE") {
                orderStatus.text = "Sampai"
                orderStatus.setBackgroundResource(R.drawable.button_green_square)
                rejectBtn.visibility = View.GONE
                if (recyclerViewDelivery.payment_status == "PAID" && recyclerViewDelivery.biz_type == "DELIVERY") {
                    rejectBtn.visibility = View.VISIBLE //This is change for Lacak button (not rejectButton anymore)
                    rejectBtn.setBackgroundResource(R.drawable.button_purple_transparent)
                    rejectBtn.text = "Lacak"
                    rejectBtn.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
                    rejectBtn.setOnClickListener {
                        val intent = Intent(activity.baseContext, TransactionTrackingActivity::class.java)
                        if (recyclerViewDelivery.shipping?.awb.isNullOrEmpty()) {
                            Toast.makeText(context, "Menunggu mendapatkan kurir, mohon refresh page", Toast.LENGTH_SHORT).show()
                        } else {
                            intent.putExtra(TransactionTrackingActivity.WAYBILL_ID,
                                recyclerViewDelivery.shipping?.awb
                            )
                            intent.putExtra(TransactionTrackingActivity.COURIER_NAME,
                                recyclerViewDelivery.shipping?.shipping_method
                            )
                            activity.startActivity(intent)
                        }
                    }
                }
                acceptBtn.visibility = View.VISIBLE
                acceptBtn.text = "Pesanan Selesai"
                if (recyclerViewDelivery.payment_status == "PAID"){
                    acceptBtn.isEnabled = true
                    acceptBtn.setTextColor(context.resources.getColor(R.color.green))
                    acceptBtn.setOnClickListener {
                        listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, "CLOSE", recyclerViewDelivery.payment_status))
                    }
                    acceptBtn.setBackgroundResource(R.drawable.button_green_transparent)
                } else {
                    acceptBtn.isEnabled = false
                    acceptBtn.setTextColor(context.resources.getColor(R.color.borderSubtle))
                    acceptBtn.setBackgroundResource(R.drawable.button_gray_transparent)
                }
            } else if (orderStatusManual == "CLOSE") {
                orderStatus.text = "Selesai"
                orderStatus.setBackgroundResource(R.drawable.button_green_square)
                rejectBtn.visibility = View.GONE
                acceptBtn.visibility = View.GONE
            } else if (orderStatusManual == "CANCELLED") {
                orderStatus.text = "Batal"
                orderStatus.setBackgroundResource(R.drawable.button_red_square)
                rejectBtn.visibility = View.GONE
                acceptBtn.visibility = View.GONE
                when (recyclerViewDelivery.payment_status) {
                    "PAID" -> {
                        statusPayment.setTextColor(context.resources.getColor(R.color.green))
                        statusPayment.text = "Sudah Bayar"
                        updatePaymentBtn.visibility = View.VISIBLE
                        updatePaymentBtn.text = "Refund ke Pelanggan"
                        updatePaymentBtn.setOnClickListener {
                            listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, recyclerViewDelivery.order_status, "REFUND"))
                        }
                    }
                    "CANCELLED" -> {
                        statusPayment.text = "Dibatalkan"
                        statusPayment.setTextColor(context.resources.getColor(R.color.red))
                        updatePaymentBtn.visibility = View.GONE
                    }
                    "REFUND" -> {
                        statusPayment.text = "Dana Dikembalikan"
                        statusPayment.setTextColor(context.resources.getColor(R.color.orange))
                        updatePaymentBtn.visibility = View.GONE
                    }
                    else -> Timber.tag("INVALID_STATUS").d("Invalid status payment")
                }
            } else if (orderStatusManual == "FAILED") {
                orderStatus.text = "Gagal"
                orderStatus.setBackgroundResource(R.drawable.button_red_square)
                rejectBtn.visibility = View.GONE
                acceptBtn.visibility = View.GONE
            }
            callBtn.setOnClickListener {
                openWhatsapp(recyclerViewDelivery)
            }
        }

        private fun setUpCard(recyclerViewDelivery: TransactionListV2Data, position: Int) {
            setUpLogo(recyclerViewDelivery)
            transactionId.text = "ID Transaksi: " + recyclerViewDelivery.transaction_id.toString().substring(0, 10)
            orderDate.text = recyclerViewDelivery.transaction_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                    recyclerViewDelivery.transaction_time.toString().substringBefore("-") +", "+ recyclerViewDelivery.transaction_time.toString().substringAfter(" ").substringBeforeLast(":")
            itemCount.text = "Total $jumlah items:"
            jumlah = 0
            formatNumber()
            totalPrice.text = "Rp. " + str
            menuPrice = 0
            shippingMethod.text = recyclerViewDelivery.shipping?.shipping_method?.replaceFirstChar { it.titlecase() }
            shippingDate.text = recyclerViewDelivery.shipping?.shipping_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                    recyclerViewDelivery.shipping?.shipping_time.toString().substringBefore("-") +", "+ recyclerViewDelivery.shipping?.shipping_time.toString().substringAfter(" ").substringBeforeLast(":")
            custName.text = recyclerViewDelivery.customer?.name
            custAddress.text = recyclerViewDelivery.customer?.address
            custPhone.text = "(" + recyclerViewDelivery.customer?.phone_number + ")"

            when(recyclerViewDelivery.payment_status) {
                "PAID" -> {
                    statusPayment.text = "Sudah Bayar"
                    statusPayment.setTextColor(context.resources.getColor(R.color.green))
                    updatePaymentBtn.visibility = View.GONE
                }
                "UNPAID" -> {
                    statusPayment.text = "Belum Bayar"
                    statusPayment.setTextColor(context.resources.getColor(R.color.red))
                    updatePaymentBtn.visibility = View.VISIBLE
                    updatePaymentBtn.setOnClickListener {
                        var tabStatus = ""
                        tabStatus = when(recyclerViewDelivery.order_status) {
                            "ON_PROCESS" -> "ON_PROCESS"
                            else -> "CLOSE"
                        }
                        openCancelDialog(recyclerViewDelivery, recyclerViewDelivery.customer?.name.toString(), recyclerViewDelivery.order_platform.toString(),
                            recyclerViewDelivery.transaction_time.toString().substringBefore(" ").substringAfterLast("-") + bulan +
                                    recyclerViewDelivery.transaction_time.toString().substringBefore("-") +", "+ recyclerViewDelivery.transaction_time.toString().substringAfter(" ").substringBeforeLast(":"),
                            recyclerViewDelivery.shipping?.shipping_method.toString(),
                            recyclerViewDelivery.transaction_id.toString(), recyclerViewDelivery.order_status.toString(), tabStatus, position)
                    }
                }
                "FAILED" -> {
                    statusPayment.text = "Gagal"
                    statusPayment.setTextColor(context.resources.getColor(R.color.red))
                    updatePaymentBtn.visibility = View.GONE
                }
            }
        }

        private fun setUpLogo(recyclerViewDelivery: TransactionListV2Data){
            if (recyclerViewDelivery.order_platform == "Instagram" ||
                recyclerViewDelivery.order_platform == "INSTAGRAM"
            ){
                logo.setImageResource(R.drawable.logo_ig)
            } else if (
                recyclerViewDelivery.order_platform == "Whatsapp" ||
                recyclerViewDelivery.order_platform == "WHATSAPP"
            ){
                logo.setImageResource(R.drawable.logo_wa)
            } else if (
                recyclerViewDelivery.order_platform == "Telepon" ||
                recyclerViewDelivery.order_platform == "PHONE_CALL"
            ){
                logo.setImageResource(R.drawable.logo_phone)
            } else if (recyclerViewDelivery.order_platform == "PIKAPP"){
                logo.setImageResource(R.drawable.logo_pikapp_square)
            } else {
                Timber.tag("INVALID_LOGO").d("Invalid Order Platform")
            }
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
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType
    }

    /* OTHER FUNCTION */
    private fun setMenu(
        recyclerView: RecyclerView,
        productList: List<ProductDetailV2Response>?
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList = TransactionProductListV2Adapter(productList)
        recyclerView.adapter = menuList
    }

    private fun setDate(position: Int, recyclerView: TransactionListV2Data) {
        val transactionPositionList = list[position]
        bulanTemp = transactionPositionList.transaction_time?.substringAfter("-")?.substringBeforeLast("-").toString()
        bulan = when (bulanTemp) {
            "01" -> " Jan "
            "02" -> " Feb "
            "03" -> " Mar "
            "04" -> " Apr "
            "05" -> " Mei "
            "06" -> " Jun "
            "07" -> " Jul "
            "08" -> " Ags "
            "09" -> " Sep "
            "10" -> " Okt "
            "11" -> " Nov "
            else -> " Des "
        }

        biz = when(transactionPositionList.biz_type) {
            "TAKE_AWAY" -> "Bungkus/Takeaway"
            "DINE_IN" -> "Makan di Tempat"
            else -> ""
        }

        for (count in transactionPositionList.products!!) {
            jumlah += count.quantity!!.toInt()
        }
        this.menuPrice = transactionPositionList.total_payment!!.toInt()
    }

    private fun timeAgo(time: String, holder: TextView) {
        val txnTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
        var timeNow = Date()
        var seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timeNow.time - txnTime.time)
        var minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timeNow.time - txnTime.time)
        var hours: Long = TimeUnit.MILLISECONDS.toHours(timeNow.time - txnTime.time)
        var days: Long = TimeUnit.MILLISECONDS.toDays(timeNow.time - txnTime.time)

        if (seconds < 60) {
            holder.text = "Baru Saja"
        } else if (minutes < 60) {
            holder.text = minutes.toString() + " Menit Yang Lalu"
        } else if (hours > 24) {
            holder.text = days.toString() + " Hari Yang Lalu"
        } else {
            holder.text = hours.toString() + " Jam Yang Lalu"
        }
    }

    private fun formatNumber() {
        str = NumberFormat.getNumberInstance(Locale("in", "ID")).format(this.menuPrice)
    }

    private fun openWhatsapp(recyclerViewDelivery: TransactionListV2Data){
        var temp = ""
        var number = ""
        temp = recyclerViewDelivery.customer?.phone_number.toString().substringAfter("0")
        number = "+62 $temp"

        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        activity.startActivity(i)
    }

    /* TRANSACTION DIALOG */
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

    private fun openCancelDialog(recyclerViewDelivery: TransactionListV2Data, nama: String, platform: String, tanggal: String, ekspedisi: String, id: String, txnStatus: String, tabStatus: String, position: Int) {
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
            listener.onItemClickTransactionPos(UpdateStatusManualTxnRequest(recyclerViewDelivery.transaction_id, txnStatus, "PAID"))
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

    /* UPDATE TRANSACTION FUNCTION */
    private fun updateTransactionTxn(
        txnId: String,
        status: String,
        loadingOverlay: View
    ) {
        listener.onItemClickTransactionTxn(txnId, status)
    }

    private fun updateTransactionChannel(
        channel: String,
        orderId: String,
        loadingOverlay: View
    ) {
        listener.onItemClickTransactionChannel(channel, orderId)
    }

    interface OnItemClickListener {
        fun onItemClickTransactionTxn(txnId: String, status: String)
        fun onItemClickTransactionChannel(channel: String, orderId: String)
        fun onItemClickTransactionPos(updateStatusManualTxnRequest: UpdateStatusManualTxnRequest)
    }
}