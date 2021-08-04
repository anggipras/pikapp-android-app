package com.tsab.pikapp.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CategoryListResult
import com.tsab.pikapp.models.model.ItemHomeCategory
import com.tsab.pikapp.models.model.MerchantListCategoryResponse
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.category_list.view.*


class CategoryAdapter(private val context: Context, val categoryList: MutableList<CategoryListResult>, private val listener: OnItemClickListener): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    var mSelectedItem = 0
    public var name: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_list, parent, false)
        return ViewHolder(view)
    }

    fun getSelectedItem(): Int {
        return mSelectedItem
    }



    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.resultText.text = categoryList[position].category_name
       /* holder.bindItems(categoryList[position], position, mSelectedItem)*/
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var resultText: TextView = itemView.resultText
        /*fun bindItems(aMSetting: CategoryListResult, position: Int, selectedPosition: Int) {
            itemView.resultText.tag=aMSetting.category_name
           *//* name = itemView.resultText.text.toString()*//*
            itemView.resultText.setOnClickListener {
                mSelectedItem=getAdapterPosition()
                *//*Log.e("Nama", name)*//*
                notifyDataSetChanged()
            }
        }*/

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
                /*itemView.resultText.setChecked(position == mSelectedItem);*/
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}


