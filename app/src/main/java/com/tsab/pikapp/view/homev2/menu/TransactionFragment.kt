package com.tsab.pikapp.view.homev2.menu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.TransactionFragmentBinding
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.homev2.transaction.CancelFragment
import com.tsab.pikapp.view.homev2.transaction.DoneFragment
import com.tsab.pikapp.view.homev2.transaction.ProcessFragment
import com.tsab.pikapp.view.homev2.transaction.TransactionAdapter
import com.tsab.pikapp.view.homev2.transaction.manualTxn.ManualTxnActivity
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber

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
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null && isAdded) {
            activity?.overridePendingTransition(0, 0)
            CoroutineScope(Dispatchers.Main).launch {
                setUpTabs()
            }

            /* Hide manual transaction for next sprint */
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
                refreshPage()
                Handler().postDelayed({
                    dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                }, 2000)
                Handler().postDelayed({
                    dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number =
                        viewModel.batal.value!!.toInt() + viewModel.batalOmni.value!!.toInt()
                }, 2000)
                Handler().postDelayed({
                    dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number =
                        viewModel.done.value!!.toInt() + viewModel.doneOmni.value!!.toInt()
                }, 2000)
                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
                swipeRefreshLayout.isRefreshing = false
            }

            observeViewModel()
        }
    }

    private fun observeViewModel() {
        viewModel.amountOfTransaction.observe(viewLifecycleOwner, Observer { amount ->
            amount?.let {
                if (it >= 2) {
                    dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number =
                        viewModel.batal.value!!.toInt() + viewModel.batalOmni.value!!.toInt()
                    dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number =
                        viewModel.done.value!!.toInt() + viewModel.doneOmni.value!!.toInt()

                    val amountOfBadgeProcess =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    viewModel.setTotalProcessBadge(amountOfBadgeProcess)
                } else {
                    Timber.tag(javaClass.simpleName)
                        .d("All transaction total amount still on progress")
                }
            }
        })

        viewModel.decreaseBadge.observe(viewLifecycleOwner, Observer { amount ->
            amount?.let {
                dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number = it
            }
        })
    }

    private fun setUpTabs() {
        val adapter = activity?.let { TransactionAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(ProcessFragment(), "Diproses")
            adapter.addFragment(DoneFragment(), "Selesai")
            adapter.addFragment(CancelFragment(), "Batal")

            dataBinding.viewpager.adapter = adapter
            tabs.setupWithViewPager(dataBinding.viewpager)
        }
    }

    private fun refreshPage() {
        val adapter = activity?.let { TransactionAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.rmFragment(ProcessFragment(), "Diproses")
            adapter.rmFragment(DoneFragment(), "Selesai")
            adapter.rmFragment(CancelFragment(), "Batal")

            adapter.addFragment(ProcessFragment(), "Diproses")
            adapter.addFragment(DoneFragment(), "Selesai")
            adapter.addFragment(CancelFragment(), "Batal")

            dataBinding.viewpager.adapter = adapter
            tabs.setupWithViewPager(dataBinding.viewpager)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setAmountOfTrans(0)
        viewModel.setProcessBadges(null)
    }
}
