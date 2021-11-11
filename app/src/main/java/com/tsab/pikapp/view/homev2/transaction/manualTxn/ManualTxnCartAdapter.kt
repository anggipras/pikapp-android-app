package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.DummyAdvData
import com.tsab.pikapp.models.model.SearchItem
import kotlinx.android.synthetic.main.item_transaction_menu.view.*

class ManualTxnCartAdapter (
        val context: Context,
        val manualAdvMenuList: MutableList<DummyAdvData>
        //val listener: ListAdapter.OnItemClickListener
) : RecyclerView.Adapter<ManualTxnCartAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuName: TextView = itemView.findViewById(R.id.menuName)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var menuPrice: TextView = itemView.findViewById(R.id.menuPrice)
        var menuTopping: TextView = itemView.findViewById(R.id.menuTopping)
        var menuNote: TextView = itemView.findViewById(R.id.menuNote)
        var divider: View = itemView.findViewById(R.id.divider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualTxnCartAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_manual_txn_cart, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return manualAdvMenuList.size
    }

    override fun onBindViewHolder(holder: ManualTxnCartAdapter.ViewHolder, position: Int) {
        holder.menuName.text = "Burger"
        if (position == manualAdvMenuList.size - 1){
            holder.divider.visibility = View.GONE
        }
    }
}