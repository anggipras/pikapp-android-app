package com.tsab.pikapp.view.homev2.transaction

import android.os.Bundle
import android.util.Log
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
import com.tsab.pikapp.databinding.FragmentCancelBinding
import com.tsab.pikapp.models.model.UpdateStatusManualTxnRequest
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.view.CustomProgressDialog
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_cancel.*
import kotlinx.android.synthetic.main.fragment_proccess.*
//import kotlinx.android.synthetic.main.fragment_cancel.recyclerview_manualTxn
//import kotlinx.android.synthetic.main.fragment_proccess.recyclerview_transaction
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import timber.log.Timber

class CancelFragment : Fragment(), TransactionListAdapter.OnItemClickListener, TransactionListV2Adapter.OnItemClickListener {

    private val viewModel: TransactionViewModel by activityViewModels()
    private val manualViewModel: ManualTxnViewModel by activityViewModels()
    lateinit var transactionListAdapter: TransactionListAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var linearLayoutManager1: LinearLayoutManager
    private lateinit var layoutManagerManualTxn: LinearLayoutManager
    private lateinit var dataBinding: FragmentCancelBinding
    private val progressDialog = CustomProgressDialog()
    private lateinit var recyclerAdapter: TransactionListV2Adapter

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

//        recyclerview_transaction.setHasFixedSize(true)
//        linearLayoutManager =
//            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
//        linearLayoutManager1 =
//            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
//        recyclerview_transaction.layoutManager = linearLayoutManager
//        recyclerview_tokopedia_cancel.layoutManager = linearLayoutManager1
//
//        layoutManagerManualTxn =
//            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
//        dataBinding.recyclerviewManualTxn.setHasFixedSize(true)
//        dataBinding.recyclerviewManualTxn.layoutManager = layoutManagerManualTxn

        initRecyclerView()
        initViewModel()

        getDataCancel()

        general_error_cancel.try_button.setOnClickListener {
            getDataCancel()
        }

        observeViewModel()
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllTransactionCancel.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionListV2Adapter(requireContext(), requireActivity(), requireActivity().supportFragmentManager, this)
        dataBinding.recyclerviewAllTransactionCancel.adapter = recyclerAdapter
    }

    private fun initViewModel() {
        viewModel.getLiveDataTransListV2CancelObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                dataBinding.emptyStateCancel.visibility = View.GONE
                recyclerAdapter.setTransactionList(it)
                recyclerAdapter.notifyDataSetChanged()
            } else {
                dataBinding.emptyStateCancel.visibility = View.VISIBLE
            }
        })

        viewModel.progressLoading.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                setProgressDialog(false)
                viewModel.setProgressLoading(false)
            }
        })
    }

    private fun getDataCancel() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
//            activity?.let {
//                viewModel.getStoreOrderList(
//                    it.baseContext,
//                    recyclerview_transaction,
//                    "Batal",
//                    requireActivity().supportFragmentManager,
//                    emptyStateCancel,
//                    this,
//                    requireActivity(),
//                    general_error_cancel
//                )
//            }
//            activity?.let {
//                viewModel.getListOmni(
//                    it.baseContext,
//                    recyclerview_tokopedia_cancel,
//                    requireActivity().supportFragmentManager,
//                    requireActivity(),
//                    "Batal",
//                    emptyStateCancel,
//                    requireParentFragment(),
//                    general_error_cancel
//                )
//            }
//
//            activity?.let { manualViewModel.getManualTxnList("CANCELLED", it.baseContext, recyclerview_manualTxn, requireActivity()) }

            /* TRANSACTION LIST V2 START FROM HERE */
            viewModel.getTransactionV2List(requireContext(), requireActivity(), false, general_error_cancel)

            general_error_cancel.isVisible = false
        } else {
            general_error_cancel.isVisible = true
            viewModel.setLoading(false)
            onlineService.networkDialog(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
//        dataBinding.emptyStateCancel.isVisible =
//            viewModel.batal.value == 0 && viewModel.batalOmni.value == 0
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        manualViewModel.emptyList.observe(viewLifecycleOwner, Observer { state ->
//            dataBinding.emptyStateCancel.visibility = if (state) View.VISIBLE else View.GONE
        })
    }

    private fun setProgressDialog(action: Boolean) {
        if (action) {
            progressDialog.show(requireContext())
        } else {
            progressDialog.dialog.dismiss()
        }
    }

    override fun onItemClick(i: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemClickTransactionTxn(txnId: String, status: String) {
        setProgressDialog(true)
        viewModel.transactionTxnUpdate(txnId, status, requireContext(), requireActivity(), general_error_cancel)
    }

    override fun onItemClickTransactionChannel(channel: String, orderId: String) {
        setProgressDialog(true)
        viewModel.transactionChannelUpdate(channel, orderId, requireContext(), requireActivity(), general_error_cancel)
    }

    override fun onItemClickTransactionPos(updateStatusManualTxnRequest: UpdateStatusManualTxnRequest) {
        setProgressDialog(true)
        viewModel.transactionPosUpdate(updateStatusManualTxnRequest, requireContext(), requireActivity(), general_error_cancel)
    }
}
