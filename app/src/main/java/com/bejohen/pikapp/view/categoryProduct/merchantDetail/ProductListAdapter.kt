package com.bejohen.pikapp.view.categoryProduct.merchantDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemMerchantProductListBinding
import com.bejohen.pikapp.models.model.ProductList
import com.bejohen.pikapp.util.rupiahFormat
import com.bejohen.pikapp.view.categoryProduct.ProductListClickListener
import kotlinx.android.synthetic.main.item_merchant_product_list.view.*

class ProductListAdapter(val merchantProductList: ArrayList<ProductList>, val productAddInterface: ProductAddInterface) :
    RecyclerView.Adapter<ProductListAdapter.MerchantProductListViewHolder>(),
    ProductListClickListener  {

    class MerchantProductListViewHolder(var view: ItemMerchantProductListBinding) :
        RecyclerView.ViewHolder(view.root)

    private lateinit var dataBinding: ItemMerchantProductListBinding

    fun updateMerchantProductList(newMerchantProductList: List<ProductList>) {
        merchantProductList.clear()
        merchantProductList.addAll(newMerchantProductList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MerchantProductListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate<ItemMerchantProductListBinding>(
            inflater,
            R.layout.item_merchant_product_list,
            parent,
            false
        )
        return MerchantProductListViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = merchantProductList.size

    override fun onBindViewHolder(holder: MerchantProductListViewHolder, position: Int) {
        dataBinding.productList = merchantProductList[position]
        dataBinding.listener = this
        dataBinding.textPrice.text = rupiahFormat(merchantProductList[position].productPrice!!)
    }

    override fun onProductListClicked(v: View) {
        val pid = dataBinding.listProductID.text.toString()
        val action = MerchantDetailFragmentDirections.actionToProductDetailFragment(pid, "0")
        Navigation.findNavController(v).navigate(action)
    }

    override fun onAddProductClicked(v: View) {
        val pid = dataBinding.listProductID.text.toString()
        productAddInterface.onAdd(pid)
    }

    interface ProductAddInterface {
        fun onAdd(pid: String)
    }

}