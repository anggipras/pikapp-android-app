package com.tsab.pikapp.view.homev2.transaction

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
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
import com.tsab.pikapp.viewmodel.homev2.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_proccess.*
import kotlinx.android.synthetic.main.layout_page_problem.view.*
import timber.log.Timber
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class ProcessFragment : Fragment(), TransactionListV2Adapter.OnItemClickListener {
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var recyclerAdapter: TransactionListV2Adapter
    private lateinit var dataBinding: FragmentProccessBinding
    private val sessionManager = SessionManager()
    private val filterSheet = FilterFragment()
    var broadcastManager: LocalBroadcastManager? = null
    private val onlineService = OnlineService()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val actionReceiver = IntentFilter()
        actionReceiver.addAction("receivedTransaction")

        broadcastManager = LocalBroadcastManager.getInstance(requireContext())
        broadcastManager!!.registerReceiver(mMessageReceiver, actionReceiver)
    }

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && context != null) {
                viewModel.getTransactionV2List(requireContext(), true)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        broadcastManager?.unregisterReceiver(mMessageReceiver)
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

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initViewModel()

        getProcessData()

        general_error_process.try_button.setOnClickListener {
            getProcessData()
        }

        buttonFilterPikapp.setOnClickListener {
            if (!viewModel.mutablePikappFilter.value!!) {
                viewModel.mutablePikappFilter.value = true
                buttonFilterPikapp.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterPikapp.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("TXN", "TXN", true)
            } else if (viewModel.mutablePikappFilter.value!!) {
                viewModel.mutablePikappFilter.value = false
                buttonFilterPikapp.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterPikapp.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("TXN", "TXN", false)
            }
        }

        buttonFilterTokped.setOnClickListener {
            if (!viewModel.mutableTokpedFilter.value!!) {
                viewModel.mutableTokpedFilter.value = true
                buttonFilterTokped.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterTokped.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("CHANNEL", "TOKOPEDIA", true)
            } else if (viewModel.mutableTokpedFilter.value!!) {
                viewModel.mutableTokpedFilter.value = false
                buttonFilterTokped.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterTokped.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("CHANNEL", "TOKOPEDIA", false)
            }
        }

        buttonFilterGrab.setOnClickListener {
            if (!viewModel.mutableGrabFilter.value!!) {
                viewModel.mutableGrabFilter.value = true
                buttonFilterGrab.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterGrab.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("CHANNEL", "GRAB", true)
            } else if (viewModel.mutableGrabFilter.value!!) {
                viewModel.mutableGrabFilter.value = false
                buttonFilterGrab.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterGrab.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("CHANNEL", "GRAB", false)
            }
        }

        buttonFilterInsta.setOnClickListener {
            if (!viewModel.mutableShopeeFilter.value!!) {
                viewModel.mutableShopeeFilter.value = true
                buttonFilterInsta.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterInsta.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("POS", "INSTAGRAM", true)
            } else if (viewModel.mutableShopeeFilter.value == true) {
                viewModel.mutableShopeeFilter.value = false
                buttonFilterInsta.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterInsta.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("POS", "INSTAGRAM", false)
            }
        }

        buttonFilterWhatsapp.setOnClickListener {
            if (!viewModel.mutableWhatsappFilter.value!!) {
                viewModel.mutableWhatsappFilter.value = true
                buttonFilterWhatsapp.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterWhatsapp.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("POS", "WHATSAPP", true)
            } else if (viewModel.mutableWhatsappFilter.value!!) {
                viewModel.mutableWhatsappFilter.value = false
                buttonFilterWhatsapp.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterWhatsapp.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("POS", "WHATSAPP", false)
            }
        }

        buttonFilterTelp.setOnClickListener {
            if (!viewModel.mutableTelpFilter.value!!) {
                viewModel.mutableTelpFilter.value = true
                buttonFilterTelp.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterTelp.setTextColor(Color.parseColor("#ffffff"))
                viewModel.filterOnProcess("POS", "PHONE_CALL", true)
            } else if (viewModel.mutableTelpFilter.value!!) {
                viewModel.mutableTelpFilter.value = false
                buttonFilterTelp.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterTelp.setTextColor(Color.parseColor("#aaaaaa"))
                viewModel.filterOnProcess("POS", "PHONE_CALL", false)
            }
        }

        buttonFilterCount.setOnClickListener {
            filterSheet.show(requireActivity().supportFragmentManager, "show")
        }
    }

    private fun initRecyclerView() {
        dataBinding.recyclerviewAllTransactionProcess.layoutManager = LinearLayoutManager(requireView().context, LinearLayoutManager.VERTICAL, false)
        recyclerAdapter = TransactionListV2Adapter(requireContext(), requireActivity(), requireActivity().supportFragmentManager,this)
        dataBinding.recyclerviewAllTransactionProcess.adapter = recyclerAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViewModel() {
        viewModel.getLiveDataTransListV2ProcessObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isNullOrEmpty()) {
                    dataBinding.emptyStateProcess.visibility = View.VISIBLE

                } else {
                    dataBinding.emptyStateProcess.visibility = View.GONE
                }
                recyclerAdapter.setTransactionList(it)
            }
        })

        viewModel.progressLoading.observe(viewLifecycleOwner, Observer { load ->
            if (load) {
                dataBinding.emptyStateProcess.visibility = View.GONE
                viewModel.setProgressLoading(false)
            }
        })

        viewModel.getLiveDataTransListV2ProcessFilterObserver().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.isNullOrEmpty()) {
                    dataBinding.emptyStateProcess.visibility = View.VISIBLE
                } else {
                    dataBinding.emptyStateProcess.visibility = View.GONE
                }
                recyclerAdapter.setTransactionList(it)
            }
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

        viewModel.allFilterColor.observe(viewLifecycleOwner, Observer {
            val drawable: Drawable = buttonFilterCount.context.resources.getDrawable(R.drawable.ic_filter)
            if (it) {
                buttonFilterCount.setBackgroundResource(R.drawable.button_green_square)
                buttonFilterCount.setTextColor(Color.parseColor("#ffffff"))

                drawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
                buttonFilterCount.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            } else {
                buttonFilterCount.setBackgroundResource(R.drawable.gray_square_btn)
                buttonFilterCount.setTextColor(Color.parseColor("#aaaaaa"))

                drawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.borderSubtle), PorterDuff.Mode.SRC_IN);
                buttonFilterCount.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        })

        viewModel.errorLoading.observe(viewLifecycleOwner, Observer { error ->
            if (error) {
                general_error_process.isVisible = true
                onlineService.serviceDialog(requireActivity())
            } else {
                general_error_process.isVisible = false
            }
        })
    }

    private fun getProcessData() {
        if (onlineService.isOnline(context)) {
            viewModel.getTransactionV2List(requireContext(), true)
            general_error_process.isVisible = false
        } else {
            general_error_process.isVisible = true
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
