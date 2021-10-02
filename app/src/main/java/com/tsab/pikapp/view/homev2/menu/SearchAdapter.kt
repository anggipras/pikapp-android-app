package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchItem

class SearchAdapter(
    val context: Context,
    val menuList: MutableList<SearchItem>,
    val categoryList: MutableList<String>
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    lateinit var linearLayoutManager: LinearLayoutManager
    val menuResult = ArrayList<SearchItem>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemText: TextView = itemView.findViewById(R.id.nama)
        var rView: RecyclerView = itemView.findViewById(R.id.listMenuDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val text: String = categoryList[position]
        holder.itemText.text = text
        setMenu(holder.rView, menuList, menuResult, text)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    private fun setMenu(
        recyclerView: RecyclerView,
        menuList: MutableList<SearchItem>,
        resultList: MutableList<SearchItem>,
        text: String
    ) {
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = MenuListAdapter(context, menuList, text)
        recyclerView.adapter = menuList1
    }

}