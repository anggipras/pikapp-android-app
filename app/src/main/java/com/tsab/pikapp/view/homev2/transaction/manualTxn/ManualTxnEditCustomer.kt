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
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnAddCustomerBinding
import com.tsab.pikapp.databinding.FragmentManualTxnEditCustomerBinding
import com.tsab.pikapp.util.substringPhone
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class ManualTxnEditCustomer : Fragment() {
    private var navController: NavController? = null
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnEditCustomerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_edit_customer, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        attachInputListener()
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.custNameTemp.observe(viewLifecycleOwner, Observer { custName ->
            if (custName != ""){
                dataBinding.custName.append(viewModel.custNameTemp.value)
                dataBinding.custPhone.append(viewModel.custPhoneTemp.value?.substringAfter("0"))
                dataBinding.custAddress.append(viewModel.custAddressTemp.value)
                dataBinding.custAddressDetail.append(viewModel.custAddressDetailTemp.value)
            }
        })
    }

    private fun attachInputListener(){
        topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
        }

        dataBinding.deleteBtn.setOnClickListener {
            viewModel.deleteCustomer()
            Toast.makeText(context, "Pelanggan berhasil dihapus", Toast.LENGTH_SHORT).show()
            navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
        }
        
        dataBinding.btnNext.setOnClickListener {
            if (dataBinding.custName.text.length < 3){
                dataBinding.nameError.visibility = View.VISIBLE
                dataBinding.custName.backgroundTintList = context?.resources?.getColorStateList(R.color.red)
                dataBinding.nameHeaderText.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.editCustName(dataBinding.custName.text.toString())
                var phone = dataBinding.custPhone.text.toString()
                viewModel.editCustPhone("0$phone")
                viewModel.editCustAddress(dataBinding.custAddress.text.toString())
                viewModel.editCustAddressDetail(dataBinding.custAddressDetail.text.toString())
                viewModel.editCustomer()
                Toast.makeText(context, "Pelanggan berhasil diubah", Toast.LENGTH_SHORT).show()
                navController?.navigate(R.id.action_manualTxnEditCustomer_to_manualTxnCustomerPage)
            }
        }
    }
}