package com.tsab.pikapp.view.homev2.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDoneBinding
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_done.*
import kotlinx.android.synthetic.main.fragment_done.recyclerview_manualTxn
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.fragment_proccess.recyclerview_transaction
import kotlinx.android.synthetic.main.layout_page_problem.view.*

class DoneFragment : Fragment(), TransactionListAdapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    lateinit var transactionListAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager1: LinearLayoutManager
    private lateinit var layoutManagerManualTxn: LinearLayoutManager
    private lateinit var dataBinding: FragmentDoneBinding
    private val sessionManager = SessionManager()
    private val manualViewModel: ManualTxnViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_done,
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
        recyclerview_tokopedia_done.layoutManager = linearLayoutManager1

        layoutManagerManualTxn =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewManualTxn.setHasFixedSize(true)
        dataBinding.recyclerviewManualTxn.layoutManager = layoutManagerManualTxn

        getDataDone()

        general_error_done.try_button.setOnClickListener {
            getDataDone()
        }

        observeViewModel()
    }

    private fun getDataDone() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            activity?.let {
                viewModel.getStoreOrderList(
                    it.baseContext,
                    recyclerview_transaction,
                    "Done",
                    requireActivity().supportFragmentManager,
                    emptyStateDone,
                    this
                )
            }
            activity?.let {
                viewModel.getListOmni(
                    it.baseContext,
                    recyclerview_tokopedia_done,
                    requireActivity().supportFragmentManager,
                    requireActivity(),
                    "Done",
                    emptyStateDone,
                    requireParentFragment()
                )
            }

            activity?.let { manualViewModel.getManualTxnList("CLOSE", it.baseContext, recyclerview_manualTxn, requireActivity()) }
            general_error_done.isVisible = false
        } else {
            general_error_done.isVisible = true
            viewModel.setLoading(false)
        }
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()

        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            activity?.let { viewModel.getStoreOrderList(it.baseContext, recyclerview_transaction, "Done", requireActivity().supportFragmentManager, emptyStateDone, this) }
            general_error_done.isVisible = false
        } else {
            general_error_done.isVisible = true
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                    if (isLoading) View.VISIBLE else View.GONE
        })
    }

    override fun onItemClick(i: Int) {
        TODO("Not yet implemented")
    }

}
