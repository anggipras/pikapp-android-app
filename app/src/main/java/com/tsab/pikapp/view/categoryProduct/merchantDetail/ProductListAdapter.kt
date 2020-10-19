package com.tsab.pikapp.view.categoryProduct.merchantDetail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemMerchantProductListBinding
import com.tsab.pikapp.models.model.ProductList
import com.tsab.pikapp.util.rupiahFormat
import com.tsab.pikapp.view.categoryProduct.ProductListClickListener
import kotlinx.android.synthetic.main.item_merchant_product_list.view.*

class ProductListAdapter(var merchantProductList: ArrayList<ProductList>, val productAddInterface: ProductAddInterface) :
    RecyclerView.Adapter<ProductListAdapter.MerchantProductListViewHolder>(),
    ProductListClickListener {

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
        dataBinding.textPrice.text = rupiahFormat(merchantProductList[position].productPrice!!.toLong())
        dataBinding.buttonAdd.setOnClickListener {
            val mid = merchantProductList[position].merchantID
            val pid = merchantProductList[position].productID
            val pName = merchantProductList[position].productName
            val pImage = merchantProductList[position].productPicture?.get(0)
            val pPrice = merchantProductList[position].productPrice.toString()
            Log.d("Debug", "product id add : " + pid)
            productAddInterface.onAdd(mid!!, pid!!, pName!!, pImage!!, pPrice)
        }
    }

    override fun onProductListClicked(v: View) {
        val pid = v.listProductProductID.text.toString()
        val action = MerchantDetailFragmentDirections.actionToProductDetailFragment(pid)
        Navigation.findNavController(v).navigate(action)
    }

    interface ProductAddInterface {
        fun onAdd(mid: String, pid: String, pName: String, pImage: String, pPrice: String)
    }

}