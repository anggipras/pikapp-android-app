package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnDynamicBinding
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel

class ManualTxnDynamicFragment : Fragment() {
    companion object {
        const val argumentCategoryName = "CATEGORY_NAME"

        fun newInstance(): ManualTxnDynamicFragment {
            return ManualTxnDynamicFragment()
        }
    }

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnDynamicBinding
    private var navController: NavController? = null
    var menuList: MutableList<SearchItem> = mutableListOf()
    lateinit var dynamicAdapter: ListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var categoryName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manual_txn_dynamic, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        categoryName = arguments?.getString(argumentCategoryName).toString()
        viewModel.getMenuList()

        observeViewModel()
        setupRecyclerView()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.menuList.observe(viewLifecycleOwner, Observer { menuList ->
            this.menuList.clear()
            this.menuList.addAll(menuList.filter {
                categoryName == "ALL" || it.merchant_category_name == categoryName
            })

            dynamicAdapter.updateMenuList(this.menuList)
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.totalItems.observe(viewLifecycleOwner, Observer { totalItems ->
            if (totalItems > 0){
                dataBinding.btnNext.visibility = View.VISIBLE
                dataBinding.btnNext.text = "Lihat Keranjang  |  $totalItems Item"
            } else {
                dataBinding.btnNext.visibility = View.GONE
            }
        })

        viewModel.isSearch.observe(viewLifecycleOwner, Observer { isSearch ->
            if(isSearch){
                viewModel.getMenuList()
                viewModel.mutableSearchEnter.value = false
            }
        })
    }

    private fun setupRecyclerView() {
        dynamicAdapter = ListAdapter(
            activity?.baseContext!!,
            menuList.toMutableList(),
            object : ListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, menuList: SearchItem) {
                    viewModel.setPID(menuList.product_id.toString())
                    viewModel.setMenuImg(menuList.pict_01.toString())
                    viewModel.setMenuName(menuList.product_name.toString())
                    viewModel.setMenuPrice(menuList.price.toString())
                    viewModel.setQty(1)
                    view?.let { Navigation.findNavController(it).navigate(R.id.action_homeViewManualTxn_to_manualAddAdvMenuFragment, bundleOf(ManualAddAdvMenuFragment.ADVANCE_MENU_EDIT to false, ManualAddAdvMenuFragment.CART_POSITION to 0)) }
                }
            })
        dataBinding.listMenuDetail.adapter = dynamicAdapter
        dataBinding.listMenuDetail.layoutManager = GridLayoutManager(requireView().context, 3)
    }

    private fun attachInputListeners() {
        dataBinding.btnNext.setOnClickListener {
            viewModel.addTotalQty()
            navController?.navigate(R.id.action_homeViewManualTxn_to_manualTxnCartPage)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.totalItems.observe(viewLifecycleOwner, Observer { totalItems ->
            if (totalItems > 0){
                dataBinding.btnNext.visibility = View.VISIBLE
                dataBinding.btnNext.text = "Lihat Keranjang  |  $totalItems Item"
            } else {
                dataBinding.btnNext.visibility = View.GONE
            }
        })
    }
}