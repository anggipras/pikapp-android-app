package com.tsab.pikapp.view.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ItemTxnCartProductBinding
import com.tsab.pikapp.models.model.CartModel
import com.tsab.pikapp.util.rupiahFormat

class TxnCartProductListAdapter(private val cartList: ArrayList<CartModel>, private val cartListInterface: CartListInterface) : RecyclerView.Adapter<TxnCartProductListAdapter.ProductViewHolder>() {

    fun updateProductList(newProductList: List<CartModel>) {
        cartList.clear()
        cartList.addAll(newProductList)
        notifyDataSetChanged()
    }

    private lateinit var dataBinding: ItemTxnCartProductBinding

    class ProductViewHolder(var view: ItemTxnCartProductBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.item_txn_cart_product, parent, false)
        return ProductViewHolder(dataBinding)
    }

    override fun getItemCount() = cartList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.view.cartList = cartList[position]
        holder.view.productPrice.text = rupiahFormat(cartList[position].totalPrice!!.toLong())

        val qty = cartList[position].qty!!
        val buttonDecrement = holder.view.buttonDecrement
        if (qty > 1) {
            buttonDecrement.isEnabled = true
            buttonDecrement.setBackgroundResource(R.drawable.button_green_rounded)
            buttonDecrement.setTextColor(Color.parseColor("#ffffff"))
        } else {
            buttonDecrement.isEnabled = false
            buttonDecrement.setBackgroundResource(R.drawable.button_darkgray_rounded)
            buttonDecrement.setTextColor(Color.parseColor("#000000"))
        }

        holder.view.buttonIncrement.setOnClickListener {
            cartListInterface.increaseQty(cartList[position])
        }

        holder.view.buttonDecrement.setOnClickListener {
            if(qty != 1) cartListInterface.decreaseQty(cartList[position])
        }

        holder.view.buttonDelete.setOnClickListener {
            cartListInterface.deleteProduct(cartList[position])
        }
    }

    interface CartListInterface {
        fun increaseQty(product: CartModel)
        fun decreaseQty(product: CartModel)
        fun editNote(product: CartModel)
        fun deleteProduct(product: CartModel)
    }
}