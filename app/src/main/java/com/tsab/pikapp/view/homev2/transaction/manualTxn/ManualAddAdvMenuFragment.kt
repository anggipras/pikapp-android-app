package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualAddAdvMenuBinding
import com.tsab.pikapp.models.model.DummyAdvData
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualAddAdvMenuFragment : Fragment() {
    val dummyAdvData = ArrayList<DummyAdvData>()
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualAddAdvMenuBinding
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_add_adv_menu, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.recyclerviewParentMenuChoice.layoutManager = linearLayoutManager
        dataBinding.recyclerviewParentMenuChoice.setHasFixedSize(false)

        dummyAdvData.add(DummyAdvData("Tambah Topping", listOf("Cokelat", "Stroberi", "Oreo", "Green Tea")))
        dummyAdvData.add(DummyAdvData("Tambah Bobba", listOf("Jelly", "Potter", "Harry", "Pottur")))
        dummyAdvData.add(DummyAdvData("Tambah Mantap", listOf("Mantap1", "Mantap2", "Mantap3", "Mantap4")))

        activity?.let { viewModel.getManualAdvanceMenuList(it.baseContext, dataBinding.recyclerviewParentMenuChoice, dummyAdvData) }
    }
}