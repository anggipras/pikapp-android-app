package com.tsab.pikapp.view.menuCategory.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.MenuCategory

class CategoryListAdapter(
    private var categoryList: MutableList<MenuCategory>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.itemTitleText)
        val menuCountText: TextView = view.findViewById(R.id.itemDescriptionText)
    }

    interface OnItemClickListener {
        fun onItemClick(category: MenuCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_title_description_action, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleText.text = categoryList[position].categoryName
        holder.menuCountText.text = "Belum ada menu" // TODO: Add menu count functionality.

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(categoryList[position])
        }
    }

    override fun getItemCount(): Int = categoryList.size

    fun setCategoryList(categoryList: List<MenuCategory>) {
        this.categoryList.clear()
        this.categoryList.addAll(categoryList)
        notifyDataSetChanged()
    }
}