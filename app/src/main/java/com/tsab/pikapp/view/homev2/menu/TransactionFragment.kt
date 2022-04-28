package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.TransactionFragmentBinding
import com.tsab.pikapp.view.homev2.transaction.TransactionAdapter
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnActivity
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

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

            dataBinding.tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when(tab.position) {
                        0 -> viewModel.setTabPosition(0)
                        1 -> viewModel.setTabPosition(1)
                        else -> viewModel.setTabPosition(2)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    // No-op
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    // No-op
                }
            })

           topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                   R.id.manualTxn -> {
                     val intent = Intent(activity?.baseContext, ManualTxnActivity::class.java)
                      activity?.startActivityForResult(intent, 1)
                       activity?.overridePendingTransition(R.anim.slide_up, R.anim.no_animation)
                      true
                  }
                  else -> false
               }
          }

            swipeRefreshLayout = swipeTransactionMenu

            swipeRefreshLayout = dataBinding.swipeTransactionMenu
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.restartFragment()
                val position = dataBinding.tabs.selectedTabPosition
                when(position) {
                    0 -> viewModel.getProcessTransactionV2List(requireContext(), true, 1)
                    1 -> {
                        viewModel.mutablePageDone.value = 0
                        viewModel.getDoneTransactionV2List(requireContext(), true, 1)
                    }
                    2 -> viewModel.getCancelTransactionV2List(requireContext(), true, 1)
                }
                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
                swipeRefreshLayout.isRefreshing = false
            }

            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.processSize.observe(viewLifecycleOwner, {
            dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number = it
        })
        viewModel.doneSize.observe(viewLifecycleOwner, {
            dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number = it
        })
        viewModel.cancelSize.observe(viewLifecycleOwner, {
            dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number = it
        })
    }

    private fun setUpTabs() {
        viewModel.getBadgesTransactionV2List(requireContext())

        TabLayoutMediator(
            dataBinding.tabs,
            dataBinding.viewpager
        ) { tab, position ->
            when(position) {
                0 -> tab.text = "Diproses"
                1 -> tab.text = "Selesai"
                2 -> tab.text = "Batal"
            }
            dataBinding.viewpager.setCurrentItem(tab.position, true)
        }.attach()
    }
}
