package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualAddAdvMenuBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import kotlinx.android.synthetic.main.fragment_manual_add_adv_menu.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ManualAddAdvMenuFragment : Fragment(), ManualChildAdvMenuAdapter.OnItemClickListener {
    companion object {
        const val ADVANCE_MENU_EDIT = "isEditing"
        const val CART_POSITION = "cartPosition"
    }

    private val advData = ArrayList<AdvanceMenu>()
    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualAddAdvMenuBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: ManualAdvMenuAdapter
    private val addAdvMenuChoiceTemplate: ArrayList<AddAdvMenuTemp> = ArrayList()
    private val localeID =  Locale("in", "ID")
    private val defaultMenuTemp: MutableList<AddManualAdvMenu> = ArrayList()

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

        if (arguments?.getBoolean(ADVANCE_MENU_EDIT) == true) {
            adapter = ManualAdvMenuAdapter(requireContext(), arguments?.getInt(CART_POSITION)!!, viewModel.selectedMenuTemp.value as MutableList<AddManualAdvMenu>, advData, addAdvMenuChoiceTemplate, true, this)
        } else {
            defaultMenuTemp.add(AddManualAdvMenu(
                product_id = "0",
                foodName = "defname",
                foodImg = "defhttp",
                foodAmount = 1,
                foodPrice = "7000",
                foodListCheckbox = listOf(),
                foodListRadio = listOf(),
                foodExtra = "",
                foodNote = "",
                foodTotalPrice = "7000"
            ))
            adapter = ManualAdvMenuAdapter(requireContext(), arguments?.getInt(CART_POSITION)!!, defaultMenuTemp, advData, addAdvMenuChoiceTemplate, false, this)
        }
        dataBinding.recyclerviewParentMenuChoice.adapter = adapter

        dataBinding.advMenuName.text = viewModel.menuName.value
        if (arguments?.getBoolean(ADVANCE_MENU_EDIT) == false) {
            viewModel.setExtraPrice(0)
            viewModel.countTotalPrice()
        }

        dataBinding.plusButton.setOnClickListener {
            viewModel.addQty()
        }
        dataBinding.minusButton.setOnClickListener {
            viewModel.minusQty()
        }

        dataBinding.btnNext.setOnClickListener {
            if (arguments?.getBoolean(ADVANCE_MENU_EDIT) == true) {
                viewModel.editToCart(dataBinding.manualNote.text.toString(), arguments?.getInt(CART_POSITION)!!, addAdvMenuChoiceTemplate)
                adapter.notifyDataSetChanged()
                Navigation.findNavController(view).popBackStack()
            } else {
                viewModel.addToCart(dataBinding.manualNote.text.toString(), addAdvMenuChoiceTemplate, view)
                Toast.makeText(context, "${viewModel.menuName.value} berhasil masuk keranjang", Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.backButton.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.quantity.observe(viewLifecycleOwner, androidx.lifecycle.Observer { qty ->
            dataBinding.menuAmount.text = qty.toString()
        })

        viewModel.menuImg.observe(viewLifecycleOwner, androidx.lifecycle.Observer { img ->
            Picasso.get().load(img).into(menu_img)
        })

        viewModel.menuPrice.observe(viewLifecycleOwner, androidx.lifecycle.Observer { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.advMenuPrice.text = getString(R.string.adv_menu_price, numberFormat.toString())
            dataBinding.btnNext.text = getString(R.string.add_cart, price.toString())
        })

        viewModel.totalPrice.observe(viewLifecycleOwner, androidx.lifecycle.Observer { totalPrice ->
            val thePrice: Long = totalPrice
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.btnNext.text = getString(R.string.add_cart, numberFormat.toString())
        })

        viewModel.isAdvReceived.observe(viewLifecycleOwner, androidx.lifecycle.Observer { trigger ->
            if (trigger) {
                for (i in advData) {
                    addAdvMenuChoiceTemplate.add(AddAdvMenuTemp(i.template_name, i.template_type, mutableListOf(AddMenuChoicesTemp("", "0"))))
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
    data class AddAdvMenuTemp(var template_name: String?, var template_type: String?, var ext_menus: MutableList<AddMenuChoicesTemp?>)
    data class AddMenuChoicesTemp(val ext_menu_name: String?, val ext_menu_price: String?)

    override fun onItemClick() {
        var totalPrice = 0
        addAdvMenuChoiceTemplate.forEach { menu ->
            menu.ext_menus.forEach { extMenu ->
                if (extMenu != null) {
                    val convertedPrice = extMenu.ext_menu_price?.toDouble()
                    val rounded = String.format("%.0f", convertedPrice)
                    val roundedToInt = rounded.toInt()
                    totalPrice += roundedToInt
                }
            }
        }
        viewModel.setExtraPrice(totalPrice)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setTrigger(false)
    }
}