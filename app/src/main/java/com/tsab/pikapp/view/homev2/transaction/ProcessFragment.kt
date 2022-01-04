package com.tsab.pikapp.view.homev2.transaction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentProccessBinding
import com.tsab.pikapp.models.model.UpdateStatusManualTxnRequest
import com.tsab.pikapp.services.OnlineService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.viewmodel.homev2.ManualTxnViewModel
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import timber.log.Timber

class ProcessFragment : Fragment(), TransactionListAdapter.OnItemClickListener, TransactionListV2Adapter.OnItemClickListener {
    private val viewModel: TransactionViewModel by activityViewModels()
    private val manualViewModel: ManualTxnViewModel by activityViewModels()
    private lateinit var layoutManagerTransaction: LinearLayoutManager
    private lateinit var layoutManagerTokopedia: LinearLayoutManager
    private lateinit var layoutManagerManualTxn: LinearLayoutManager

    private lateinit var layoutManagerTransactionV2: LinearLayoutManager
    private lateinit var recyclerAdapter: TransactionListV2Adapter

    private lateinit var dataBinding: FragmentProccessBinding
    private val sessionManager = SessionManager()

    private val filterSheet = FilterFragment()
    var broadcastManager: LocalBroadcastManager? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val actionReceiver = IntentFilter()
        actionReceiver.addAction("receivedTransaction")

        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        broadcastManager!!.registerReceiver(mMessageReceiver, actionReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_proccess,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.mutableCountTxn.value = 0
        dataBinding.recyclerviewManualTxn.visibility = View.VISIBLE

        layoutManagerTransaction =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewTransaction.setHasFixedSize(true)
        dataBinding.recyclerviewTransaction.layoutManager = layoutManagerTransaction

        layoutManagerTokopedia =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewTokopedia.layoutManager = layoutManagerTokopedia

        layoutManagerManualTxn =
            LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        dataBinding.recyclerviewManualTxn.setHasFixedSize(true)
        dataBinding.recyclerviewManualTxn.layoutManager = layoutManagerManualTxn

        initRecyclerView()
        initViewModel()

        getProcessData()

        general_error_process.try_button.setOnClickListener {
            getProcessData()
        }

        buttonFilterPikapp.setOnClickListener {
            if (!viewModel.mutablePikappFilter.value!!) {
                viewModel.mutableCountTxn.value = viewModel.proses.value!!.toInt()

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                if (!viewModel.mutableTokpedFilter.value!!) {
                    viewModel.mutablePikappFilter.value = true
                    recyclerview_transaction.visibility = View.VISIBLE
                    recyclerview_tokopedia.visibility = View.GONE
                    buttonFilterPikapp.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterPikapp.setTextColor(Color.parseColor("#ffffff"))
                }
                viewModel.mutablePikappFilter.value = true
                recyclerview_transaction.visibility = View.VISIBLE
                buttonFilterPikapp.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterPikapp.setTextColor(Color.parseColor("#ffffff"))
            } else if (viewModel.mutablePikappFilter.value!!) {
                if (!viewModel.mutableTokpedFilter.value!! && !viewModel.mutableGrabFilter.value!! && !viewModel.mutableShopeeFilter.value!!) {
                    viewModel.mutableCountTxn.value =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    recyclerview_transaction.visibility = View.VISIBLE
                    recyclerview_tokopedia.visibility = View.VISIBLE
                } else {
                    viewModel.mutableCountTxn.value =
                        viewModel.mutableCountTxn.value!! - viewModel.proses.value!!.toInt()
                    recyclerview_transaction.visibility = View.GONE
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutablePikappFilter.value = false
                buttonFilterPikapp.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterPikapp.setTextColor(Color.parseColor("#aaaaaa"))
            }
        }

        buttonFilterTokped.setOnClickListener {
            if (!viewModel.mutableTokpedFilter.value!!) {
                viewModel.mutableCountTxn.value = viewModel.prosesOmni.value!!.toInt()

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                if (!viewModel.mutablePikappFilter.value!!) {
                    viewModel.mutableTokpedFilter.value = true
                    recyclerview_transaction.visibility = View.GONE
                    recyclerview_tokopedia.visibility = View.VISIBLE
                    buttonFilterTokped.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterTokped.setTextColor(Color.parseColor("#ffffff"))
                }
                viewModel.mutableTokpedFilter.value = true
                recyclerview_tokopedia.visibility = View.VISIBLE
                buttonFilterTokped.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterTokped.setTextColor(Color.parseColor("#ffffff"))
            } else if (viewModel.mutableTokpedFilter.value!!) {
                if (!viewModel.mutablePikappFilter.value!! && !viewModel.mutableGrabFilter.value!! && !viewModel.mutableShopeeFilter.value!!) {
                    viewModel.mutableCountTxn.value =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    recyclerview_transaction.visibility = View.VISIBLE
                    recyclerview_tokopedia.visibility = View.VISIBLE
                } else {
                    viewModel.mutableCountTxn.value =
                        viewModel.mutableCountTxn.value!! - viewModel.prosesOmni.value!!.toInt()
                    recyclerview_tokopedia.visibility = View.GONE
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutableTokpedFilter.value = false
                buttonFilterTokped.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterTokped.setTextColor(Color.parseColor("#aaaaaa"))
            }
        }

        buttonFilterGrab.setOnClickListener {
            if (!viewModel.mutableGrabFilter.value!!) {
                if (!viewModel.mutablePikappFilter.value!! && !viewModel.mutableTokpedFilter.value!!) {
                    viewModel.mutableCountTxn.value = 0
                    viewModel.mutableGrabFilter.value = true
                    recyclerview_transaction.visibility = View.GONE
                    recyclerview_tokopedia.visibility = View.GONE
                    buttonFilterGrab.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterGrab.setTextColor(Color.parseColor("#ffffff"))
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutableGrabFilter.value = true
                buttonFilterGrab.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterGrab.setTextColor(Color.parseColor("#ffffff"))
            } else if (viewModel.mutableGrabFilter.value!!) {
                if (!viewModel.mutablePikappFilter.value!! && !viewModel.mutableTokpedFilter.value!! && !viewModel.mutableShopeeFilter.value!!) {
                    viewModel.mutableCountTxn.value =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    recyclerview_transaction.visibility = View.VISIBLE
                    recyclerview_tokopedia.visibility = View.VISIBLE
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutableGrabFilter.value = false
                buttonFilterGrab.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterGrab.setTextColor(Color.parseColor("#aaaaaa"))
            }
        }

        buttonFilterShopee.setOnClickListener {
            if (!viewModel.mutableShopeeFilter.value!!) {
                if (!viewModel.mutablePikappFilter.value!! && !viewModel.mutableTokpedFilter.value!!) {
                    viewModel.mutableCountTxn.value = 0
                    viewModel.mutableShopeeFilter.value = true
                    recyclerview_transaction.visibility = View.GONE
                    recyclerview_tokopedia.visibility = View.GONE
                    buttonFilterShopee.setBackgroundResource(R.drawable.button_green_square)
                    buttonFilterShopee.setTextColor(Color.parseColor("#ffffff"))
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutableShopeeFilter.value = true
                buttonFilterShopee.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterShopee.setTextColor(Color.parseColor("#ffffff"))
            } else if (viewModel.mutableShopeeFilter.value == true) {
                if (!viewModel.mutablePikappFilter.value!! && !viewModel.mutableTokpedFilter.value!! && !viewModel.mutableGrabFilter.value!!) {
                    viewModel.mutableCountTxn.value =
                        viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
                    recyclerview_transaction.visibility = View.VISIBLE
                    recyclerview_tokopedia.visibility = View.VISIBLE
                }

                if (viewModel.mutableCountTxn.value == 0) {
                    icon.visibility = View.VISIBLE
                    text.visibility = View.VISIBLE
                } else {
                    icon.visibility = View.GONE
                    text.visibility = View.GONE
                }

                viewModel.mutableShopeeFilter.value = false
                buttonFilterShopee.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterShopee.setTextColor(Color.parseColor("#aaaaaa"))
            }
        }

        buttonFilterCount.setOnClickListener {
            if (viewModel.mutablePikappFilter.value == false && viewModel.mutableGrabFilter.value == false
                && viewModel.mutableShopeeFilter.value == false && viewModel.mutableTokpedFilter.value == false
            ) {
                viewModel.mutableCountTxn.value =
                    viewModel.proses.value!!.toInt() + viewModel.prosesOmni.value!!.toInt()
            }
            filterSheet.show(requireActivity().supportFragmentManager, "show")
        }
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllTransactionProcess.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionListV2Adapter(requireContext(), requireActivity(), requireActivity().supportFragmentManager, this)
        dataBinding.recyclerviewAllTransactionProcess.adapter = recyclerAdapter
    }

    private fun initViewModel() {
        viewModel.getLiveDataTransListV2ProcessObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.setLoading(false)
                recyclerAdapter.setTransactionList(it)
                recyclerAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun getProcessData() {
        val onlineService = OnlineService()
        if (onlineService.isOnline(context)) {
            activity?.let {
                viewModel.getStoreOrderList(
                    it.baseContext,
                    recyclerview_transaction,
                    "Proses",
                    requireActivity().supportFragmentManager,
                    emptyState,
                    this,
                    requireActivity(),
                    general_error_process
                )
            }

            activity?.let {
                viewModel.getListOmni(
                    it.baseContext,
                    recyclerview_tokopedia,
                    requireActivity().supportFragmentManager,
                    requireActivity(),
                    "Proses",
                    emptyState,
                    requireParentFragment(),
                    general_error_process
                )
            }

            activity?.let { manualViewModel.getManualTxnList("ON_PROCESS", it.baseContext, recyclerview_manualTxn, requireActivity()) }

            viewModel.editList(
                recyclerview_transaction,
                recyclerview_tokopedia,
                buttonFilterPikapp,
                buttonFilterTokped,
                buttonFilterGrab,
                buttonFilterShopee,
                icon,
                text
            )

            /* TRANSACTION LIST V2 START FROM HERE */
            viewModel.getTransactionV2List(requireContext(), "sample_response_txn.json")

            general_error_process.isVisible = false
        } else {
            general_error_process.isVisible = true
            viewModel.setLoading(false)
            onlineService.networkDialog(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.errCode.observe(viewLifecycleOwner, Observer { errCode ->
            Timber.tag(javaClass.simpleName).e(errCode)

            if (errCode == "EC0032" || errCode == "EC0021" || errCode == "EC0017") {
                sessionManager.logout()
                Intent(activity?.baseContext, LoginV2Activity::class.java).apply {
                    activity?.startActivity(this)
                }
            }
        })

        viewModel.processBadges.observe(viewLifecycleOwner, Observer { amount ->
            amount?.let {
                dataBinding.icon.isVisible = it == 0
                dataBinding.text.isVisible = it == 0
            }
        })

        viewModel.decreaseBadge.observe(viewLifecycleOwner, Observer { amount ->
            amount?.let {
                dataBinding.icon.isVisible = it == 0
                dataBinding.text.isVisible = it == 0
            }
        })

        manualViewModel.emptyList.observe(viewLifecycleOwner, Observer { state ->
            dataBinding.icon.visibility = if (state) View.VISIBLE else View.GONE
            dataBinding.text.visibility = if (state) View.VISIBLE else View.GONE
        })
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            layoutManagerTransaction =
                LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
            dataBinding.recyclerviewTransaction.setHasFixedSize(true)
            dataBinding.recyclerviewTransaction.layoutManager = layoutManagerTransaction
        }
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && context != null) {
                viewModel.getStoreOrderList(
                    context,
                    recyclerview_transaction,
                    "Proses",
                    requireActivity().supportFragmentManager,
                    emptyState,
                    this@ProcessFragment,
                    requireActivity(),
                    general_error_process
                )
                viewModel.getListOmni(
                    context,
                    recyclerview_tokopedia,
                    requireActivity().supportFragmentManager,
                    requireActivity(),
                    "Proses",
                    emptyState,
                    requireParentFragment(),
                    general_error_process
                )
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        broadcastManager?.unregisterReceiver(mMessageReceiver)
    }

    override fun onItemClick(i: Int) {
        viewModel.setDecreaseBadge(i)
    }

    override fun onItemClickTransactionTxn(txnId: String, status: String) {
        viewModel.setLoading(true)
        Handler().postDelayed({
            viewModel.transactionTxnUpdateDummy(txnId, status, requireContext())
        }, 3000)
    }

    override fun onItemClickTransactionChannel(channel: String, orderId: String) {
        viewModel.setLoading(true)
        Handler().postDelayed({
            viewModel.transactionChannelUpdateDummy(channel, orderId, requireContext())
        }, 3000)
    }

    override fun onItemClickTransactionPos(updateStatusManualTxnRequest: UpdateStatusManualTxnRequest) {
        viewModel.setLoading(true)
        Handler().postDelayed({
            viewModel.transactionPosUpdateDummy(updateStatusManualTxnRequest, requireContext())
        }, 3000)
    }
}
