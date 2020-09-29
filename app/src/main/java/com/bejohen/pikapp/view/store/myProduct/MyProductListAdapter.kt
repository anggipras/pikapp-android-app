package com.bejohen.pikapp.view.store.myProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemStoreMyProductListBinding
import com.bejohen.pikapp.models.model.StoreProductList

class MyProductListAdapter(val storeProductList: ArrayList<StoreProductList>) :
    RecyclerView.Adapter<MyProductListAdapter.ProductViewHolder>() {

    fun updateProductList(newProductList: List<StoreProductList>) {
        storeProductList.clear()
        storeProductList.addAll(newProductList)
        notifyDataSetChanged()
    }

    class ProductViewHolder(var view: ItemStoreMyProductListBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemStoreMyProductListBinding>(inflater, R.layout.item_store_my_product_list, parent, false)
        return MyProductListAdapter.ProductViewHolder(view)
    }

    override fun getItemCount(): Int  = storeProductList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.productItem = storeProductList[position]
    }
}