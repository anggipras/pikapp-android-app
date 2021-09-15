package com.tsab.pikapp.view.homev2.Transaction

import androidx.recyclerview.widget.DiffUtil
import com.tsab.pikapp.models.model.StoreOrderList

class MyDiffUtil(
        private val oldList: MutableList<StoreOrderList>,
        private val newList: MutableList<StoreOrderList>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].transactionID == newList[newItemPosition].transactionID
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].transactionID != newList[newItemPosition].transactionID -> {
                false
            }
            oldList[oldItemPosition].status != newList[newItemPosition].status -> {
                false
            }
            oldList[oldItemPosition].bizType != newList[newItemPosition].bizType -> {
                false
            }
            oldList[oldItemPosition].customerName != newList[newItemPosition].customerName -> {
                false
            }
            oldList[oldItemPosition].paymentWith != newList[newItemPosition].paymentWith -> {
                false
            }
            oldList[oldItemPosition].tableNo != newList[newItemPosition].tableNo -> {
                false
            }
            oldList[oldItemPosition].transactionTime != newList[newItemPosition].transactionTime -> {
                false
            }
            /*oldList[oldItemPosition].detailProduct != newList[newItemPosition].detailProduct -> {
                false
            }*/
            else -> true
        }
    }
}