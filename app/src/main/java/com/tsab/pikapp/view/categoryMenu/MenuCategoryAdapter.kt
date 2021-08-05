package com.tsab.pikapp.view.categoryMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import kotlinx.android.synthetic.main.category_items.view.*

class MenuCategoryAdapter (private val context: Context, val menuCategoryList: MutableList<CategoryListResult>, private val listener: OnItemClickListener): RecyclerView.Adapter<MenuCategoryAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_menu_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.resultText.text = menuCategoryList[position].category_name
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener{
        var resultText: TextView = itemView.resultText

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

}