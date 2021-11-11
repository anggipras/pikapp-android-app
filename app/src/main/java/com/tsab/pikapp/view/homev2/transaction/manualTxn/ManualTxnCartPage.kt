package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualAddAdvMenuBinding
import com.tsab.pikapp.databinding.FragmentManualTxnCartPageBinding
import com.tsab.pikapp.models.model.DummyAdvData
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualTxnCartPage : Fragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnCartPageBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    lateinit var manualTxnCartAdapter: ManualTxnCartAdapter
    val dummyAdvData = ArrayList<DummyAdvData>()


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

        dummyAdvData.add(DummyAdvData("Tambah Topping", listOf("Cokelat", "Stroberi", "Oreo", "Green Tea")))
        dummyAdvData.add(DummyAdvData("Tambah Bobba", listOf("Jelly", "Potter", "Harry", "Pottur")))
        dummyAdvData.add(DummyAdvData("Tambah Mantap", listOf("Mantap1", "Mantap2", "Mantap3", "Mantap4")))

        manualTxnCartAdapter = ManualTxnCartAdapter(requireView().context, dummyAdvData)
        manualTxnCartAdapter.notifyDataSetChanged()
        dataBinding.recyclerviewCart.adapter = manualTxnCartAdapter

        attachInputListener()
    }

    private fun attachInputListener(){
        dataBinding.detailBtn.setOnClickListener {
            navController?.navigate(R.id.action_manualTxnCartPage_to_manualTxnDetail)
        }
    }

}