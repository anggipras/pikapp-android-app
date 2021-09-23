package com.tsab.pikapp.view.omni.integration

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationListBinding
import com.tsab.pikapp.models.model.Omnichannel
import com.tsab.pikapp.models.model.OmnichannelStatus
import com.tsab.pikapp.receiver.AlarmReceiver
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.homev2.HomeNavigation
import com.tsab.pikapp.view.omni.integration.IntegrationActivity.Companion.ARGUMENT_OMNICHANNEL
import com.tsab.pikapp.view.omni.integration.lists.IntegrationListAdapter
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel
import java.util.*

class IntegrationListFragment : Fragment() {
    private val viewModel: IntegrationViewModel by activityViewModels()

    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationListBinding
    private val sessionManager = SessionManager()

    private lateinit var integrationListAdapter: IntegrationListAdapter
    private var alarmMgr : AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_integration_list, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        sessionManager.setHomeNav(3)

        observeViewModel()
        attachInputListeners()
        onBackPressed()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Intent(activity?.baseContext, HomeNavigation::class.java).apply {
                    startActivity(this)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.selectedList.observe(viewLifecycleOwner, Observer { selectedList ->
            integrationListAdapter.setIntegrationList(selectedList.toMutableList())
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.integrationSwipeContainer.isRefreshing = isLoading
        })

        viewModel.tokpedListSize.observe(viewLifecycleOwner, Observer { size ->
            if (size < 1) cancelAlarm()
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            Intent(activity?.baseContext, HomeNavigation::class.java).apply {
                startActivity(this)
            }
        }, view)

        setupStatusSelectionSpinner()
        setupRecyclerView()

        dataBinding.tambahTokoButton.setOnClickListener {
            navController.navigate(R.id.action_integrationListFragment_to_integrationConnectFirstFragment)
        }

        dataBinding.integrationSwipeContainer.setOnRefreshListener {
            viewModel.fetchIntegrationList()
        }
    }

    private fun setupStatusSelectionSpinner() {
        ArrayAdapter.createFromResource(
            activity?.baseContext!!,
            R.array.oi_status_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dataBinding.statusSelectSpinner.adapter = adapter
        }

        dataBinding.statusSelectSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?,
                position: Int, id: Long
            ) {
                when (position) {
                    0 -> viewModel.setSelectedStatus(null)
                    1 -> viewModel.setSelectedStatus(OmnichannelStatus.ON_PROGRESS)
                    2 -> viewModel.setSelectedStatus(OmnichannelStatus.CONNECTED)
                    3 -> viewModel.setSelectedStatus(OmnichannelStatus.EXPIRED)
                    else -> viewModel.setSelectedStatus(null)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.setSelectedStatus(null)
            }
        }
    }

    private fun setupRecyclerView() {
        integrationListAdapter = IntegrationListAdapter(
            viewModel.selectedList.value!!.toMutableList(),
            object : IntegrationListAdapter.OnItemClickListener {
                override fun onItemClick(omnichannel: Omnichannel) {
                    val bundle = bundleOf(ARGUMENT_OMNICHANNEL to omnichannel)
                    if (omnichannel.status == OmnichannelStatus.CONNECTED) {
                        navController.navigate(
                            R.id.action_integrationListFragment_to_tokopediaIntegrationConnectedFragment,
                            bundle
                        )
                    } else {
                        navController.navigate(
                            R.id.action_integrationListFragment_to_tokopediaIntegrationPendingFragment,
                            bundle
                        )
                    }
                }
            }
        )

        dataBinding.integrationRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = integrationListAdapter
        }
    }

    private fun cancelAlarm() {
        alarmMgr = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(requireActivity(), AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(requireActivity(), 0, intent, 0)
        }

        alarmMgr!!.cancel(alarmIntent)
    }
}