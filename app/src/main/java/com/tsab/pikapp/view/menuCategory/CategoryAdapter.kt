package com.tsab.pikapp.view.menuCategory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import kotlinx.android.synthetic.main.category_items.view.*

class CategoryAdapter(
    private val context: Context,
    val categoryList: MutableList<CategoryListResult>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.resultText.text = categoryList[position].category_name
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resultText: TextView = itemView.resultText
    }
}