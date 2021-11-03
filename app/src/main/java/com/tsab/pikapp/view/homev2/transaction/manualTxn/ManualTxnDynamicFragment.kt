package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentManualTxnDynamicBinding
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.view.AdvanceMenuActivity
import com.tsab.pikapp.view.homev2.menu.DynamicListAdapter
import com.tsab.pikapp.viewmodel.homev2.DynamicViewModel
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import java.io.Serializable


class ManualTxnDynamicFragment : Fragment() {
    companion object {
        const val argumentCategoryName = "CATEGORY_NAME"

        fun newInstance(): ManualTxnDynamicFragment {
            return ManualTxnDynamicFragment()
        }
    }

    private val viewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentManualTxnDynamicBinding

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
    }

    private fun setupRecyclerView() {
        dynamicAdapter = ListAdapter(
            activity?.baseContext!!,
            menuList.toMutableList(),
            object : ListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, menuList: SearchItem) {
                    Intent(activity?.baseContext, AdvanceMenuActivity::class.java).apply {
                        putExtra(AdvanceMenuActivity.EXTRA_TYPE, AdvanceMenuActivity.TYPE_EDIT)
                        putExtra(AdvanceMenuActivity.MENU_LIST, menuList as Serializable)
                        startActivity(this)
                    }

                }
            })
        dataBinding.listMenuDetail.adapter = dynamicAdapter

        //linearLayoutManager = LinearLayoutManager(requireView().context)
        dataBinding.listMenuDetail.layoutManager = GridLayoutManager(requireView().context, 3)
    }

    private fun attachInputListeners() {
        dataBinding.continueButton.setOnClickListener {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
        }
    }
}