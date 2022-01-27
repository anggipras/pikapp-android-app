package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityManualAddAdvMenuBinding
import com.tsab.pikapp.models.model.AddManualAdvMenu
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.services.CacheService
import com.tsab.pikapp.viewmodel.homev2.ManualTxnAddViewModel
import kotlinx.android.synthetic.main.fragment_manual_add_adv_menu.*
import java.io.Serializable
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ManualAddAdvMenuActivity : AppCompatActivity(), ManualChildAdvMenuAdapterDev.OnItemClickListener {
    companion object {
        const val ADVANCE_MENU_EDIT = "isEditing"
        const val CART_POSITION = "cartPosition"
        const val MENU_PID = "menu_pid"
        const val MENU_IMG = "menu_img"
        const val MENU_NAME = "menu_name"
        const val MENU_PRICE = "menu_price"
        const val MENU_QTY = "menu_qty"
    }

    private val advData = ArrayList<AdvanceMenu>()
    private val viewModel: ManualTxnAddViewModel by viewModels()
    private lateinit var dataBinding: ActivityManualAddAdvMenuBinding
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var adapter: ManualAdvMenuAdapterDev
    private val addAdvMenuChoiceTemplate: ArrayList<AddAdvMenuTemp> = ArrayList()
    private val localeID =  Locale("in", "ID")
    private val defaultMenuTemp: MutableList<AddManualAdvMenu> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_manual_add_adv_menu)

        linearLayoutManager = LinearLayoutManager(baseContext)
        dataBinding.recyclerviewParentMenuChoice.layoutManager = linearLayoutManager

        val menuPidManualTxn = intent.getStringExtra(MENU_PID)
        val menuImgManualTxn = intent.getStringExtra(MENU_IMG)
        val menuNameManualTxn = intent.getStringExtra(MENU_NAME)
        val menuPriceManualTxn = intent.getStringExtra(MENU_PRICE)
        val menuQtyManualTxn = intent.getIntExtra(MENU_QTY, 1)
        viewModel.setPID(menuPidManualTxn!!)
        viewModel.setMenuImg(menuImgManualTxn!!)
        viewModel.setMenuName(menuNameManualTxn!!)
        viewModel.setMenuPrice(menuPriceManualTxn!!)
        viewModel.setQty(menuQtyManualTxn)

        viewModel.fetchAdvanceMenuData(advData, dataBinding.recyclerviewParentMenuChoice)

        val addOrEditManualTxn = intent.getBooleanExtra(ADVANCE_MENU_EDIT, false)
        val cartPosManualTxn = intent.getIntExtra(CART_POSITION, 0)
        if (addOrEditManualTxn) {
            adapter = ManualAdvMenuAdapterDev(baseContext, cartPosManualTxn, viewModel.selectedMenuTemp.value as MutableList<AddManualAdvMenu>, advData, addAdvMenuChoiceTemplate, true, this)
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
            adapter = ManualAdvMenuAdapterDev(baseContext, cartPosManualTxn, defaultMenuTemp, advData, addAdvMenuChoiceTemplate, false, this)
        }
        dataBinding.recyclerviewParentMenuChoice.adapter = adapter

        dataBinding.advMenuName.text = viewModel.menuName.value
        if (!addOrEditManualTxn) {
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
            if (addOrEditManualTxn) {
                viewModel.editToCart(dataBinding.manualNote.text.toString(), cartPosManualTxn, addAdvMenuChoiceTemplate)
                adapter.notifyDataSetChanged()
                finish()
            } else {
                dataBinding.loadingOverlay.loadingView.isVisible = true
                viewModel.addToCart(dataBinding.manualNote.text.toString(), addAdvMenuChoiceTemplate)
                Toast.makeText(baseContext, "${viewModel.menuName.value} berhasil masuk keranjang", Toast.LENGTH_SHORT).show()
            }
        }

        dataBinding.backButton.setOnClickListener {
            finish()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.resultOK.observe(this, { res ->
            if (res) {
                val intent = Intent()
                intent.putExtra("MENU_LIST_DATA", viewModel.selectedMenuTemp.value as Serializable)
                intent.putExtra("MENU_QTY", viewModel.quantity.value!!)
                intent.putExtra("MENU_PRICE", viewModel.menuPrice.value.toString())
                intent.putExtra("MENU_TOTAL_PRICE", viewModel.totalPrice.value)
                intent.putExtra("MANUAL_TXN_ACT", 1)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        viewModel.quantity.observe(this, { qty ->
            dataBinding.menuAmount.text = qty.toString()
        })

        viewModel.menuImg.observe(this, { img ->
            Picasso.get().load(img).into(menu_img)
        })

        viewModel.menuPrice.observe(this, { price ->
            val thePrice: Long = price.toLong()
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.advMenuPrice.text = getString(R.string.adv_menu_price, numberFormat.toString())
            dataBinding.btnNext.text = getString(R.string.add_cart, price.toString())
        })

        viewModel.totalPrice.observe(this, { totalPrice ->
            val thePrice: Long = totalPrice
            val numberFormat = NumberFormat.getInstance(localeID).format(thePrice)
            dataBinding.btnNext.text = getString(R.string.add_cart, numberFormat.toString())
        })

        viewModel.isAdvReceived.observe(this, { trigger ->
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
        CacheService().deleteCache(baseContext)
    }
}