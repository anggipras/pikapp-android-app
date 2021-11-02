package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R

class ManualChildAdvMenuAdapter(
    val context: Context,
    private val menuChoiceList: MutableList<String>
) : RecyclerView.Adapter<ManualChildAdvMenuAdapter.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var menuChoice: TextView = itemView.findViewById(R.id.menuChoiceAdv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.radiochild_adv_menu_choice_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.menuChoice.text = menuChoiceList[position]
    }

    override fun getItemCount(): Int {
        return menuChoiceList.size
    }
}