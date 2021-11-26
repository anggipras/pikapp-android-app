package com.tsab.pikapp.view.homev2.transaction

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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TransactionListAdapter(
    private val context: Context,
    private var transactionList: MutableList<StoreOrderList>,
    private val transactionList1: MutableList<List<OrderDetailDetail>>,
    private val sessionManager: SessionManager,
    private val supportFragmentManager: FragmentManager,
    private val prefHelper: SharedPreferencesUtil,
    private val recyclerView: RecyclerView,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    lateinit var linearLayoutManager: LinearLayoutManager
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()
    lateinit var categoryAdapter: TransactionListAdapter
    var orderResult = ArrayList<StoreOrderList>()
    var jumlah = 0
    var price = 0
    var str: String = ""
    val reasonsheet = CancelReasonFragment()
    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var biz: String = ""
    var isLoading: Boolean = false
    var prosesList = ArrayList<StoreOrderList>()
    var batalList = ArrayList<StoreOrderList>()
    var doneList = ArrayList<StoreOrderList>()
    var menuList = ArrayList<ArrayList<OrderDetailDetail>>()
    var menuList1 = ArrayList<ArrayList<OrderDetailDetail>>()
    var menuList2 = ArrayList<ArrayList<OrderDetailDetail>>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setMenu(holder.rView, transactionList1[position] as MutableList<OrderDetailDetail>)
        val context: Context = holder.price.context
        sessionManager.setHomeNav(0)

        //on_procces
        if (transactionList[position].status == "OPEN" || transactionList[position].status == "PAID" || transactionList[position].status == "ON_PROCESS") {
            if (transactionList[position].status == "PAID") {
                setDate(position)
                holder.orderStatus.visibility = View.GONE
                holder.paymentStatus.text = "NEW"
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text =
                    transactionList[position].transactionTime?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                holder.price.text = "Rp " + str
                price = 0
                holder.acceptBtn.text = "Terima"
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    updateTransaction(txnId, "ON_PROCESS", "Proses", holder)
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("TransactionID", transactionList[position].transactionID)
                    reasonsheet.arguments = bundle
                    reasonsheet.show(supportFragmentManager, "show")
                    Log.e("paid", "bisa bos")
                }
            } else if (transactionList[position].status == "OPEN") {
                setDate(position)
                holder.orderStatus.text = "NEW"
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text =
                    transactionList[position].transactionTime?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                holder.price.text = "Rp " + str
                price = 0
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    updateTransaction(txnId, "ON_PROCESS", "Proses", holder)
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("TransactionID", transactionList[position].transactionID)
                    reasonsheet.arguments = bundle
                    reasonsheet.show(supportFragmentManager, "show")
                    Log.e("paid", "bisa bos")

                }
            } else if (transactionList[position].status == "ON_PROCESS") {
                setDate(position)
                holder.orderStatus.visibility = View.GONE
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.paymentStatus.text = "Diproses"
                holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text =
                    transactionList[position].transactionTime?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                holder.price.text = "Rp " + str
                price = 0
                holder.acceptBtn.text = "Pesanan Siap"
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    updateTransaction(txnId, "DELIVER", "Proses", holder)
//                    listener.onItemClick(1)
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.visibility = View.GONE
            }
        } else if (transactionList[position].status == "FAILED" || transactionList[position].status == "ERROR") {
            setDate(position)
            holder.orderStatus.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.paymentStatus.text = "Gagal"
            holder.tableNumer.text = "Meja " + transactionList[position].tableNo
            holder.tableStatus.text = biz
            holder.orderDate.textSize = 10F
            holder.orderDate.text =
                "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
            holder.lastOrder.textSize = 11F
            holder.lastOrder.text =
                transactionList[position].transactionTime?.substringAfterLast("-")
                    ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                    " "
                )?.substringBeforeLast(":")
            holder.menuCount.text = "Total " + jumlah + " Items"
            jumlah = 0
            formatNumber()
            holder.price2.text = "Rp " + str
            price = 0
            holder.price.visibility = View.GONE
        } else if (transactionList[position].status == "DELIVER" || transactionList[position].status == "CLOSE" || transactionList[position].status == "FINALIZE") {
            if (transactionList[position].status == "DELIVER") {
                setDate(position)
                holder.orderStatus.visibility = View.GONE
                holder.price.visibility = View.GONE
                holder.rView.visibility = View.GONE
                holder.rejectBtn.visibility = View.GONE
                holder.acceptBtn.text = "Selesai"
                holder.paymentStatus.text = "Dikirim"
                holder.paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.textSize = 10F
                holder.orderDate.text =
                    "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
                holder.lastOrder.textSize = 11F
                holder.lastOrder.text =
                    transactionList[position].transactionTime?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                holder.menuCount.text = "Total " + jumlah + " Items"
                formatNumber()
                holder.price2.text = "Rp " + str
                price = 0
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    updateTransaction(txnId, "CLOSE", "Done", holder)
                    Log.e("paid", "bisa bos")
                }
                jumlah = 0
            } else if (transactionList[position].status == "CLOSE" || transactionList[position].status == "FINALIZE") {
                setDate(position)
                holder.orderStatus.visibility = View.GONE
                holder.price.visibility = View.GONE
                holder.rView.visibility = View.GONE
                holder.acceptBtn.visibility = View.GONE
                holder.rejectBtn.visibility = View.GONE
                holder.paymentStatus.text = "Selesai"
                holder.paymentStatus.setBackgroundResource(R.drawable.button_green_square)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.textSize = 10F
                holder.orderDate.text =
                    "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
                holder.lastOrder.textSize = 11F
                holder.lastOrder.text =
                    transactionList[position].transactionTime?.substringAfterLast("-")
                        ?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(
                        " "
                    )?.substringBeforeLast(":")
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                formatNumber()
                holder.price2.text = "Rp " + str
                price = 0
            }
        }
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tableNumer: TextView = itemView.tableNumber
        var tableStatus: TextView = itemView.tableStatus
        var orderDate: TextView = itemView.orderDate
        var orderStatus: TextView = itemView.orderStatus
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var price: TextView = itemView.totalPrice
        var price2: TextView = itemView.totalPrice2
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu
        var lastOrder: TextView = itemView.lastOrder
        var loadingOverlay: View = itemView.loadingOverlay
    }

    private fun updateTransaction(
        txnId: String,
        status: String,
        orderStatus: String,
        holder: ViewHolder
    ) {
        holder.loadingOverlay.visibility = View.VISIBLE
        setIsLoading(true)
        postUpdate(txnId, status)
        Handler().postDelayed({
            getStoreOrderList(status, orderStatus, holder)
            notifyDataSetChanged()
        }, 2000)
    }

    private fun setMenu(
        recyclerView: RecyclerView,
        transactionList1: MutableList<OrderDetailDetail>
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = TransactionMenuAdapter(context, transactionList1)
        recyclerView.adapter = menuList1
    }

    private fun setDate(position: Int) {
        bulanTemp =
            transactionList[position].transactionTime?.substringAfter("-")?.substringBeforeLast("-")
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

        if (transactionList[position].bizType == "TAKE_AWAY") {
            biz = "Bungkus/Takeaway"
        } else if (transactionList[position].bizType == "DINE_IN") {
            biz = "Makan di Tempat"
        }

        for (count in transactionList1[position]) {
            jumlah = jumlah + count.productQty!!.toInt()
            if (count.productQty > 1) {
                price += count.productQty * count.productPrice!!.toInt()
            } else {
                price += count.productPrice!!.toInt()
            }
        }
    }

    private fun formatNumber() {
        str = NumberFormat.getNumberInstance(Locale.US).format(price)
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

    fun postUpdate(id: String, status: String) {
        setIsLoading(true)
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
            pikappService.postUpdateOrderStatus(
                email,
                token,
                RequestBody.create(MediaType.parse("multipart/form-data"), id),
                RequestBody.create(MediaType.parse("multipart/form-data"), status)
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>() {
                    override fun onSuccess(t: UpdateStatusResponse) {
                        Log.e("Berhasil", "Sukses")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("Gagal", "Fail")
                    }
                })
        )
    }

    fun getStoreOrderList(deliveryStatus: String, status: String, holder: ViewHolder) {
        setIsLoading(true)
        prefHelper.clearStoreOrderList()
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!
        val mid = sessionManager.getUserData()!!.mid!!

        disposable.add(
            pikappService.getTransactionListMerchant(email, token, mid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GetStoreOrderListResponse>() {
                    override fun onSuccess(t: GetStoreOrderListResponse) {
                        val result = t.results
                        orderResult.addAll(result as MutableList<StoreOrderList>)
                        transactionList.addAll(orderResult)
                        sortOrderList(result)
                        setProcessOrder(
                            context,
                            recyclerView,
                            status,
                            supportFragmentManager,
                            listener
                        )
                        setIsLoading(false)
                        holder.loadingOverlay.visibility = View.GONE
                        if (deliveryStatus == "DELIVER") {
                            listener.onItemClick(1)
                        }
                    }

                    override fun onError(e: Throwable) {
                        var errorResponse: ErrorResponse
                        Toast.makeText(context, "failed: " + e.message, Toast.LENGTH_SHORT).show()
                    }
                })
        )
    }

    fun sortOrderList(transactionList: List<StoreOrderList>?) {
        if (transactionList != null) {
            for (transaction in transactionList) {
                if (transaction.status == "OPEN" || transaction.status == "PAID" || transaction.status == "ON_PROCESS") {
                    prosesList.add(transaction)
                    transaction.detailProduct?.let { menuList.add(it as ArrayList<OrderDetailDetail>) }
                } else if (transaction.status == "FAILED" || transaction.status == "ERROR") {
                    batalList.add(transaction)
                    transaction.detailProduct?.let { menuList1.add(it as ArrayList<OrderDetailDetail>) }
                } else {
                    doneList.add(transaction)
                    transaction.detailProduct?.let { menuList2.add(it as ArrayList<OrderDetailDetail>) }
                }
            }
        }
    }

    fun setProcessOrder(
        baseContext: Context,
        recyclerView: RecyclerView,
        status: String,
        support: FragmentManager,
        listener: OnItemClickListener
    ) {
        if (status == "Proses") {
            transactionList.clear()
            categoryAdapter = TransactionListAdapter(
                baseContext,
                prosesList as MutableList<StoreOrderList>,
                menuList as MutableList<List<OrderDetailDetail>>,
                sessionManager,
                support,
                prefHelper,
                recyclerView,
                listener
            )
            notifyDataSetChanged()
            recyclerView.adapter = categoryAdapter
            notifyDataSetChanged()
        }
        if (status == "Batal") {
            transactionList.clear()
            categoryAdapter = TransactionListAdapter(
                baseContext,
                batalList as MutableList<StoreOrderList>,
                menuList1 as MutableList<List<OrderDetailDetail>>,
                sessionManager,
                support,
                prefHelper,
                recyclerView,
                listener
            )
            categoryAdapter.notifyDataSetChanged()
            recyclerView.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }
        if (status == "Done") {
            transactionList.clear()
            categoryAdapter = TransactionListAdapter(
                baseContext,
                doneList as MutableList<StoreOrderList>,
                menuList2 as MutableList<List<OrderDetailDetail>>,
                sessionManager,
                support,
                prefHelper,
                recyclerView,
                listener
            )
            categoryAdapter.notifyDataSetChanged()
            recyclerView.adapter = categoryAdapter
            categoryAdapter.notifyDataSetChanged()
        }
    }

    fun setIsLoading(value: Boolean) {
        isLoading = value
    }

    interface OnItemClickListener {
        fun onItemClick(i: Int)
    }
}