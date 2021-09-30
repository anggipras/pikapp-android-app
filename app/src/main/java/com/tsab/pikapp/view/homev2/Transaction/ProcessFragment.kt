package com.tsab.pikapp.view.homev2.Transaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProccessBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.fragment_proccess.buttonFilterCount
import kotlinx.android.synthetic.main.fragment_proccess.recyclerview_transaction
import kotlinx.android.synthetic.main.fragment_txn_report.*
import kotlinx.android.synthetic.main.layout_loading_overlay.view.*


class ProcessFragment : Fragment() {

    private val viewModel: TransactionViewModel by activityViewModels()
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager1: LinearLayoutManager
    private lateinit var dataBinding: FragmentProccessBinding
    private val sessionManager = SessionManager()

    val filterSheet = FilterFragment()

    var bm: LocalBroadcastManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bm = LocalBroadcastManager.getInstance(requireContext())
        val actionReceiver = IntentFilter()
        actionReceiver.addAction("receivedTransaction")
        bm!!.registerReceiver(mMessageReceiver, actionReceiver)
    }
    
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_proccess,
                container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerview_transaction.setHasFixedSize(true)
        linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager1 =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerview_transaction.layoutManager = linearLayoutManager
        recyclerview_tokopedia.layoutManager = linearLayoutManager1
        activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Proses", requireActivity().supportFragmentManager, emptyState) }
        activity?.let { viewModel.getListOmni(it.baseContext, recyclerview_tokopedia, requireActivity().supportFragmentManager, requireActivity(), "Proses", emptyState) }

        viewModel.editList(recyclerview_transaction, recyclerview_tokopedia, buttonFilterPikapp, buttonFilterTokped,
                buttonFilterGrab, buttonFilterShopee, icon)

        buttonFilterCount.setOnClickListener {
            filterSheet.show(requireActivity().supportFragmentManager, "show")
        }

    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
        //activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Proses", requireActivity().supportFragmentManager, emptyState) }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser){
            recyclerview_transaction.setHasFixedSize(true)
            linearLayoutManager =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
            recyclerview_transaction.layoutManager = linearLayoutManager
            //activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Proses", requireActivity().supportFragmentManager, emptyState) }
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                if (context != null) {
                    viewModel.getStoreOrderList(context, recyclerview_transaction, "Proses", requireActivity().supportFragmentManager, emptyState)
                }
            }

        }
    }

    override fun onDetach() {
        super.onDetach()
        bm?.unregisterReceiver(mMessageReceiver)
    }
}