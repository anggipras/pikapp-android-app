package com.tsab.pikapp.view.homev2.menu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.TransactionFragmentBinding
import com.tsab.pikapp.view.homev2.HomeActivity
import com.tsab.pikapp.view.TransactionDetailActivity
import com.tsab.pikapp.view.homev2.SearchActivity
import com.tsab.pikapp.view.homev2.Transaction.CancelFragment
import com.tsab.pikapp.view.homev2.Transaction.DoneFragment
import com.tsab.pikapp.view.homev2.Transaction.ProcessFragment
import com.tsab.pikapp.view.homev2.Transaction.TransactionAdapter
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.transaction_fragment.*

class TransactionFragment : Fragment() {

    companion object {
        fun newInstance() = TransactionFragment()
    }

    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var dataBinding: TransactionFragmentBinding
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.transaction_fragment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.overridePendingTransition(0, 0)
        setUpTabs()
        dataBinding.report.setOnClickListener {
            val intent = Intent(activity?.baseContext, TransactionDetailActivity::class.java)
            activity?.startActivityForResult(intent, 1)
            activity?.overridePendingTransition(0, 0)
        }
        Handler().postDelayed({
            dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number = viewModel.proses.value!!.toInt() + viewModel.size1.value!!.toInt()
        }, 2000)
        Handler().postDelayed({
            dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number = viewModel.batal.value!!.toInt()
        }, 2000)
        Handler().postDelayed({
            dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number = viewModel.done.value!!.toInt()
        }, 2000)
        swipeRefreshLayout = swipeTransactionMenu
        swipeRefreshLayout.setOnRefreshListener {
            val position = dataBinding.tabs.selectedTabPosition
            refreshPage()
            Handler().postDelayed({
                dataBinding.tabs.getTabAt(0)?.orCreateBadge?.number = viewModel.proses.value!!.toInt() + viewModel.size1.value!!.toInt()
            }, 2000)
            Handler().postDelayed({
                dataBinding.tabs.getTabAt(2)?.orCreateBadge?.number = viewModel.batal.value!!.toInt()
            }, 2000)
            Handler().postDelayed({
                dataBinding.tabs.getTabAt(1)?.orCreateBadge?.number = viewModel.done.value!!.toInt()
            }, 2000)
            dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
            swipeRefreshLayout.isRefreshing = false
        }
    }

    fun closeKeyboard() {
        val activity = activity as HomeActivity

        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setUpTabs(){
        val adapter = activity?.let { TransactionAdapter(it.supportFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(ProcessFragment(), "Diproses")
            adapter.addFragment(DoneFragment(), "Selesai")
            adapter.addFragment(CancelFragment(), "Batal")
            viewpager.adapter = adapter
            tabs.setupWithViewPager(viewpager)
        }
    }

    private fun refreshPage(){
        val adapter = activity?.let { TransactionAdapter(it.supportFragmentManager) }
        if (adapter != null) {
            adapter.rmFragment(ProcessFragment(), "Diproses")
            adapter.rmFragment(DoneFragment(), "Selesai")
            adapter.rmFragment(CancelFragment(), "Batal")
            adapter.addFragment(ProcessFragment(), "Diproses")
            adapter.addFragment(DoneFragment(), "Selesai")
            adapter.addFragment(CancelFragment(), "Batal")
            viewpager.adapter = adapter
            tabs.setupWithViewPager(viewpager)
        }
    }

}