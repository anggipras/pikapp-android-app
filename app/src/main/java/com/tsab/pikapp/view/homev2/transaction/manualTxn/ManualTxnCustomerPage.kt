package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.annotations.SerializedName
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnCartPageBinding
import com.tsab.pikapp.databinding.FragmentManualTxnCustomerPageBinding
import com.tsab.pikapp.models.model.*
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.other_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import java.util.*

class ManualTxnCustomerPage : Fragment(), ManualTxnCustomerAdapter.OnItemClickListener {
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnCustomerPageBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    lateinit var manualTxnCustomerAdapter: ManualTxnCustomerAdapter
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_customer_page, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        swipeRefreshLayout = dataBinding.swipeCustomerList
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getCustomer()
            swipeRefreshLayout.isRefreshing = false
        }

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.recyclerviewCustomer.layoutManager = linearLayoutManager
        dataBinding.recyclerviewCustomer.setHasFixedSize(false)

        viewModel.getCustomer()

        observeViewModel()
        attatchInputListeners()
    }

    private fun observeViewModel(){
        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.customerSize.observe(viewLifecycleOwner, androidx.lifecycle.Observer { size->
            if(size == 0){
                dataBinding.searchField.visibility = View.GONE
                dataBinding.searchDivider.visibility = View.GONE
                dataBinding.emptyText.visibility = View.VISIBLE
                dataBinding.buttonContinue.visibility = View.VISIBLE
                dataBinding.btnSelect.visibility = View.GONE
                dataBinding.recyclerviewCustomer.visibility = View.GONE
                dataBinding.imageEmpty.visibility = View.VISIBLE
            } else {
                setRecyclerView()
                dataBinding.searchField.visibility = View.VISIBLE
                dataBinding.searchDivider.visibility = View.VISIBLE
                dataBinding.emptyText.visibility = View.GONE
                dataBinding.buttonContinue.visibility = View.GONE
                dataBinding.btnSelect.visibility = View.VISIBLE
                dataBinding.recyclerviewCustomer.visibility = View.VISIBLE
                dataBinding.imageEmpty.visibility = View.GONE
            }
        })
    }

    private fun attatchInputListeners(){
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController?.navigate(R.id.action_manualTxnCustomerPage_to_checkoutFragment)
                }
            })

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

        topAppBar.setNavigationOnClickListener {
            navController?.navigate(R.id.action_manualTxnCustomerPage_to_checkoutFragment)
        }

        dataBinding.buttonContinue.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            navController?.navigate(R.id.action_manualTxnCustomerPage_to_manualTxnAddCustomer)
        }

        dataBinding.btnSelect.setOnClickListener {
            Toast.makeText(context, "Pelanggan berhasil dipilih", Toast.LENGTH_SHORT).show()
            viewModel.setCustName(viewModel.custNameTemp.value.toString())
            viewModel.custIdTemp.value?.let { it1 -> viewModel.setCustId(it1.toLong()) }
            viewModel.setCustPhone(viewModel.custPhoneTemp.value.toString())
            viewModel.setCustAddress(viewModel.custAddressTemp.value.toString())
            viewModel.setCustAddressDetail(viewModel.custAddressDetailTemp.value.toString())
            navController?.navigate(R.id.action_manualTxnCustomerPage_to_checkoutFragment)
        }
    }

    private fun setRecyclerView(){
        manualTxnCustomerAdapter = ManualTxnCustomerAdapter(requireView().context, viewModel.customerList.value as MutableList<CustomerResponseDetail>, this)
        manualTxnCustomerAdapter.notifyDataSetChanged()
        dataBinding.recyclerviewCustomer.adapter = manualTxnCustomerAdapter

    }

    override fun onItemClick(b: Boolean, i: Int) {
        if (b){
            viewModel.customerList.value?.get(i)?.let {
                it.name?.let { it1 -> viewModel.setCustNameTemp(it1) }
                it.phone?.let { it1 -> viewModel.setCustPhoneTemp(it1) }
                it.address?.let { it1 -> viewModel.setCustAddressTemp(it1) }
                it.addressDetail?.let { it1 -> viewModel.setCustAddressDetailTemp(it1) }
                it.customerId?.let { it1 -> viewModel.setCustIdTemp(it1) }
            }
            view?.let { Navigation.findNavController(it).navigate(R.id.action_manualTxnCustomerPage_to_manualTxnEditCustomer) }
        } else {
            viewModel.customerList.value?.get(i)?.let {
                it.name?.let { it1 -> viewModel.setCustNameTemp(it1) }
                it.phone?.let { it1 -> viewModel.setCustPhoneTemp(it1) }
                it.address?.let { it1 -> viewModel.setCustAddressTemp(it1) }
                it.addressDetail?.let { it1 -> viewModel.setCustAddressDetailTemp(it1) }
                it.customerId?.let { it1 -> viewModel.setCustIdTemp(it1) }
            }
            dataBinding.btnSelect.setBackgroundResource(R.drawable.button_green_square)
            dataBinding.btnSelect.isEnabled = true
        }
    }

}
