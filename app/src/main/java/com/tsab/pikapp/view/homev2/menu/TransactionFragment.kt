package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.TransactionFragmentBinding
import com.tsab.pikapp.view.homev2.transaction.TransactionAdapter
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnActivity
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class TransactionFragment : Fragment() {
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var dataBinding: TransactionFragmentBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.transaction_fragment,
            container, false
        )

        val adapter = TransactionAdapter(childFragmentManager, lifecycle)
        dataBinding.viewpager.adapter = adapter
        dataBinding.viewpager.isSaveEnabled = false

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null && isAdded) {
            activity?.overridePendingTransition(0, 0)
            setUpTabs()

           topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                   R.id.manualTxn -> {
                     val intent = Intent(activity?.baseContext, ManualTxnActivity::class.java)
                      activity?.startActivityForResult(intent, 1)
                       activity?.overridePendingTransition(0, 0)
                      true
                  }
                  else -> false
               }
          }

            swipeRefreshLayout = swipeTransactionMenu

            swipeRefreshLayout = dataBinding.swipeTransactionMenu
            swipeRefreshLayout.setOnRefreshListener {
                val position = dataBinding.tabs.selectedTabPosition
                viewModel.getTransactionV2List(requireContext(), true)
                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
                swipeRefreshLayout.isRefreshing = false
            }

            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.processSize.observe(viewLifecycleOwner, Observer {
            dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number = it
        })
        viewModel.doneSize.observe(viewLifecycleOwner, Observer {
            dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number = it
        })
        viewModel.cancelSize.observe(viewLifecycleOwner, Observer {
            dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number = it
        })
    }

    private fun setUpTabs() {
        TabLayoutMediator(
            dataBinding.tabs,
            dataBinding.viewpager
        ) { tab, position ->
            when(position) {
                0 -> tab.text = "Diproses"
                1 -> tab.text = "Selesai"
                else -> tab.text = "Batal"
            }
            dataBinding.viewpager.setCurrentItem(tab.position, true)
        }.attach()
    }
}
