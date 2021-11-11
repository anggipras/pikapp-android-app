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
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualTxnCartPage : Fragment() {

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnCartPageBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    private var navController: NavController? = null
    lateinit var manualTxnCartAdapter: ManualTxnCartAdapter
    val dummyAdvData = ArrayList<AddManualAdvMenu>()

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

        dummyAdvData.add(AddManualAdvMenu(
                "123456",
                "Croffle",
                "", 1, "Rp. 3000",
                listOf(),
                listOf(),
                "",
                "crispy kang",
                "Rp. 3000" ))

        manualTxnCartAdapter = ManualTxnCartAdapter(requireView().context, viewModel.selectedMenuTemp.value as MutableList<AddManualAdvMenu>)
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