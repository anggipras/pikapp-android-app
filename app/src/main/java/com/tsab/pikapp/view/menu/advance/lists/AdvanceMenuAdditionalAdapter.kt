package com.tsab.pikapp.view.menu.advance.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu

class AdvanceMenuAdditionalAdapter(
    private var additionalMenuList: MutableList<AdvanceAdditionalMenu>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdvanceMenuAdditionalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.itemTitleText)
        val priceText: TextView = view.findViewById(R.id.itemPilihanText)
    }

    interface OnItemClickListener {
        fun onItemClick(advanceAdditionalMenu: AdvanceAdditionalMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_advance_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = additionalMenuList[position].advanceAdditionalMenuName
        holder.priceText.text = "Rp ${additionalMenuList[position].advanceAdditionalMenuPrice}"

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(additionalMenuList[position])
        }
    }

    override fun getItemCount(): Int = additionalMenuList.size

    fun setAdditionalMenuList(additionalMenuList: List<AdvanceAdditionalMenu>) {
        this.additionalMenuList.clear()
        this.additionalMenuList.addAll(additionalMenuList)
        notifyDataSetChanged()
    }
}