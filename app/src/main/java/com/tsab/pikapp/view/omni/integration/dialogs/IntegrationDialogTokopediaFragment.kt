package com.tsab.pikapp.view.omni.integration.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentIntegrationDialogTokopediaBinding

class IntegrationDialogTokopediaFragment : DialogFragment() {
    private lateinit var dataBinding: FragmentIntegrationDialogTokopediaBinding
    private var navController: NavController? = null

    override fun getTheme(): Int = R.style.NoMarginsDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_integration_dialog_tokopedia,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        navController = parentFragment?.view?.let { Navigation.findNavController(it) }
        dataBinding.closeButton.setOnClickListener {
            navController?.navigateUp()
        }
    }
}