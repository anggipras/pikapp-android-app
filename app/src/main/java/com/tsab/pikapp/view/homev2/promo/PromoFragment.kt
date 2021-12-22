package com.tsab.pikapp.view.homev2.promo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.PromoFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.transaction.CancelFragment
import com.tsab.pikapp.view.homev2.transaction.DoneFragment
import com.tsab.pikapp.view.homev2.transaction.ProcessFragment
import com.tsab.pikapp.view.homev2.transaction.TransactionAdapter
import com.tsab.pikapp.viewmodel.homev2.PromoViewModel
import kotlinx.android.synthetic.main.promo_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.*
import kotlinx.android.synthetic.main.transaction_fragment.tabs

class PromoFragment : Fragment() {
    private val sessionManager = SessionManager()
    private lateinit var dataBinding: PromoFragmentBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var viewModel: PromoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.promo_fragment,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this).get(PromoViewModel::class.java)

        if (activity != null && isAdded) {
            sessionManager.setHomeNav(2)
            setUpTabs()

            swipeRefreshLayout = dataBinding.swipePromoMenu
            swipeRefreshLayout.setOnRefreshListener {
                val position = dataBinding.tabs.selectedTabPosition
                refreshPage()
                dataBinding.tabs.selectTab(dataBinding.tabs.getTabAt(position))
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun setUpTabs() {
        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.addFragment(AllPromoFragment(), "Semua")
            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")

            dataBinding.viewpager.adapter = adapter
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
        }
    }

    private fun refreshPage() {
        val adapter = activity?.let { PromoAdapter(childFragmentManager) }
        if (adapter != null) {
            adapter.rmFragment(AllPromoFragment(), "Semua")
            adapter.rmFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.rmFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.rmFragment(ExpirePromoFragment(), "Berakhir")
            adapter.rmFragment(CanceledPromoFragment(), "Dihentikan")

            adapter.addFragment(AllPromoFragment(), "Semua")
            adapter.addFragment(OngoingPromoFragment(), "Berlangsung")
            adapter.addFragment(UpcomingPromoFragment(), "Akan Datang")
            adapter.addFragment(ExpirePromoFragment(), "Berakhir")
            adapter.addFragment(CanceledPromoFragment(), "Dihentikan")

            dataBinding.viewpager.adapter = adapter
            dataBinding.tabs.setupWithViewPager(dataBinding.viewpager)
        }
    }
}