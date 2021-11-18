package com.tsab.pikapp.view.homev2.transaction.manualTxn

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
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnAddCustomerBinding
import com.tsab.pikapp.databinding.FragmentManualTxnCustomerPageBinding
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualTxnAddCustomer : Fragment() {

    private var navController: NavController? = null
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnAddCustomerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_add_customer, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        attachInputListener()
    }
    
    private fun attachInputListener(){
        dataBinding.btnNext.setOnClickListener {
            if (dataBinding.custName.text.length < 3){
                dataBinding.nameError.visibility = View.VISIBLE
                dataBinding.custName.backgroundTintList = context?.resources?.getColorStateList(R.color.red)
                dataBinding.nameHeader.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.setCustName(dataBinding.custName.text.toString())
                Log.e("cust name", viewModel.custName.value.toString())
                var phone = dataBinding.custPhone.text.toString()
                viewModel.setCustPhone("0$phone")
                Log.e("phone", viewModel.custPhone.value)
                viewModel.setCustAddress(dataBinding.custAddress.text.toString())
                Log.e("address", viewModel.custAddress.value)
                viewModel.addCustomer()
                Log.e("list", viewModel.customerList.value.toString())
                Toast.makeText(context, "sabi ni bos", Toast.LENGTH_SHORT).show()
            }
        }
    }

}