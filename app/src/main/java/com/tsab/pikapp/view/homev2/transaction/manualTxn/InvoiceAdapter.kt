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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AddManualAdvMenu
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class InvoiceAdapter(val context: Context,
                     private val manualCartList: MutableList<AddManualAdvMenu>,
) : RecyclerView.Adapter<InvoiceAdapter.ViewHolder>(){
    lateinit var linearLayoutManager: LinearLayoutManager
    private val localeID =  Locale("in", "ID")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuName: TextView = itemView.findViewById(R.id.nama)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var menuPrice: TextView = itemView.findViewById(R.id.harga)
        var menuNote: TextView = itemView.findViewById(R.id.menuNote)
        var menuTopping: RecyclerView = itemView.findViewById(R.id.advChoice)
        var amount: TextView = itemView.findViewById(R.id.quantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.invoice_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: InvoiceAdapter.ViewHolder, position: Int) {
        val img = manualCartList[position].foodImg
        val list: ArrayList<String> = ArrayList()
        Glide.with(context).load(img).transform(RoundedCorners(25), CenterCrop()).into(holder.img)
        holder.menuName.text = manualCartList[position].foodName
        holder.menuNote.text = manualCartList[position].foodNote
        holder.amount.text = " x " + manualCartList[position].foodAmount.toString()
        if (manualCartList[position].foodTotalPrice != "null"){
            val thePrice: Long = manualCartList[position].foodTotalPrice.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            holder.menuPrice.text = "Rp. ${numberFormat}"
        } else {
            val thePrice: Long = manualCartList[position].foodPrice.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            holder.menuPrice.text = "Rp. ${numberFormat}"
        }
        for(i in manualCartList[position].foodListCheckbox){
            if (i != null) {
                for(m in i.foodListChildCheck){
                    if (m?.name?.isNotEmpty() == true) {
                        list.add(m.name)
                    }
                }
            }
        }
        for(i in manualCartList[position].foodListRadio){
            if (i != null) {
                i.foodListChildRadio?.let { list.add(it.name) }
            }
        }
        setExtra(holder.menuTopping, list)
    }

    override fun getItemCount(): Int {
        return manualCartList.size
    }

    private fun setExtra(recycler: RecyclerView, list: ArrayList<String>){
        linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
        recycler.setHasFixedSize(true)
        recycler.isNestedScrollingEnabled = false
        val menuList1 = InvoiceAdvAdapter(context, list)
        recycler.adapter = menuList1
    }
}