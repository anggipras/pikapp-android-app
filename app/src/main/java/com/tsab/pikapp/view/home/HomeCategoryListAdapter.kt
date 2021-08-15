package com.tsab.pikapp.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemHomeCategoryBinding
import com.tsab.pikapp.models.model.ItemHomeCategory
import kotlinx.android.synthetic.main.item_home_category.view.*

class HomeCategoryListAdapter(val categoryItemlist: ArrayList<ItemHomeCategory>) :
    RecyclerView.Adapter<HomeCategoryListAdapter.CategoryViewHolder>(), HomeCategoryClickListener {
    class CategoryViewHolder(var view: ItemHomeCategoryBinding) : RecyclerView.ViewHolder(view.root)

    fun updateCategoryList(newCategoryList: List<ItemHomeCategory>) {
        categoryItemlist.clear()
        categoryItemlist.addAll(newCategoryList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemHomeCategoryBinding>(
            inflater,
            R.layout.item_home_category,
            parent,
            false
        )
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int = categoryItemlist.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.view.categoryItem = categoryItemlist[position]
        holder.view.listener = this
        val par: Long = 1
        if (categoryItemlist[position].categoryId != par) {
            holder.view.categoryImage.setColorFilter(R.color.colorGrey)
            holder.view.categoryImage.alpha = 0.4f
        }
    }

    override fun onCategoryClicked(v: View) {
        val uuid = v.HomeCategoryUuid.text.toString().toLong()
        val par: Long = 1
        if (uuid == par) {
            val action = HomeFragmentDirections.actionToCategoryFragment(uuid)
            Navigation.findNavController(v).navigate(action)
        } else {

        }
    }

}