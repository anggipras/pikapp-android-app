package com.tsab.pikapp.view.homev2.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentCancelBinding
import com.tsab.pikapp.models.model.UpdateStatusManualTxnRequest
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_cancel.*
import kotlinx.android.synthetic.main.layout_page_problem.view.*

class CancelFragment : Fragment(), TransactionListV2Adapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentCancelBinding
    private lateinit var recyclerAdapter: TransactionListV2Adapter
    private val onlineService = OnlineService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_cancel,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.mutableFinishPageStateCancel.value = false

        initRecyclerView()
        initViewModel()

        general_error_cancel.try_button.setOnClickListener {
            getDataCancel()
        }

//        dataBinding.nestedScrollCancel.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
//            // on scroll change we are checking when users scroll as bottom.
//            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
//                if (!viewModel.finishPageStateCancel.value!!) {
//                    // in this method we are incrementing page number,
//                    // making progress bar visible and calling get data method.
//                    val pageCancelAct = viewModel.pageCancel.value!! + 1
//                    dataBinding.loadingPB.isVisible = true
//                    viewModel.getCancelTransactionV2PaginationList(pageCancelAct, dataBinding.loadingPB)
//                }
//            }
//        })
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllTransactionCancel.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionListV2Adapter(requireContext(), requireActivity(), requireActivity().supportFragmentManager,this)
        dataBinding.recyclerviewAllTransactionCancel.adapter = recyclerAdapter
    }

    private fun initViewModel() {
        viewModel.getLiveDataTransListV2CancelObserver().observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                dataBinding.emptyStateCancel.visibility = View.GONE
                recyclerAdapter.setTransactionList(it)
            } else {
                dataBinding.emptyStateCancel.visibility = View.VISIBLE
            }
        })

        viewModel.errorLoading.observe(viewLifecycleOwner, { error ->
            if (error) {
                general_error_cancel.isVisible = true
                onlineService.serviceDialog(requireActivity())
            } else {
                general_error_cancel.isVisible = false
            }
        })

        viewModel.emptyState.observe(viewLifecycleOwner, {
            if (it) {
                dataBinding.emptyStateCancel.visibility = View.VISIBLE
            } else {
                dataBinding.emptyStateCancel.visibility = View.GONE
            }
        })

        viewModel.tabPosition.observe(viewLifecycleOwner, {
            if (it == 2) {
                getDataCancel()
            }
        })
    }

    private fun getDataCancel() {
        if (onlineService.isOnline(context)) {
            viewModel.getCancelTransactionV2List(requireContext(), true, 0)
            general_error_cancel.isVisible = false
        } else {
            general_error_cancel.isVisible = true
            onlineService.networkDialog(requireActivity())
        }
    }

    override fun onItemClickTransactionTxn(txnId: String, status: String) {
//        viewModel.setProgressDialog(true, requireContext())
//        viewModel.transactionTxnUpdate(txnId, status, requireContext())
    }

    override fun onItemClickTransactionChannel(channel: String, orderId: String) {
//        viewModel.setProgressDialog(true, requireContext())
//        viewModel.transactionChannelUpdate(channel, orderId, requireContext())
    }

    override fun onItemClickTransactionPos(updateStatusManualTxnRequest: UpdateStatusManualTxnRequest) {
//        viewModel.setProgressDialog(true, requireContext())
//        viewModel.transactionPosUpdate(updateStatusManualTxnRequest, requireContext())
    }
}
