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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ExtraMenuAdapter
import com.tsab.pikapp.view.homev2.menu.MenuListAdapter
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class ManualTxnCartAdapter (
        val context: Context,
        private val manualCartList: MutableList<AddManualAdvMenu>
) : RecyclerView.Adapter<ManualTxnCartAdapter.ViewHolder>(){

    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuName: TextView = itemView.findViewById(R.id.menuName)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var menuPrice: TextView = itemView.findViewById(R.id.menuPrice)
        var menuTopping: RecyclerView = itemView.findViewById(R.id.recyclerview_extra)
        var menuNote: TextView = itemView.findViewById(R.id.menuNote)
        var divider: View = itemView.findViewById(R.id.divider)
        var amount: TextView = itemView.findViewById(R.id.menu_amount)
        var minusBtn: Button = itemView.findViewById(R.id.minus_button)
        var plusBtn: Button = itemView.findViewById(R.id.plus_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualTxnCartAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_manual_txn_cart, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return manualCartList.size
    }

    override fun onBindViewHolder(holder: ManualTxnCartAdapter.ViewHolder, position: Int) {
        val img = manualCartList[position].foodImg
        var list: ArrayList<String> = ArrayList()
        for(i in manualCartList[position].foodListCheckbox){
            if (i != null) {
               for(m in i.foodListChildCheck){
                   if (m != null) {
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
        Log.e("Checkbox", manualCartList[position].foodListCheckbox.toString())
        Log.e("Checkbox", manualCartList[position].foodListRadio.toString())
        Log.e("Checkbox", list.toString())
        setExtra(holder.menuTopping, list)
        Glide.with(context).load(img).transform(RoundedCorners(25), CenterCrop()).into(holder.img)
        holder.menuName.text = manualCartList[position].foodName
        if (manualCartList[position].foodTotalPrice != "null"){
            holder.menuPrice.text = manualCartList[position].foodTotalPrice
        } else {
            holder.menuPrice.text = manualCartList[position].foodPrice
        }
        holder.menuNote.text = manualCartList[position].foodNote
        holder.amount.text = manualCartList[position].foodAmount.toString()
        holder.minusBtn.setOnClickListener {
            if (holder.amount.text.toString().toInt() != 1) {
                var countAmount = manualCartList[position].foodAmount - 1
                var countTotal = manualCartList[position].foodTotalPrice.toInt() - (manualCartList[position].foodTotalPrice.toInt() / manualCartList[position].foodAmount)
                manualCartList[position].foodAmount = countAmount
                holder.amount.text = (holder.amount.text.toString().toInt() - 1).toString()
                manualCartList[position].foodTotalPrice = countTotal.toString()
                holder.menuPrice.text = countTotal.toString()
            }
        }
        holder.plusBtn.setOnClickListener {
            var countAmount = manualCartList[position].foodAmount + 1
            var countTotal = manualCartList[position].foodTotalPrice.toInt() + (manualCartList[position].foodTotalPrice.toInt() / manualCartList[position].foodAmount)
            manualCartList[position].foodAmount = countAmount
            holder.amount.text = (holder.amount.text.toString().toInt() + 1).toString()
            manualCartList[position].foodTotalPrice = countTotal.toString()
            holder.menuPrice.text = countTotal.toString()
        }
        if (position == manualCartList.size - 1){
            holder.divider.visibility = View.GONE
        }
    }
    private fun setExtra(recycler: RecyclerView, list: ArrayList<String>){
        linearLayoutManager = LinearLayoutManager(context)
        recycler.layoutManager = linearLayoutManager
        recycler.setHasFixedSize(true)
        recycler.isNestedScrollingEnabled = false
        var menuList1 = ExtraMenuAdapter(context, list)
        recycler.adapter = menuList1
    }
}