package com.tsab.pikapp.view.homev2.Transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.view.homev2.menu.MenuListAdapter
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.transaction_list_items.view.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class TransactionListAdapter(private val context: Context, private val transactionList: MutableList<StoreOrderList>, private val transactionList1: MutableList<List<OrderDetailDetail>>, private val sessionManager: SessionManager, private val supportFragmentManager: FragmentManager) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    lateinit var viewModel: TransactionViewModel
    lateinit var linearLayoutManager: LinearLayoutManager
    private val disposable = CompositeDisposable()
    private val pikappService = PikappApiService()
    var menuResult = ArrayList<OrderDetailDetail>()
    var jumlah = 0
    val reasonsheet = CancelReasonFragment()
    var bulan: String = " Jun "
    var bulanTemp: String = ""
    var biz: String = ""

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setMenu(holder.rView, transactionList1[position] as MutableList<OrderDetailDetail>)
        val context: Context = holder.price.context
        sessionManager.setHomeNav(0)

        //on_procces
        if (transactionList[position].status == "OPEN" || transactionList[position].status == "PAID" || transactionList[position].status == "ON_PROCESS"){
            if (transactionList[position].status == "PAID"){
                setDate(position)
                holder.orderTime.visibility = View.GONE
                holder.orderStatus.visibility = View.GONE
                holder.paymentStatus.text = "NEW"
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                holder.divider.visibility = View.GONE
                holder.acceptBtn.text = "Terima"
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    postUpdate(txnId, "ON_PROCESS")
                    context.startActivity(Intent(context, HomeNavigation::class.java))
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("TransactionID", transactionList[position].transactionID)
                    reasonsheet.arguments = bundle
                    reasonsheet.show(supportFragmentManager, "show")
                    Log.e("paid", "bisa bos")

                }
            } else if (transactionList[position].status == "OPEN"){
                setDate(position)
                holder.orderTime.visibility = View.GONE
                holder.orderStatus.text = "NEW"
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                holder.divider.visibility = View.GONE
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    postUpdate(txnId, "PAID")
                    context.startActivity(Intent(context, HomeNavigation::class.java))
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("TransactionID", transactionList[position].transactionID)
                    reasonsheet.arguments = bundle
                    reasonsheet.show(supportFragmentManager, "show")
                    Log.e("paid", "bisa bos")

                }
            } else if (transactionList[position].status == "ON_PROCESS"){
                setDate(position)
                holder.orderTime.visibility = View.GONE
                holder.orderStatus.visibility = View.GONE
                timeAgo(transactionList[position].transactionTime.toString(), holder.lastOrder)
                holder.paymentStatus.text = "Diproses"
                holder.paymentStatus.backgroundTintList = context.resources.getColorStateList(R.color.orange)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
                holder.divider.visibility = View.GONE
                holder.acceptBtn.text = "Pesanan Siap"
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    postUpdate(txnId, "DELIVER")
                    context.startActivity(Intent(context, HomeNavigation::class.java))
                    Log.e("paid", "bisa bos")
                }
                holder.rejectBtn.visibility = View.GONE
            }
        } else if (transactionList[position].status == "FAILED" || transactionList[position].status == "ERROR"){
            setDate(position)
            holder.orderTime.visibility = View.GONE
            holder.orderStatus.visibility = View.GONE
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.paymentStatus.text = "Gagal"
            holder.tableNumer.text = "Meja " + transactionList[position].tableNo
            holder.tableStatus.text = biz
            holder.orderDate.textSize = 10F
            holder.orderDate.text = "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
            holder.lastOrder.textSize = 11F
            holder.lastOrder.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
            holder.price.text = ""
            holder.menuCount.text = "Total " + jumlah + " Items"
            jumlah = 0
        } else if (transactionList[position].status == "DELIVER" || transactionList[position].status == "CLOSE" || transactionList[position].status == "FINALIZE"){
            if (transactionList[position].status == "DELIVER"){
                setDate(position)
                holder.orderTime.visibility = View.GONE
                holder.orderStatus.visibility = View.GONE
                holder.rView.visibility = View.GONE
                holder.rejectBtn.visibility = View.GONE
                holder.acceptBtn.text = "Selesai"
                holder.paymentStatus.text = "Dikirim"
                holder.paymentStatus.backgroundTintList = context.resources.getColorStateList(R.color.green)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.textSize = 10F
                holder.orderDate.text = "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
                holder.lastOrder.textSize = 11F
                holder.lastOrder.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                holder.acceptBtn.setOnClickListener {
                    val txnId = transactionList[position].transactionID.toString()
                    postUpdate(txnId, "CLOSE")
                    context.startActivity(Intent(context, HomeNavigation::class.java))
                    Log.e("paid", "bisa bos")
                }
                jumlah = 0
            } else if (transactionList[position].status == "CLOSE" || transactionList[position].status == "FINALIZE"){
                setDate(position)
                holder.orderTime.visibility = View.GONE
                holder.orderStatus.visibility = View.GONE
                holder.rView.visibility = View.GONE
                holder.acceptBtn.visibility = View.GONE
                holder.rejectBtn.visibility = View.GONE
                holder.paymentStatus.text = "Selesai"
                holder.paymentStatus.backgroundTintList = context.resources.getColorStateList(R.color.green)
                holder.tableNumer.text = "Meja " + transactionList[position].tableNo
                holder.tableStatus.text = biz
                holder.orderDate.textSize = 10F
                holder.orderDate.text = "ID Transaksi: " + transactionList[position].transactionID?.substringBefore("-")
                holder.lastOrder.textSize = 11F
                holder.lastOrder.text = transactionList[position].transactionTime?.substringAfterLast("-")?.substringBefore(" ") + bulan + transactionList[position].transactionTime?.substringAfter(" ")?.substringBeforeLast(":")
                holder.price.text = ""
                holder.menuCount.text = "Total " + jumlah + " Items"
                jumlah = 0
            }
        }
    }

    override fun getItemCount(): Int {
        //ganti yang ini sama di parameter
        return transactionList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tableNumer: TextView = itemView.tableNumber
        var tableStatus: TextView = itemView.tableStatus
        var orderDate: TextView = itemView.orderDate
        var orderTime: TextView = itemView.orderTime
        var orderStatus: TextView = itemView.orderStatus
        var paymentStatus: TextView = itemView.paymentStatus
        var menuCount: TextView = itemView.menuCount
        var price: TextView = itemView.totalPrice
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu
        var lastOrder: TextView = itemView.lastOrder
        var divider: View = itemView.divider

    }

    private fun setMenu(recyclerView: RecyclerView, transactionList1: MutableList<OrderDetailDetail>){
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = TransactionMenuAdapter(context, transactionList1)
        recyclerView.adapter = menuList1
    }

    private fun setDate(position: Int){
        bulanTemp = transactionList[position].transactionTime?.substringAfter("-")?.substringBeforeLast("-").toString()
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

        if(transactionList[position].bizType == "TAKE_AWAY"){
            biz = "Bungkus/Takeaway"
        }else if(transactionList[position].bizType == "DINE_IN"){
            biz = "Makan di Tempat"
        }

        for(count in transactionList1[position]){
            jumlah = jumlah + count.productQty!!.toInt()
        }
    }

    fun timeAgo(time: String, holder:TextView){
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
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

    fun postUpdate(id: String, status: String){
        val email = sessionManager.getUserData()!!.email!!
        val token = sessionManager.getUserToken()!!

        disposable.add(
                pikappService.postUpdateOrderStatus(email, token,
                        RequestBody.create(MediaType.parse("multipart/form-data"), id)
                        , RequestBody.create(MediaType.parse("multipart/form-data"), status))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<UpdateStatusResponse>(){
                            override fun onSuccess(t: UpdateStatusResponse) {
                                Log.e("Berhasil", "Sukses")
                            }

                            override fun onError(e: Throwable) {
                                Log.e("Gagal", "Fail")
                            }
                        })
        )
    }

}