package com.tsab.pikapp.view.omni.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationListBinding
import com.tsab.pikapp.models.model.OmnichannelStatus
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel

class IntegrationListFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationListBinding
    private val viewModel: IntegrationViewModel by activityViewModels()

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

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {

    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            activity?.finish()
        }, view)

        setupStatusSelectionSpinner()

        dataBinding.tambahTokoButton.setOnClickListener {
            navController.navigate(R.id.action_integrationListFragment_to_integrationConnectFirstFragment)
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
}