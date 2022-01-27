package com.tsab.pikapp.view.homev2.transaction.manualTxn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentHomeViewManualTxnBinding
import com.tsab.pikapp.services.CacheService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.MenuViewModel

class HomeViewManualTxn : Fragment() {

    private val viewModel: MenuViewModel by activityViewModels()
    private val viewModelDynamic: ManualTxnViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentHomeViewManualTxnBinding

    private var categoryList: List<String> = listOf()
    lateinit var linearLayoutManager: LinearLayoutManager
    private val sessionManager = SessionManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_view_manual_txn, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.manualTransAct.value == 0) {
            activity?.let { viewModel.getMenuManualTxnList(it.baseContext) }
        }

        dataBinding.searchField.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModelDynamic.searchMenu(query!!, true)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        observeViewModel()
        attachInputListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.restartFragment()
        CacheService().deleteCache(requireContext())
    }

    private fun attachInputListeners() {
        sessionManager.setHomeNav(0)
        dataBinding.topAppBar.setNavigationOnClickListener {
            activity?.finish()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })

        dataBinding.searchField.setOnClickListener {
            dataBinding.searchField.onActionViewExpanded()
        }

        dataBinding.nextToCart.setOnClickListener {
            viewModelDynamic.addTotalQty()
            view?.let { it1 -> Navigation.findNavController(it1).navigate(R.id.action_homeViewManualTxn_to_manualTxnCartPage) }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                initViews()
            }
        })

        viewModel.categoryListResult.observe(viewLifecycleOwner, Observer { categoryList ->
            this.categoryList = categoryList.map { it.category_name ?: "" }

            dataBinding.tabs.removeAllTabs()
            this.categoryList.forEach { categoryName ->
                dataBinding.tabs.newTab().apply {
                    text = categoryName
                    dataBinding.tabs.addTab(this)
                }
            }
        })

        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017") {
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        dataBinding.tabs.getTabAt(viewModel.menuTabs.value!!)?.select()
        dataBinding.viewpager.currentItem = viewModel.menuTabs.value!!
        viewModelDynamic.totalItems.observe(viewLifecycleOwner, { totalItems ->
            if (totalItems > 0){
                dataBinding.nextToCart.visibility = View.VISIBLE
                dataBinding.nextToCart.text = getString(R.string.cart, totalItems.toString())
            } else {
                dataBinding.nextToCart.visibility = View.GONE
            }
        })
    }

    private fun initViews() {
        dataBinding.viewpager.offscreenPageLimit = 5
        dataBinding.viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                dataBinding.tabs
            )
        )

        dataBinding.tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                dataBinding.viewpager.currentItem = tab.position
                viewModel.mutableMenuTabs.value = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        setDynamicFragmentToTabLayout()
    }

    private fun setDynamicFragmentToTabLayout() {
        dataBinding.tabs.removeAllTabs()
        categoryList.forEach { categoryName ->
            dataBinding.tabs.newTab().apply {
                text = categoryName
                dataBinding.tabs.addTab(this)
            }
        }

        val dynamicFragmentAdapter = ManualTxnDynamicFragmentAdapter(childFragmentManager, categoryList.size, categoryList)
        dataBinding.viewpager.adapter = dynamicFragmentAdapter
        dataBinding.viewpager.currentItem = 0
    }
}