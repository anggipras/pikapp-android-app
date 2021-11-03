package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchItem


class ListAdapter(
        val context: Context,
        private val menuList: MutableList<SearchItem>,
        val listener: OnItemClickListener
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemText: TextView = itemView.findViewById(R.id.namaMenu)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var itemTextHarga: TextView = itemView.findViewById(R.id.harga)
        var cardView: CardView = itemView.findViewById(R.id.cardviewMenu)
    }

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): ListAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.transaction_list_manual, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ListAdapter.ViewHolder, position: Int) {
        var screenDisplay = context.resources.displayMetrics
        var screenWidth = screenDisplay.widthPixels
        Log.e("width", screenWidth.toString())
        var newWidth = screenWidth / 3.2
        Log.e("new width", newWidth.toString())

        holder.cardView.layoutParams = FrameLayout.LayoutParams(newWidth.toInt(), newWidth.toInt()).apply {
            setMargins(10, 20, 10, 0)
        }

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