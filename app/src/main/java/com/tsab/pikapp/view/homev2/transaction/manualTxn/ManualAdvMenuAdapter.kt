package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.DummyAdvData

class ManualAdvMenuAdapter(
    val context: Context,
    val manualAdvMenuList: MutableList<DummyAdvData>
) : RecyclerView.Adapter<ManualAdvMenuAdapter.ViewHolder>() {
    lateinit var linearLayoutManager: LinearLayoutManager

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var parentMenuChoice: TextView = itemView.findViewById(R.id.advMenu_parentChoice)
        var rView: RecyclerView = itemView.findViewById(R.id.recyclerview_childMenuChoice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.manual_advance_menu_radio_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.parentMenuChoice.text = manualAdvMenuList[position].parentMenuChoice
        val childMenuChoice = manualAdvMenuList[position].childMenuChoice as MutableList<String>
        setChildManualAdvMenu(holder.rView, childMenuChoice)
    }

    override fun getItemCount(): Int {
        return manualAdvMenuList.size
    }

    private fun setChildManualAdvMenu(rView: RecyclerView, childMenuChoice: MutableList<String>) {
        linearLayoutManager = LinearLayoutManager(context)
        rView.layoutManager = linearLayoutManager
        rView.setHasFixedSize(false)
        var childRecyclerView = ManualChildAdvMenuAdapter(context, childMenuChoice)
        rView.adapter = childRecyclerView
    }
}