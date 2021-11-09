package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualAddAdvMenuBinding
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ManualAddAdvMenuFragment : Fragment(), ManualChildAdvMenuAdapter.OnItemClickListener {
    private val advData = ArrayList<AdvanceMenu>()
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualAddAdvMenuBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: ManualAdvMenuAdapter
    private val addAdvMenuChoiceTemplate: ArrayList<AddAdvMenuTemp> = ArrayList()
    private val localeID =  Locale("in", "ID")
    var menuPrice: Long = 0

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

        viewModel.fetchAdvanceMenuData(advData, dataBinding.recyclerviewParentMenuChoice)

        adapter = ManualAdvMenuAdapter(requireContext(), advData, addAdvMenuChoiceTemplate, this)
        dataBinding.recyclerviewParentMenuChoice.adapter = adapter

        var menuDetailPrice = "1000"
        this.menuPrice = menuDetailPrice.toLong()
        dataBinding.btnNext.text = getString(R.string.add_cart, menuDetailPrice)
        dataBinding.btnNext.setOnClickListener {
            Log.e("ALLREQDATA", addAdvMenuChoiceTemplate.toString())
//            viewModel.setManualQuantity(dataBinding.menuAmount.text.toString())
//            viewModel.setManualNote(dataBinding.manualNote.text.toString())
        }

        dataBinding.plusButton.setOnClickListener {
            viewModel.addQty()
        }
        dataBinding.minusButton.setOnClickListener {
            viewModel.minusQty()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.quantity.observe(viewLifecycleOwner, androidx.lifecycle.Observer { qty ->
            dataBinding.menuAmount.text = qty.toString()
            this.menuPrice *= qty
            dataBinding.btnNext.text = getString(R.string.add_cart, this.menuPrice.toString())
        })

        viewModel.isAdvReceived.observe(viewLifecycleOwner, androidx.lifecycle.Observer { trigger ->
            if (trigger) {
                for (i in advData) {
                    addAdvMenuChoiceTemplate.add(AddAdvMenuTemp("", mutableListOf(AddMenuChoicesTemp("", "0"))))
                    if (i.template_type == "CHECKBOX") {
                        val indexOfAdvMenu = advData.indexOf(i)
                        val sizeOfAdvMenu = advData[indexOfAdvMenu].ext_menus.size-2
                        for (x in 0..sizeOfAdvMenu) {
                            addAdvMenuChoiceTemplate[indexOfAdvMenu].ext_menus.add(AddMenuChoicesTemp("", "0"))
                        }
                    }
                }
            }
        })
    }

    /*DUMMY ADV DATA*/
    data class AddAdvMenuTemp(var template_name: String?, var ext_menus: MutableList<AddMenuChoicesTemp?>)
    data class AddMenuChoicesTemp(val ext_menu_name: String?, val ext_menu_price: String?)

    override fun onItemClick() {
        var totalPrice = this.menuPrice
        addAdvMenuChoiceTemplate.forEach { menu ->
            menu.ext_menus.forEach { extMenu ->
                if (extMenu != null) {
                    totalPrice += extMenu.ext_menu_price?.toInt()!!
                }
            }
        }
        totalPrice *= viewModel.quantity.value!!
        val numberFormat = NumberFormat.getInstance(localeID).format(totalPrice)
        dataBinding.btnNext.text = getString(R.string.add_cart, numberFormat.toString())
    }
}