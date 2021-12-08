package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.transaction_fragment.*
import android.text.Editable
import android.text.TextUtils

import android.text.TextWatcher
import android.view.MotionEvent

import android.view.View.OnTouchListener







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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        observeField()
        attachInputListener()
    }

    private fun observeField(){
        dataBinding.custName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        dataBinding.custPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        dataBinding.custAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name: String = dataBinding.custName.text.toString()
                val phone: String = dataBinding.custPhone.text.toString()
                val address: String = dataBinding.custAddress.text.toString()
                validate(name, phone, address)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun validate(name: String, phone: String, address: String){
        if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(phone)&&!TextUtils.isEmpty(address)){
            dataBinding.btnNext.setBackgroundResource(R.drawable.button_green_square)
            dataBinding.btnNext.isEnabled = true
        } else {
            dataBinding.btnNext.setBackgroundResource(R.drawable.button_gray_square)
            dataBinding.btnNext.isEnabled = false
        }
    }

    private fun attachInputListener(){
        dataBinding.btnNext.setOnClickListener {
            if (dataBinding.custName.text.length < 3){
                dataBinding.nameError.visibility = View.VISIBLE
                dataBinding.custName.backgroundTintList = context?.resources?.getColorStateList(R.color.red)
                dataBinding.nameHeaderText.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                viewModel.addCustName(dataBinding.custName.text.toString())
                var phone = dataBinding.custPhone.text.toString()
                viewModel.addCustPhone("0$phone")
                viewModel.addCustAddress(dataBinding.custAddress.text.toString())
                viewModel.addCustAddressDetail(dataBinding.custAddressDetail.text.toString())
                viewModel.addCustomer()
                Toast.makeText(context, "Pelanggan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                navController?.navigate(R.id.action_manualTxnAddCustomer_to_manualTxnCustomerPage)
            }
        }

        topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_manualTxnAddCustomer_to_manualTxnCustomerPage)
        }
    }

}