package com.bejohen.pikapp.view.store.myProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemStoreMyProductListBinding
import com.bejohen.pikapp.models.model.StoreProductList
import com.bejohen.pikapp.util.rupiahFormat

class StoreMyProductListAdapter(
    private val storeProductList: ArrayList<StoreProductList>,
    private val productListInterface: ProductListInterface
) : RecyclerView.Adapter<StoreMyProductListAdapter.ProductViewHolder>() {

    private lateinit var dataBinding: ItemStoreMyProductListBinding

    fun updateProductList(newProductList: List<StoreProductList>) {
        storeProductList.clear()
        storeProductList.addAll(newProductList)
        notifyDataSetChanged()
    }

    class ProductViewHolder(var view: ItemStoreMyProductListBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate<ItemStoreMyProductListBinding>(
            inflater,
            R.layout.item_store_my_product_list,
            parent,
            false
        )
        return StoreMyProductListAdapter.ProductViewHolder(dataBinding)
    }

    override fun getItemCount(): Int = storeProductList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.productItem = storeProductList[position]
        holder.view.productPrice.text = rupiahFormat(storeProductList[position].productPrice!!)
        if (storeProductList[position].onOff!!) holder.view.productAvailability.text =
            "Stok Tersedia" else holder.view.productAvailability.text = "Tidak Tersedia"
        holder.view.toggleAvailable.isChecked = storeProductList[position].onOff!!

        holder.view.toggleAvailable.setOnClickListener {
            val status = !holder.view.toggleAvailable.isChecked
            val pid = holder.view.productID.text.toString()
            holder.view.toggleAvailable.isChecked = status
            productListInterface.changeToOnOff(pid, position, status)
        }

        holder.view.buttonEdit.setOnClickListener {
            val pid = holder.view.productID.text.toString()
            productListInterface.onEditTapped(pid)
        }

        holder.view.buttonDelete.setOnClickListener {
            val pid = holder.view.productID.text.toString()
            val pName =  holder.view.productName.text.toString()
            productListInterface.onDeleteTapped(pid, position, pName)
        }
    }

    interface ProductListInterface {
        fun changeToOnOff(pid: String,position: Int, status: Boolean)
        fun onEditTapped(pid: String)
        fun onDeleteTapped(pid: String, position: Int, pName: String)
    }
}