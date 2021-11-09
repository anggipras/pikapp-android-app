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
import com.tsab.pikapp.models.model.DummyAdvData
import com.tsab.pikapp.models.model.DummyChoices
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualAddAdvMenuFragment : Fragment(), ManualChildAdvMenuAdapter.OnItemClickListener {
    val dummyAdvData = ArrayList<DummyAdvData>()
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualAddAdvMenuBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: ManualAdvMenuAdapter
    private val addAdvMenuChoiceTemplate: ArrayList<AddAdvMenuTemp> = ArrayList()

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

        dummyAdvData.add(DummyAdvData("Tambah Topping", "radio", active = true, mandatory = true, max_choose = 2, ext_menus = listOf(DummyChoices("Cokelat", "1000"), DummyChoices("Stroberi", "2000"), DummyChoices("Pisang", "3000"), DummyChoices("Keju", "4000"))))
        dummyAdvData.add(DummyAdvData("Tambah Bobba", "checkbox", active = true, mandatory = true, max_choose = 2, ext_menus = listOf(DummyChoices("Jelly", "1000"), DummyChoices("Potter", "2000"), DummyChoices("Harry", "3000"), DummyChoices("Pottur", "4000"))))
        dummyAdvData.add(DummyAdvData("Tambah Mantap", "radio", active = true, mandatory = true, max_choose = 2, ext_menus = listOf(DummyChoices("Mantap1", "1000"), DummyChoices("Mantap2", "2000"), DummyChoices("Mantap3", "3000"), DummyChoices("Mantap4", "4000"))))

        for (i in dummyAdvData) {
            addAdvMenuChoiceTemplate.add(AddAdvMenuTemp("", mutableListOf(AddMenuChoicesTemp("", "0"))))
            if (i.template_type == "checkbox") {
                val indexOfAdvMenu = dummyAdvData.indexOf(i)
                val sizeOfAdvMenu = dummyAdvData[indexOfAdvMenu].ext_menus.size-2
                for (x in 0..sizeOfAdvMenu) {
                    addAdvMenuChoiceTemplate[indexOfAdvMenu].ext_menus.add(AddMenuChoicesTemp("", "0"))
                }
            }
        }

        adapter = ManualAdvMenuAdapter(requireContext(), dummyAdvData, addAdvMenuChoiceTemplate, this)
        dataBinding.recyclerviewParentMenuChoice.adapter = adapter

        activity?.let { viewModel.getManualAdvanceMenuList(it.baseContext, dataBinding.recyclerviewParentMenuChoice, dummyAdvData, addAdvMenuChoiceTemplate, this) }

        var sumTotalMenu = "1000"
        dataBinding.btnNext.text = getString(R.string.add_cart, sumTotalMenu)
        dataBinding.btnNext.setOnClickListener {
            Log.e("ALLREQDATA", addAdvMenuChoiceTemplate.toString())
//            viewModel.setManualQuantity(dataBinding.menuAmount.text.toString())
//            viewModel.setManualNote(dataBinding.manualNote.text.toString())
        }
    }

    /*DUMMY ADV DATA*/
    data class AddAdvMenuTemp(var template_name: String?, var ext_menus: MutableList<AddMenuChoicesTemp?>)
    data class AddMenuChoicesTemp(val ext_menu_name: String?, val ext_menu_price: String?)

    override fun onItemClick() {
        Log.e("CLICKED", "testing click")
    }
}