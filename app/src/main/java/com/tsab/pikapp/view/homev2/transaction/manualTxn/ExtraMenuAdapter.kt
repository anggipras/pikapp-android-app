package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.view.homev2.menu.MenuListAdapter

class ExtraMenuAdapter (
    val context: Context,
    private var extraList: ArrayList<String>
) :
    RecyclerView.Adapter<ExtraMenuAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemText: TextView = itemView.findViewById(R.id.menuTopping)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtraMenuAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_extra_menu, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ExtraMenuAdapter.ViewHolder, position: Int) {
        Log.e("List", extraList.toString())
        holder.itemText.text = extraList[position]
    }

    override fun getItemCount(): Int {
        return extraList.size
    }

}