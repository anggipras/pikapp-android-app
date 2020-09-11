package com.bejohen.pikapp.view.categoryProduct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.ItemMerchantListBinding
import com.bejohen.pikapp.models.model.MerchantList
import com.bejohen.pikapp.models.model.ProductListSmall
import com.bejohen.pikapp.view.home.ItemHomeCategoryDecoration
import kotlinx.android.synthetic.main.item_merchant_list.view.*

class MerchantListAdapter(val merchantList: ArrayList<MerchantList>) :
    RecyclerView.Adapter<MerchantListAdapter.MerchantViewHolder>(), MerchantClickListener {

    class MerchantViewHolder(var view: ItemMerchantListBinding) : RecyclerView.ViewHolder(view.root)

    private val productListAdapter = ProductListSmallAdapter(arrayListOf())

    fun updateMerchantList(newMerchantList: List<MerchantList>) {
        merchantList.clear()
        merchantList.addAll(newMerchantList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemMerchantListBinding>(
            inflater,
            R.layout.item_merchant_list,
            parent,
            false
        )
        view.productList.apply {
            val gridLayoutManager =
                GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
            val spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing)
            addItemDecoration(ItemHomeCategoryDecoration(spacingInPixels))
            adapter = productListAdapter
        }
        return MerchantViewHolder(view)
    }

    override fun getItemCount(): Int = merchantList.size

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        holder.view.merchantItem = merchantList[position]
        holder.view.listener = this
        val productList : List<ProductListSmall>?
        merchantList[position].products?.let {
            productList = it
            productListAdapter.productList = productList
        }
    }

    override fun onMerchantClicked(v: View) {
        val mid = v.merchantId.text.toString()
        val action = CategoryFragmentDirections.actionToMerchantDetailFragment(mid)
        Navigation.findNavController(v).navigate(action)
    }

}