package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.CustomerResponseDetail

class ManualTxnCustomerAdapter (
    val context: Context,
    private val customerList: MutableList<CustomerResponseDetail>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ManualTxnCustomerAdapter.ViewHolder>(){

    //private lateinit var mListener: onItemClickListener
    var clickedPosition = 0
    var clicked = false

/*    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }*/

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.customerName)
        var phone: TextView = itemView.findViewById(R.id.customerPhone)
        var address: TextView = itemView.findViewById(R.id.customerAddress)
        var addressDetail: TextView = itemView.findViewById(R.id.customerAddressDetail)
        var customerEdit: TextView = itemView.findViewById(R.id.customerEdit)
        var container: ConstraintLayout = itemView.findViewById(R.id.containerCustomer)

        init {
            itemView.setOnClickListener {
                clickedPosition = position;
                clicked = true;
                listener.onItemClick(false, position)
                notifyDataSetChanged();
            }
        }

/*        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
                clickedPosition = position
                clicked = true
                notifyItemChanged(position)
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManualTxnCustomerAdapter.ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.list_manual_txn_customer, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    override fun onBindViewHolder(holder: ManualTxnCustomerAdapter.ViewHolder, position: Int) {
        holder.name.text = customerList[position].name
        holder.phone.text = customerList[position].phone
        holder.address.text = customerList[position].address
        if (customerList[position].addressDetail == ""){
            holder.addressDetail.visibility = View.GONE
        } else {
            holder.addressDetail.text = customerList[position].addressDetail
        }
        holder.customerEdit.setOnClickListener {
            listener.onItemClick(true, position)
        }
        if (clicked){
            if (position == clickedPosition)
                holder.container.setBackgroundResource(R.drawable.cardview_green)
            else
                holder.container.setBackgroundResource(R.drawable.cardview_white)
        }
        holder.itemView
    }

    interface OnItemClickListener {
        fun onItemClick(b: Boolean, i: Int)
    }

}
