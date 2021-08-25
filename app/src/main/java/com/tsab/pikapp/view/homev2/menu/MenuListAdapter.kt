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
import com.tsab.pikapp.models.model.SearchList

class MenuListAdapter(val context: Context, val menuList: MutableList<SearchList>, category: String): RecyclerView.Adapter<MenuListAdapter.ViewHolder>() {

    var category: String = category
    var listTetap: ArrayList<SearchList> = menuList as ArrayList<SearchList>
    var jumlah: ArrayList<SearchList> = ArrayList()
    var jumlah1: ArrayList<SearchList> = ArrayList()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemText: TextView = itemView.findViewById(R.id.namaMenu)
        var img: ImageView = itemView.findViewById(R.id.foodimg)
        var itemTextHarga: TextView = itemView.findViewById(R.id.harga)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        for(menu in listTetap){
            if(menu.merchant_category_name == category){
                jumlah.add(menu)
            }
        }
        jumlah1.addAll(jumlah)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.menu_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = jumlah[position].pict_02
        holder.itemTextHarga.text = "Rp." + jumlah[position].price
        Glide.with(context).load(img).transform(RoundedCorners(25), CenterCrop()).into(holder.img)
        holder.itemText.text = jumlah[position].product_name
    }

    override fun getItemCount(): Int {
        return jumlah1.size
    }
}