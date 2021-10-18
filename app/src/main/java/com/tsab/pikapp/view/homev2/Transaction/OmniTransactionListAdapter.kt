package com.tsab.pikapp.view.homev2.Transaction

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.*
import com.tsab.pikapp.view.homev2.Transaction.shipment.ResiTokopediaDialogFragment
import com.tsab.pikapp.view.other.otherSettings.profileSetting.ProfileBirthdayFragment
import com.tsab.pikapp.view.other.otherSettings.profileSetting.setFragmentResultListener
import kotlinx.android.synthetic.main.omni_tokped_popup.view.*
import kotlinx.android.synthetic.main.tokped_reason2_popup.view.*
import kotlinx.android.synthetic.main.tokped_reason_popup.*
import kotlinx.android.synthetic.main.tokped_reason_popup.view.*
import kotlinx.android.synthetic.main.tokped_reason_popup.view.dialog_reason_close
import kotlinx.android.synthetic.main.tokped_reason_popup.view.dialog_reason_text
import kotlinx.android.synthetic.main.tokped_reject_popup.view.*
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_back
import kotlinx.android.synthetic.main.tokped_reject_popup.view.dialog_tokped_ok
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

class OmniTransactionListAdapter(
    private val context: Context,
    private var omniList: MutableList<OrderDetailOmni>,
    private val omniDetailList: MutableList<List<ProductDetailOmni>>,
    private val sessionManager: SessionManager,
    private val supportFragmentManager: FragmentManager,
    private val prefHelper: SharedPreferencesUtil,
    private val recyclerView: RecyclerView,
    private val activity: Activity,
    private val logisticList: MutableList<LogisticsDetailOmni>,
    private val empty: ConstraintLayout,
    private val lifecycle: Fragment
) : RecyclerView.Adapter<OmniTransactionListAdapter.ViewHolder>() {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var omniAdapter: OmniTransactionListAdapter
    var orderResult = ArrayList<OrderDetailOmni>()
    var jumlah = 0
    var price = 0
    var str: String = ""
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list_items_omni, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        setMenu(holder.rView, omniDetailList[position] as MutableList<ProductDetailOmni>)
        sessionManager.setHomeNav(0)
        if (omniList[position].channel == "TOKOPEDIA") {
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
        var price2: TextView = itemView.totalPriceOmni
        var acceptBtn: Button = itemView.acceptButton
        var rejectBtn: Button = itemView.rejectButton
        var rView: RecyclerView = itemView.recyclerview_menu_omni
        var lastOrder: TextView = itemView.lastOrder
        var loadingOverlay: View = itemView.loadingOverlay
        var deliveryStatus: TextView = itemView.deliveryStatus
        var deliveryMethod: TextView = itemView.deliveryMethod
    }

    private fun setTokopediaCardView(holder: ViewHolder, position: Int) {
        if (omniList[position].status == "PAYMENT_VERIFIED") {
            setDate(position)
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
            formatNumber()
            holder.price2.visibility = View.GONE
            holder.price.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
            holder.acceptBtn.text = "Siap Dikirim"
            holder.acceptBtn.setOnClickListener {
                updateTransaction(
                    omniList[position].channel.toString(),
                    omniList[position].orderId.toString(),
                    "Proses",
                    holder
                )
            }
            holder.rejectBtn.setOnClickListener {
                Log.e("msg", "clicked")
                /*rejectDialog(position)*/
                openDialogTokopedia(position)
            }
            timeAgo(omniList[position].orderTime.toString(), holder.lastOrder)
        } else if (omniList[position].status == "SELLER_ACCEPT_ORDER") {
            setDate(position)
            holder.paymentStatus.text = "Diproses"
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
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
        } else if (omniList[position].status == "WAITING_FOR_PICKUP") {
            setDate(position)
            holder.paymentStatus.text = "Diproses"
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.setBackgroundResource(R.drawable.button_orange_square)
            holder.orderDate.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
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
        } else if (omniList[position].status == "SELLER_CANCEL_ORDER" || omniList[position].status == "ORDER_REJECTED_BY_SELLER") {
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Gagal"
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "ORDER_DELIVERED" || omniList[position].status == "ORDER_FINISHED") {
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
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "ORDER_SHIPMENT" || omniList[position].status == "DELIVERED_TO_PICKUP_POINT") {
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
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        } else if (omniList[position].status == "BUYER_OPEN_A_CASE_TO_FINISH_AN_ORDER") {
            setDate(position)
            holder.rView.visibility = View.GONE
            holder.acceptBtn.visibility = View.GONE
            holder.rejectBtn.visibility = View.GONE
            holder.price.visibility = View.GONE
            holder.deliveryMethod.text = logisticList[position].shippingAgency
            holder.deliveryStatus.text = logisticList[position].serciveType
            holder.paymentStatus.text = "Dikomplain"
            holder.orderDate.text = "ID Transaksi: " + omniList[position].orderId
            holder.lastOrder.text = omniList[position].orderTime.toString().substringAfterLast("-")
                .substringBefore("T") + bulan + omniList[position].orderTime.toString()
                .substringAfter("T").substringBeforeLast(":")
            formatNumber()
            holder.price2.text = "Rp. $str"
            price = 0
            holder.menuCount.text = "Total $jumlah items:"
            jumlah = 0
        }
    }

    private fun rejectDialog(position: Int) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.tokped_reject_popup, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )

        mDialogView.dialog_tokped_ok.setOnClickListener {
            reasonDialog(position)
            mAlertDialog.dismiss()
        }

        mDialogView.dialog_tokped_back.setOnClickListener {
            mAlertDialog.dismiss()
        }
        mDialogView.dialog_tokped_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun reasonDialog(position: Int) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.tokped_reason_popup, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val mAlertDialog = mBuilder.show()
        var reason: String
        val items = activity.resources.getStringArray(R.array.spinnerReason)
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )

        val spinnerAdapter =
            object : ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, items) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.parseColor("#767676"))
                    } else {
                        view.setTextColor(Color.parseColor("#000000"))
                    }
                    return view
                }
            }

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mDialogView.spinnerReason.adapter = spinnerAdapter

        mDialogView.spinnerReason.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val value = parent!!.getItemAtPosition(position).toString()
                    reason = parent.getItemAtPosition(position).toString()
                    if (value == items[0]) {
                        (view as TextView).setTextColor(Color.parseColor("#767676"))
                        mAlertDialog.btnNext.setBackgroundResource(R.drawable.button_dark_gray)
                        mAlertDialog.btnNext.setTextColor(Color.parseColor("#ffffff"))
                        mAlertDialog.btnNext.isEnabled = false
                        mAlertDialog.btnNext.isClickable = false
                    } else {
                        mAlertDialog.btnNext.setBackgroundResource(R.drawable.button_green_square)
                        mAlertDialog.btnNext.setTextColor(Color.parseColor("#ffffff"))
                        mAlertDialog.btnNext.isEnabled = true
                        mAlertDialog.btnNext.isClickable = true
                    }
                }
            }

        mDialogView.btnNext.setOnClickListener {
            Log.e("Result", mDialogView.spinnerReason.selectedItem.toString())
            rejectReason(position, mDialogView.spinnerReason.selectedItem.toString())
            mAlertDialog.dismiss()
        }

        mDialogView.dialog_reason_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun openDialogTokopedia(position: Int) {
        if (omniList[position].status == "PAYMENT_VERIFIED" || omniList[position].status == "PAYMENT_CONFIRMATION") {
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
        } else if (omniList[position].status == "SELLER_ACCEPT_ORDER") {
            ResiTokopediaDialogFragment().show(
                supportFragmentManager,
                ResiTokopediaDialogFragment.tag
            )
        } else if (omniList[position].status == "WAITING_FOR_PICKUP") {
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

    private fun rejectReason(position: Int, Reason: String) {
        val mDialogView = LayoutInflater.from(activity).inflate(R.layout.tokped_reason2_popup, null)
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        val items = activity.resources.getStringArray(R.array.kurirReason)
        val mAlertDialog = mBuilder.show()
        mAlertDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                activity,
                R.drawable.dialog_background
            )
        )

        if (Reason == "Stok Produk Kosong") {
            mDialogView.stokKosong.visibility = View.VISIBLE
            mDialogView.dialog_reason_text.text = Reason
        } else if (Reason == "Pembeli Tidak Merespons") {
            mDialogView.stokKosong.visibility = View.VISIBLE
            mDialogView.dialog_reason_text.text = Reason
            mDialogView.textExplain.text = "Jelaskan kendala yang kamu alami"
            mDialogView.editReason.hint = "Contoh: Pembeli tidak jawab saat ditanya varian apa..."
        } else if (Reason == "Lainnya") {
            mDialogView.stokKosong.visibility = View.VISIBLE
            mDialogView.dialog_reason_text.text = "Alasan Penolakan Lainnya"
            mDialogView.textExplain.text = "Jelaskan alasan kamu menolak pesanan ini"
            mDialogView.editReason.hint = "Contoh: Saya tidak menerima pesanan ini karena..."
        } else if (Reason == "Kendala Kurir") {
            mDialogView.kendalaKurir.visibility = View.VISIBLE
            val spinnerAdapter = object :
                ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, items) {
                override fun isEnabled(position: Int): Boolean {
                    return position != 0
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView =
                        super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        view.setTextColor(Color.parseColor("#767676"))
                    } else {
                        view.setTextColor(Color.parseColor("#000000"))
                    }
                    return view
                }
            }

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mDialogView.spinnerKurir.adapter = spinnerAdapter

            mDialogView.spinnerKurir.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val value = parent!!.getItemAtPosition(position).toString()
                        if (value == items[0]) {
                            (view as TextView).setTextColor(Color.parseColor("#767676"))
                            mDialogView.textOther.visibility = View.GONE
                            mDialogView.editReasonOther.visibility = View.GONE
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                            mDialogView.dialog_tokped_back.isEnabled = false
                            mDialogView.dialog_tokped_back.isClickable = false
                        } else if (value == items[1] || value == items[2] || value == items[3]) {
                            mDialogView.textOther.visibility = View.GONE
                            mDialogView.editReasonOther.visibility = View.GONE
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_green_square)
                            mDialogView.dialog_tokped_back.isEnabled = true
                            mDialogView.dialog_tokped_back.isClickable = true
                        } else if (value == items[4]) {
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                            mDialogView.textOther.visibility = View.VISIBLE
                            mDialogView.dialog_tokped_back.isEnabled = false
                            mDialogView.dialog_tokped_back.isClickable = false
                            mDialogView.editReasonOther.visibility = View.VISIBLE
                            mDialogView.editReasonOther.addTextChangedListener(object :
                                TextWatcher {
                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                    if (!s.toString().equals("")) {
                                        mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_green_square)
                                        mDialogView.dialog_tokped_back.isEnabled = true
                                        mDialogView.dialog_tokped_back.isClickable = true
                                    }

                                    if (s.toString().equals("")) {
                                        mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                                        mDialogView.dialog_tokped_back.isEnabled = false
                                        mDialogView.dialog_tokped_back.isClickable = false
                                    }
                                }

                                override fun afterTextChanged(s: Editable?) {
                                }
                            })
                        }
                    }
                }
        } else if (Reason == "Toko Sedang Tutup") {
            mDialogView.restoTutup.visibility = View.VISIBLE
            mDialogView.editDate.setOnClickListener {
                val datePickerFragment = ProfileBirthdayFragment()

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY", lifecycle.viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        mDialogView.editDate.setText(date)
                        if (mDialogView.editCloseReason.text.isNotEmpty()) {
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_green_square)
                            mDialogView.dialog_tokped_back.isEnabled = true
                            mDialogView.dialog_tokped_back.isClickable = true
                        } else {
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                            mDialogView.dialog_tokped_back.isEnabled = false
                            mDialogView.dialog_tokped_back.isClickable = false
                        }
                    }
                }
                datePickerFragment.show(supportFragmentManager, "ProfileBirthdayFragment")
            }

            mDialogView.editCloseReason.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.toString().equals("")) {
                        if (mDialogView.editDate.text.isNotEmpty()) {
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_green_square)
                            mDialogView.dialog_tokped_back.isEnabled = true
                            mDialogView.dialog_tokped_back.isClickable = true
                        } else {
                            mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                            mDialogView.dialog_tokped_back.isEnabled = false
                            mDialogView.dialog_tokped_back.isClickable = false
                        }
                    }

                    if (s.toString().equals("")) {
                        mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                        mDialogView.dialog_tokped_back.isEnabled = false
                        mDialogView.dialog_tokped_back.isClickable = false
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }

        mDialogView.editReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.toString().equals("")) {
                    mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_green_square)
                    mDialogView.dialog_tokped_back.isEnabled = true
                    mDialogView.dialog_tokped_back.isClickable = true
                }

                if (s.toString().equals("")) {
                    mDialogView.dialog_tokped_back.setBackgroundResource(R.drawable.button_dark_gray)
                    mDialogView.dialog_tokped_back.isEnabled = false
                    mDialogView.dialog_tokped_back.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        mDialogView.dialog_tokped_ok.setOnClickListener {
            reasonDialog(position)
            mAlertDialog.dismiss()
        }

        mDialogView.dialog_tokped_back.setOnClickListener {
            Log.e("Success", "Success")
        }
        mDialogView.dialog_reason_close.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun updateTransaction(
        channel: String,
        orderId: String,
        status: String,
        holder: ViewHolder
    ) {
        holder.loadingOverlay.visibility = View.VISIBLE
        setIsLoading(true)
        postUpdate(channel, orderId)
        Handler().postDelayed({
            getListOmni(
                context,
                recyclerView,
                supportFragmentManager,
                activity,
                status,
                empty,
                holder
            )
            notifyDataSetChanged()
        }, 2000)
    }

    private fun setMenu(
        recyclerView: RecyclerView,
        omniDetailList: MutableList<ProductDetailOmni>
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = OmniTransactionProductAdapter(context, omniDetailList)
        recyclerView.adapter = menuList1
    }

    private fun setDate(position: Int) {
        bulanTemp =
            omniList[position].orderTime.toString().substringAfter("-").substringBeforeLast("-")
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

        for (count in omniDetailList[position]) {
            jumlah = jumlah + count.quantity!!.toInt()
            if (count.quantity > 1) {
                price += count.quantity * count.price!!.toInt()
            } else {
                price += count.price!!.toInt()
            }
        }
    }

    private fun formatNumber() {
        str = NumberFormat.getNumberInstance(Locale.US).format(price)
    }

    private fun timeAgo(time: String, holder: TextView) {
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")
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

    private fun postUpdate(channel: String, orderId: String) {
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

    fun getListOmni(
        baseContext: Context,
        recyclerview_transaction: RecyclerView,
        support: FragmentManager,
        activity: Activity,
        status: String,
        empty: ConstraintLayout,
        holder: ViewHolder
    ) {
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
                if (resultList != null) {
                    for (result in resultList) {
                        getOrderDetailOmni(result.orderId.toString())
                        if (result.status == "PAYMENT_CONFIRMATION" || result.status == "PAYMENT_VERIFIED" || result.status == "SELLER_ACCEPT_ORDER" || result.status == "WAITING_FOR_PICKUP") {
                            prosesList.add(result)
                            result.producDetails.let { productList.add(it as ArrayList<ProductDetailOmni>) }
                        } else if (result.status == "SELLER_CANCEL_ORDER" || result.status == "ORDER_REJECTED_BY_SELLER") {
                            batalList.add(result)
                            result.producDetails?.let { producList1.add(it as ArrayList<ProductDetailOmni>) }
                        } else {
                            doneList.add(result)
                            result.producDetails?.let { productList2.add(it as ArrayList<ProductDetailOmni>) }
                        }
                    }
                }
                Handler().postDelayed({
                    if (status == "Proses") {
                        empty.isVisible = prosesList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                            baseContext,
                            prosesList as MutableList<OrderDetailOmni>,
                            productList as MutableList<List<ProductDetailOmni>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            activity,
                            logisticList,
                            empty,
                            lifecycle
                        )
                        omniAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = omniAdapter
                        omniAdapter.notifyDataSetChanged()
                    }
                    if (status == "Batal") {
                        empty.isVisible = batalList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                            baseContext,
                            prosesList as MutableList<OrderDetailOmni>,
                            producList1 as MutableList<List<ProductDetailOmni>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            activity,
                            logisticList,
                            empty,
                            lifecycle
                        )
                        omniAdapter.notifyDataSetChanged()
                        recyclerview_transaction.adapter = omniAdapter
                        omniAdapter.notifyDataSetChanged()
                    }
                    if (status == "Done") {
                        empty.isVisible = doneList.isEmpty()
                        omniAdapter = OmniTransactionListAdapter(
                            baseContext,
                            prosesList as MutableList<OrderDetailOmni>,
                            productList2 as MutableList<List<ProductDetailOmni>>,
                            sessionManager,
                            support,
                            prefHelper,
                            recyclerview_transaction,
                            activity,
                            logisticList,
                            empty,
                            lifecycle
                        )
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

    fun getOrderDetailOmni(orderId: String) {
        var orderId = orderId
        PikappApiService().api.getListOrderDetailOmni(
            getUUID(), getTimestamp(), getClientID(), orderId
        ).enqueue(object : Callback<ListOrderDetailOmni> {
            override fun onFailure(call: Call<ListOrderDetailOmni>, t: Throwable) {
                Log.e("msg", "error: $t")
            }

            override fun onResponse(
                call: Call<ListOrderDetailOmni>,
                response: Response<ListOrderDetailOmni>
            ) {
                val orderResponse = response.body()
                val resultList = orderResponse?.results
                val arrayResultLit = ArrayList<OrderDetailDetailOmni>()
                if (resultList != null) {
                    arrayResultLit.add(resultList)
                    for (result in arrayResultLit) {
                        if (result.logistics != null) {
                            logisticList.add(result.logistics)
                        }
                    }
                }
            }
        })
    }

    interface OmniTransactionClickListener {
        // TODO: Add upload resi implementation.
        fun onUploadResi()
    }
}