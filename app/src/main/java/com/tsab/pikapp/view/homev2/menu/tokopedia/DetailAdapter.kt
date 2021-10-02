package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R

class DetailAdapter(val context: Context, val list: List<String>, val button: Button, val isChoose:TextView): RecyclerView.Adapter<DetailAdapter.ViewHolder>() {
    private val mutableNama = MutableLiveData("")
    val nama: LiveData<String> get() = mutableNama

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemText: TextView = itemView.findViewById(R.id.categoryDetail)
        var imageView: ImageView = itemView.findViewById(R.id.arrow)
        var field: ConstraintLayout = itemView.findViewById(R.id.field)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.category_detail_row,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailAdapter.ViewHolder, position: Int) {
        holder.itemText.text = list[position]
        holder.imageView.visibility = if(list[position] ==  mutableNama.value){
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        holder.field.setOnClickListener {
            for (etalase in list){
                if(etalase == list[position]){
                    mutableNama.value = list[position]
                }
            }
            Log.e("uwqfh", mutableNama.value)
            isChoose.text = list[position]
            notifyDataSetChanged()
            button.setBackgroundResource(R.drawable.button_green_small)
            button.isEnabled = true
            button.isClickable = true
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}