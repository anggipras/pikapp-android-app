package com.tsab.pikapp.view.menu.advance.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceAdditionalMenuEdit

class AdvanceMenuEditAdditionalAdapter(
    private var additionalMenuList: MutableList<AdvanceAdditionalMenuEdit>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<AdvanceMenuEditAdditionalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.itemTitleText)
        val priceText: TextView = view.findViewById(R.id.itemDescriptionText)
    }

    interface OnItemClickListener {
        fun onItemClick(advanceAdditionalMenu: AdvanceAdditionalMenuEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_menu_extra, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = additionalMenuList[position].ext_menu_name
        holder.priceText.text = "Rp ${additionalMenuList[position].ext_menu_price}"

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(additionalMenuList[position])
        }
    }

    override fun getItemCount(): Int = additionalMenuList.size

    fun setAdditionalMenuEditList(additionalMenuList: List<AdvanceAdditionalMenuEdit>) {
        this.additionalMenuList.clear()
        this.additionalMenuList.addAll(additionalMenuList)
        notifyDataSetChanged()
    }

    fun removeMenuChoice(model: AdvanceAdditionalMenuEdit) {
        val position = additionalMenuList.indexOf(model)
        additionalMenuList.remove(model)
        notifyItemRemoved(position)
    }
}