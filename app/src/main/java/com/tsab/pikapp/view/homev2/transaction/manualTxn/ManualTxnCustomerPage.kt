package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.annotations.SerializedName
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnCartPageBinding
import com.tsab.pikapp.databinding.FragmentManualTxnCustomerPageBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.models.model.FoodListParentCheck
import com.tsab.pikapp.models.model.FoodListParentRadio
import com.tsab.pikapp.models.model.StoreOrderList
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*
import java.util.*

class ManualTxnCustomerPage : Fragment() {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnCustomerPageBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    lateinit var manualTxnCustomerAdapter: ManualTxnCustomerAdapter
    val customerList = ArrayList<dummyCustomer>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_customer_page, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.recyclerviewCustomer.layoutManager = linearLayoutManager
        dataBinding.recyclerviewCustomer.setHasFixedSize(false)

        observeViewModel()
        attatchInputListeners()
    }

    private fun observeViewModel(){

        viewModel.customerSize.observe(viewLifecycleOwner, androidx.lifecycle.Observer { size->
            if(size == 0){
                dataBinding.searchField.visibility = View.GONE
                dataBinding.searchDivider.visibility = View.GONE
                dataBinding.emptyText.visibility = View.VISIBLE
                dataBinding.buttonContinue.visibility = View.VISIBLE
                dataBinding.btnSelect.visibility = View.GONE
                dataBinding.recyclerviewCustomer.visibility = View.GONE
            } else {
                setRecyclerView()
                dataBinding.searchField.visibility = View.VISIBLE
                dataBinding.searchDivider.visibility = View.VISIBLE
                dataBinding.emptyText.visibility = View.GONE
                dataBinding.buttonContinue.visibility = View.GONE
                dataBinding.btnSelect.visibility = View.VISIBLE
                dataBinding.recyclerviewCustomer.visibility = View.VISIBLE
            }
        })
    }

    private fun attatchInputListeners(){
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.manualTxn -> {
                    Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                    navController?.navigate(R.id.action_manualTxnCustomerPage_to_manualTxnAddCustomer)
                    true
                }
                else -> false
            }
        }

        dataBinding.buttonContinue.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            navController?.navigate(R.id.action_manualTxnCustomerPage_to_manualTxnAddCustomer)
        }
    }

    private fun setRecyclerView(){
        manualTxnCustomerAdapter = ManualTxnCustomerAdapter(requireView().context, viewModel.customerList.value as MutableList<dummyCustomer>)
        manualTxnCustomerAdapter.notifyDataSetChanged()
        dataBinding.recyclerviewCustomer.adapter = manualTxnCustomerAdapter

        manualTxnCustomerAdapter.setOnItemClickListener(object : ManualTxnCustomerAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                Toast.makeText(context, "clicked $position", Toast.LENGTH_SHORT).show()
                viewModel.setCustName(customerList[position].customerName.toString())
                viewModel.setCustPhone(customerList[position].customerPhone)
                viewModel.setCustAddress(customerList[position].customerAddress)
                viewModel.setCustAddressDetail(customerList[position].customerAddressDetail)
                dataBinding.btnSelect.setBackgroundResource(R.drawable.button_green_square)
            }

        })
    }

    data class dummyCustomer(
        @SerializedName("customer_name")
        var customerName: String?,
        @SerializedName("customer_phone")
        var customerPhone: String,
        @SerializedName("customer_address")
        var customerAddress: String,
        @SerializedName("customer_address_detail")
        var customerAddressDetail: String
    )

}