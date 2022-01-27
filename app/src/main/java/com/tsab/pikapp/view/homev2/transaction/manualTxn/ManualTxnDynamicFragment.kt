package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            dataBinding.loadingOverlay.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
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
                    val intent = Intent(requireActivity(), ManualAddAdvMenuActivity::class.java)
                    intent.putExtra(ManualAddAdvMenuActivity.ADVANCE_MENU_EDIT, false)
                    intent.putExtra(ManualAddAdvMenuActivity.CART_POSITION, 0)
                    intent.putExtra(ManualAddAdvMenuActivity.MENU_PID, menuList.product_id.toString())
                    intent.putExtra(ManualAddAdvMenuActivity.MENU_IMG, menuList.pict_01.toString())
                    intent.putExtra(ManualAddAdvMenuActivity.MENU_NAME, menuList.product_name.toString())
                    intent.putExtra(ManualAddAdvMenuActivity.MENU_PRICE, menuList.price.toString())
                    intent.putExtra(ManualAddAdvMenuActivity.MENU_QTY, 1)
                    startActivityForResult(intent, 1)
                }
            })
        dataBinding.listMenuDetail.adapter = dynamicAdapter
        dataBinding.listMenuDetail.layoutManager = GridLayoutManager(requireView().context, 3)
    }
}