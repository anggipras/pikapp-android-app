package com.bejohen.pikapp.view.categoryProduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemMerchantProductListSmallBinding
import com.bejohen.pikapp.models.model.ProductListSmall
import kotlinx.android.synthetic.main.item_merchant_product_list_small.view.*

class ProductListSmallAdapter(var productList: List<ProductListSmall>) :
    RecyclerView.Adapter<ProductListSmallAdapter.ProductViewHolder>(), ProductClickListener {

    class ProductViewHolder(var view: ItemMerchantProductListSmallBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemMerchantProductListSmallBinding>(inflater, R.layout.item_merchant_product_list_small, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.productItem = productList[position]
        holder.view.listener = this
    }

    override fun onProductClicked(v: View) {
        val pid = v.productId.text.toString()
        val action = CategoryFragmentDirections.actionFromCategoryFragmentToProductDetailFragment(pid,"0")
        Navigation.findNavController(v).navigate(action)
    }

}