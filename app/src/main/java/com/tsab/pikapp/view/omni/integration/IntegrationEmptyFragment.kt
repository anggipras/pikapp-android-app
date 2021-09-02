package com.tsab.pikapp.view.omni.integration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationEmptyBinding
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel

class IntegrationEmptyFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationEmptyBinding
    private val viewModel: IntegrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_integration_empty, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        observeViewModel()
        attachInputListeners()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.integrationList.observe(viewLifecycleOwner, Observer { integrationList ->
            if (integrationList.isNotEmpty()) {
                navController.navigate(R.id.action_integrationEmptyFragment_to_integrationHomeFragment)
            }
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            activity?.finish()
        }, view)

        dataBinding.tambahTokoButton.setOnClickListener {
            //TODO: Navigate to the add connection fragment.
        }
    }
}