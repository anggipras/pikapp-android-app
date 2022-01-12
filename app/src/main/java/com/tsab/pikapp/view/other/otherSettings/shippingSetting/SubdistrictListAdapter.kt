package com.tsab.pikapp.view.other.otherSettings.shippingSetting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SubdistrictResult

class SubdistrictListAdapter(
    private val onSubdistrictListener: OnSubdistrictClickListener
) : RecyclerView.Adapter<SubdistrictListAdapter.ViewHolder>() {
    private var subdistrictList: List<SubdistrictResult> = ArrayList()

    fun setSubdistrictList(subList: List<SubdistrictResult>) {
        this.subdistrictList = subList
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val subdistrict: TextView = view.findViewById(R.id.subdistrict_content)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subdistrict, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subdistrict.text = subdistrictList[position].subdisctrict_name

        holder.itemView.setOnClickListener {
            onSubdistrictListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return subdistrictList.size
    }

    interface OnSubdistrictClickListener {
        fun onItemClick(position: Int)
    }

}