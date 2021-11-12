package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnCartPageBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import java.text.NumberFormat
import java.util.*

class ManualTxnCartPage : Fragment(), ManualTxnCartAdapter.OnItemClickListener {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnCartPageBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    lateinit var manualTxnCartAdapter: ManualTxnCartAdapter
    private val localeID =  Locale("in", "ID")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_cart_page, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.recyclerviewCart.layoutManager = linearLayoutManager
        dataBinding.recyclerviewCart.setHasFixedSize(false)

        manualTxnCartAdapter = ManualTxnCartAdapter(requireView().context, viewModel.selectedMenuTemp.value as MutableList<AddManualAdvMenu>, this)
        manualTxnCartAdapter.notifyDataSetChanged()
        dataBinding.recyclerviewCart.adapter = manualTxnCartAdapter

        observeViewModel()
        attachInputListener()
    }

    private fun observeViewModel(){
        viewModel.totalQuantity.observe(viewLifecycleOwner, Observer { totalQty ->
            dataBinding.btnNext.visibility = View.VISIBLE
            dataBinding.btnNext.text = "Check Out($totalQty)"
        })

        viewModel.totalCart.observe(viewLifecycleOwner, Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.totalPrice.text = "Rp. $numberFormat"
        })
    }

    private fun attachInputListener(){
        dataBinding.detailBtn.setOnClickListener {
            navController?.navigate(R.id.action_manualTxnCartPage_to_manualTxnDetail)
        }
    }

    override fun onItemClick() {
        viewModel.setTotalPrice()
        viewModel.addTotalQty()
    }

}