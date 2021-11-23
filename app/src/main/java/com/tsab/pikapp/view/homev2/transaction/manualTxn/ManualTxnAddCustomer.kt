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
                dataBinding.nameHeaderText.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.setCustName(dataBinding.custName.text.toString())
                var phone = dataBinding.custPhone.text.toString()
                viewModel.setCustPhone("0$phone")
                viewModel.setCustAddress(dataBinding.custAddress.text.toString())
                viewModel.setCustAddressDetail(dataBinding.custAddressDetail.text.toString())
                viewModel.addCustomer()
                Toast.makeText(context, "Pelanggan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                navController?.navigate(R.id.action_manualTxnAddCustomer_to_manualTxnCustomerPage)
            }
        }
    }

}