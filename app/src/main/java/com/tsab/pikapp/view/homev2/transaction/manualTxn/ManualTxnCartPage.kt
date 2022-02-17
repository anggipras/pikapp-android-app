package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dataBinding.loadingOverlay.loadingView.isVisible = true
                    navController?.navigateUp()
                }
            })

        dataBinding.detailBtn.setOnClickListener {
            navController?.navigate(R.id.action_manualTxnCartPage_to_manualTxnDetail)
        }

        dataBinding.btnNext.setOnClickListener {
            viewModel.setInsurance("0")
            viewModel.setEkspedisi("", "0")
            viewModel.liveDataCourierList.value = ArrayList()
            navController?.navigate(R.id.action_manualTxnCartPage_to_checkoutFragment)
        }

        dataBinding.topAppBar.setOnClickListener {
            dataBinding.loadingOverlay.loadingView.isVisible = true
            navController?.navigateUp()
        }
    }

    override fun onItemClick(bool: Boolean, i: Int) {
        if (bool) {
            viewModel.selectedMenuTemp.value?.get(i)?.let {
                it.product_id?.let { it1 -> viewModel.setPID(it1) }
                it.foodImg?.let { it1 -> viewModel.setMenuImg(it1) }
                it.foodName?.let { it1 -> viewModel.setMenuName(it1) }
                it.foodPrice?.let { it1 -> viewModel.setMenuPrice(it1) }
                it.foodAmount?.let { it1 -> viewModel.setQty(it1) }

                var totalExtraPrice = 0
                it.foodListCheckbox.forEach { checkVal ->
                    checkVal?.foodListChildCheck?.forEach { priceVal ->
                        val convertedPrice = priceVal?.price?.toDouble()
                        val rounded = String.format("%.0f", convertedPrice)
                        val roundedToInt = rounded.toInt()
                        totalExtraPrice += roundedToInt
                    }
                }
                it.foodListRadio.forEach { radioVal ->
                    val convertedPrice = radioVal?.foodListChildRadio?.price?.toDouble()
                    val rounded = String.format("%.0f", convertedPrice)
                    val roundedToInt = rounded.toInt()
                    totalExtraPrice += roundedToInt
                }

                viewModel.setExtraPrice(totalExtraPrice)
                viewModel.countTotalPrice()
            }
            view?.let { Navigation.findNavController(it).navigate(R.id.action_manualTxnCartPage_to_manualAddAdvMenuFragment, bundleOf(ManualAddAdvMenuFragment.ADVANCE_MENU_EDIT to true, ManualAddAdvMenuFragment.CART_POSITION to i)) }
        } else {
            if (i == 0) {
                viewModel.setCartItems(0)
                dataBinding.loadingOverlay.loadingView.isVisible = true
                navController?.navigateUp()
            } else {
                viewModel.selectedMenuTemp.value?.let { viewModel.setCartItems(it.size) }
            }
            viewModel.setTotalPrice()
            viewModel.addTotalQty()
        }
    }

    override fun onResume() {
        super.onResume()
        manualTxnCartAdapter.notifyDataSetChanged()
    }
}