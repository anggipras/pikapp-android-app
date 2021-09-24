package com.tsab.pikapp.view.menu.advance.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.models.model.AdvanceMenuEdit

class AdvanceMenuEditAdapter(
    private var advanceMenuList: MutableList<AdvanceMenuEdit>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdvanceMenuEditAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.itemTitleText)
        val priceText: TextView = view.findViewById(R.id.itemDescriptionText)
    }

    interface OnItemClickListener {
        fun onItemClick(advanceMenu: AdvanceMenuEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_title_description_action, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = advanceMenuList[position].template_name
        holder.priceText.text = "${advanceMenuList[position].ext_menus.size} pilihan"

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(advanceMenuList[position])
        }
    }

    override fun getItemCount(): Int = advanceMenuList.size

    fun setAdvanceMenuEditList(advanceMenuList: List<AdvanceMenuEdit>) {
        this.advanceMenuList.clear()
        this.advanceMenuList.addAll(advanceMenuList)
        notifyDataSetChanged()
    }
}