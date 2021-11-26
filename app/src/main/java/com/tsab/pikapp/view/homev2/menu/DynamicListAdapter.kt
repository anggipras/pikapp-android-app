package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchItem

class DynamicListAdapter(
    val context: Context,
    private val menuList: MutableList<SearchItem>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<DynamicListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemText: TextView = itemView.findViewById(R.id.namaMenu)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var itemTextHarga: TextView = itemView.findViewById(R.id.harga)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DynamicListAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.menu_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DynamicListAdapter.ViewHolder, position: Int) {
        val img = menuList[position].pict_02
        holder.itemTextHarga.text = "Rp. " + menuList[position].price
        Glide.with(context).load(img).transform(RoundedCorners(25), CenterCrop()).into(holder.img)
        holder.itemText.text = menuList[position].product_name

        holder.itemView.setOnClickListener { listener.onItemClick(position, menuList[position]) }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    fun updateMenuList(newMenuList: List<SearchItem>) {
        menuList.clear()
        menuList.addAll(newMenuList)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, menuList: SearchItem)
    }
}