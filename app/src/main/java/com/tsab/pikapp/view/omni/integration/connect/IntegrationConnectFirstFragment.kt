package com.tsab.pikapp.view.omni.integration.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationConnectFirstBinding
import com.tsab.pikapp.util.setAllOnClickListener

class IntegrationConnectFirstFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var dataBinding: FragmentIntegrationConnectFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_integration_connect_first, container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.headerLayout.backButton.setAllOnClickListener(View.OnClickListener {
            navController.navigateUp()
        }, view)

        dataBinding.tokopediaButton.setAllOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_integrationConnectFirstFragment_to_integrationTokopediaConnectSecondFragment)
        }, view)
    }
}