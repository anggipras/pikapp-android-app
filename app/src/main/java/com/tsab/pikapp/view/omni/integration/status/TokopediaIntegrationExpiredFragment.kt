package com.tsab.pikapp.view.omni.integration.status

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
import com.tsab.pikapp.databinding.FragmentIntegrationExpiredTokopediaBinding
import com.tsab.pikapp.models.model.Omnichannel
import com.tsab.pikapp.util.setAllOnClickListener
import com.tsab.pikapp.view.omni.integration.IntegrationActivity
import com.tsab.pikapp.viewmodel.omni.integration.IntegrationViewModel

class TokopediaIntegrationExpiredFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationExpiredTokopediaBinding
    private val viewModel: IntegrationViewModel by activityViewModels()

    private lateinit var omnichannel: Omnichannel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_integration_expired_tokopedia, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        omnichannel =
            arguments?.getSerializable(IntegrationActivity.ARGUMENT_OMNICHANNEL) as Omnichannel
        populateData()

        observeViewModel()
        attachInputListeners()
    }

    private fun populateData() {
        dataBinding.namaTokoDetailsText.text = omnichannel.name
        dataBinding.waktuIntegrasiDetailsText.text = omnichannel.createdAt
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            dataBinding.loadingOverlay.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        })
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigateUp()
        }, view)

        dataBinding.hapusTokoButton.setOnClickListener {
            //TODO: Implement hapus toko button.
        }

        dataBinding.integrasiUlangButton.setOnClickListener {
            //TODO: Implement integrasi ulang button.
        }
    }
}