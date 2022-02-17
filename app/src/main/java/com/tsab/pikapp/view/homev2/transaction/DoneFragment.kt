package com.tsab.pikapp.view.homev2.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDoneBinding
import com.tsab.pikapp.models.model.UpdateStatusManualTxnRequest
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_done.*
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import androidx.core.widget.NestedScrollView

class DoneFragment : Fragment(), TransactionListV2Adapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentDoneBinding
    private lateinit var recyclerAdapter: TransactionListV2Adapter
    private val onlineService = OnlineService()

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
        viewModel.mutableFinishPageStateDone.value = false

        initRecyclerView()
        initViewModel()

        general_error_done.try_button.setOnClickListener {
            getDataDone()
        }

        dataBinding.nestedScrollDone.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            // on scroll change we are checking when users scroll as bottom.
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                if (!viewModel.finishPageStateDone.value!!) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    val pageDoneAct = viewModel.pageDone.value!! + 1
                    dataBinding.loadingPB.isVisible = true
                    viewModel.getDoneTransactionV2PaginationList(requireContext(), true, pageDoneAct)
                } else {
                    dataBinding.loadingPB.isVisible = false
                }
            }
        })

        viewModel.finishPageStateDone.observe(viewLifecycleOwner, {
            if (it) {
                dataBinding.loadingPB.isVisible = false
            }
        })
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllTransactionDone.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionListV2Adapter(requireContext(), requireActivity(), requireActivity().supportFragmentManager,this)
        dataBinding.recyclerviewAllTransactionDone.adapter = recyclerAdapter
    }

    private fun initViewModel() {
        viewModel.getLiveDataTransListV2DoneObserver().observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                dataBinding.emptyStateDone.visibility = View.GONE
                recyclerAdapter.setTransactionList(it)
            } else {
                dataBinding.emptyStateDone.visibility = View.VISIBLE
            }
        })

        viewModel.errorLoading.observe(viewLifecycleOwner, { error ->
            if (error) {
                general_error_done.isVisible = true
                onlineService.serviceDialog(requireActivity())
            } else {
                general_error_done.isVisible = false
            }
        })

        viewModel.emptyState.observe(viewLifecycleOwner, {
            if (it) {
                dataBinding.emptyStateDone.visibility = View.VISIBLE
            } else {
                dataBinding.emptyStateDone.visibility = View.GONE
            }
        })

        viewModel.tabPosition.observe(viewLifecycleOwner, {
            if (it == 1) {
                viewModel.mutablePageDone.value = 0
                getDataDone()
            }
        })
    }

    private fun getDataDone() {
        if (onlineService.isOnline(context)) {
            viewModel.getDoneTransactionV2List(requireContext(), true, 0)
            general_error_done.isVisible = false
        } else {
            general_error_done.isVisible = true
            onlineService.networkDialog(requireActivity())
        }
    }

    override fun onItemClickTransactionTxn(txnId: String, status: String) {
        viewModel.setProgressDialog(true, requireContext())
        viewModel.transactionTxnUpdate(txnId, status, requireContext())
    }

    override fun onItemClickTransactionChannel(channel: String, orderId: String) {
        viewModel.setProgressDialog(true, requireContext())
        viewModel.transactionChannelUpdate(channel, orderId, requireContext())
    }

    override fun onItemClickTransactionPos(updateStatusManualTxnRequest: UpdateStatusManualTxnRequest) {
        viewModel.setProgressDialog(true, requireContext())
        viewModel.transactionPosUpdate(updateStatusManualTxnRequest, requireContext())
    }
}
