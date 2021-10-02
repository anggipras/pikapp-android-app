package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.view.onboarding.login.navController

class EtalaseAdapter(val context: Context, val list: List<DummyDataEtalase>, val button: Button, val isChoose:TextView): RecyclerView.Adapter<EtalaseAdapter.ViewHolder>() {
    var pHolder: String = "Empty"

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var itemText: TextView = itemView.findViewById(R.id.nama)
        var imageView: ImageView = itemView.findViewById(R.id.arrow)
        var field: ConstraintLayout = itemView.findViewById(R.id.field)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EtalaseAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_etalase,parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: EtalaseAdapter.ViewHolder, position: Int) {
        holder.itemText.text = list[position].etalaseList
        holder.imageView.visibility = if(list[position].choose){
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

        holder.field.setOnClickListener {
            for (etalase in list){
                etalase.choose = etalase.etalaseList == list[position].etalaseList
            }
            isChoose.text = list[position].etalaseList
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