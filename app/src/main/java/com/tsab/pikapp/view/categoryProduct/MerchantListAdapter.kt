package com.tsab.pikapp.view.categoryProduct

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemMerchantListBinding
import com.tsab.pikapp.models.model.MerchantList
import com.tsab.pikapp.view.home.ItemHomeCategoryDecoration

class MerchantListAdapter(private val merchantList: ArrayList<MerchantList>, val merchantClickInterface: MerchantClickInterface) :
    RecyclerView.Adapter<MerchantListAdapter.MerchantViewHolder>(), ProductListSmallAdapter.ProductClickInterface {

    class MerchantViewHolder(var view: ItemMerchantListBinding) : RecyclerView.ViewHolder(view.root)

    private val productListAdapter = ProductListSmallAdapter(arrayListOf(), this)

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
            val gridLayoutManager = GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false)
            layoutManager = gridLayoutManager
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing2)
            addItemDecoration(ItemHomeCategoryDecoration(spacingInPixels))
            adapter = productListAdapter
        }

        return MerchantViewHolder(view)
    }

    override fun getItemCount(): Int = merchantList.size

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        holder.view.merchantItem = merchantList[position]

        merchantList[position].products?.let {
            val productList = it
            productListAdapter.productList = productList
        }

//        holder.view.productList.setHasFixedSize(true)
//        holder.view.productList.isNestedScrollingEnabled = false
        holder.view.buttonMerchantCategory.setOnClickListener {
            val mid = holder.view.merchantId.text.toString()
            merchantClickInterface.onClickMerchant(mid)
        }
    }

    override fun onClickProductSmall(pid: String) {
        merchantClickInterface.onClickProductSmall(pid)
    }

    interface MerchantClickInterface {
        fun onClickMerchant(mid: String)
        fun onClickProductSmall(pid: String)
    }
}