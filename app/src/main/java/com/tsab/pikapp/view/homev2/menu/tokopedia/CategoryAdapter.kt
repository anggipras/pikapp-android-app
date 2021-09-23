package com.tsab.pikapp.view.homev2.menu.tokopedia

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.view.homev2.menu.MenuListAdapter

class CategoryAdapter(val context: Context, val list: List<DummyData>, val button: Button, val isChoose:TextView): RecyclerView.Adapter<CategoryAdapter.CategoryVH>() {

    lateinit var linearLayoutManager: LinearLayoutManager

    class CategoryVH(itemView: View): RecyclerView.ViewHolder(itemView){
        var namaCategory : TextView = itemView.findViewById(R.id.nama)
        var image: ImageView = itemView.findViewById(R.id.arrow)
        var categoryDetail : RecyclerView = itemView.findViewById(R.id.recycler_view_category)
        var expandableLayout: ConstraintLayout = itemView.findViewById(R.id.recyclerField)
        var clickField: ConstraintLayout = itemView.findViewById(R.id.field)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.categori_row,parent, false)
        return CategoryVH(view)
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.namaCategory.text = list[position].categoryName

        val isExpandable : Boolean = list[position].expandable
        holder.expandableLayout.visibility = if(isExpandable){
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.image.setImageResource(if(isExpandable){
            R.drawable.ic_baseline_keyboard_arrow_up_24
        } else {
            R.drawable.ic_baseline_keyboard_arrow_down_24
        })

        setMenu(holder.categoryDetail, list[position].listDetail)

        holder.clickField.setOnClickListener {
            list[position].expandable = !list[position].expandable
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun setMenu(recyclerView: RecyclerView, detailList: List<String>){
        linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(false)
        var menuList1 = DetailAdapter(context, detailList, button, isChoose)
        recyclerView.adapter = menuList1
    }


}