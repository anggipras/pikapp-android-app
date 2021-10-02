package com.tsab.pikapp.view.menu.advance.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceAdditionalMenu
import com.tsab.pikapp.models.model.AdvanceAdditionalMenuEdit
import com.tsab.pikapp.view.menuCategory.SortCategoryAdapter
import kotlinx.android.synthetic.main.menu_choice_items_sort.view.*

class AdvanceMenuEditDetailsSortAdapter(
    val menuEditChoiceList: MutableList<AdvanceAdditionalMenuEdit>
) : RecyclerView.Adapter<AdvanceMenuEditDetailsSortAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_choice_items_sort, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdvanceMenuEditDetailsSortAdapter.ViewHolder, position: Int) {
        holder.menuChoiceName.text = menuEditChoiceList[position].ext_menu_name
        holder.menuChoicePrice.text = "Rp ${menuEditChoiceList[position].ext_menu_price}"
    }

    override fun getItemCount(): Int {
        return menuEditChoiceList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var menuChoiceName: TextView = itemView.menuChoiceName
        var menuChoicePrice: TextView = itemView.menuChoicePrice
        override fun onClick(v: View?) {
            val position: Int = adapterPosition
        }
    }
}