package com.tsab.pikapp.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult

class CategoryAdapter(
        private val context: Context,
        val categoryList: MutableList<CategoryListResult>,
        private val listener: OnItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var name: String = ""
    private var lastChecked: RadioButton? = null
    private var lastCheckedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.resultText.text = categoryList[position].category_name
        holder.resultText.tag = position

        //for default check in first item
        if(position == 0 && holder.resultText.isChecked) {
            lastChecked = holder.resultText;
            lastCheckedPos = 0;
        }

        holder.resultText.setOnClickListener { v ->
            val cb = v as RadioButton
            val clickedPos = (cb.tag as Int).toInt()
            listener.onItemClick(categoryList[position])
            if (cb.isChecked) {
                lastChecked?.isChecked = false
                lastChecked = cb
                lastCheckedPos = clickedPos
            } else lastChecked = null
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resultText: RadioButton = itemView.findViewById(R.id.resultText)
    }

    interface OnItemClickListener {
        fun onItemClick(position: CategoryListResult)
    }
}

