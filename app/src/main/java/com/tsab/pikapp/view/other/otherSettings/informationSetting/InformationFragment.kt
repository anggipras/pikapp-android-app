package com.tsab.pikapp.view.other.otherSettings.informationSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.InformationFragmentBinding
import com.tsab.pikapp.viewmodel.other.otherSettings.informationSetting.InformationViewModel

class InformationFragment : Fragment() {

    private lateinit var dataBinding: InformationFragmentBinding
    private lateinit var viewModel: InformationViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(InformationViewModel::class.java)
        dataBinding = InformationFragmentBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.saveInformationButton.setOnClickListener {
            dataBinding.informationBanner.alpha = 1.toFloat()
            dataBinding.informationBannerIcChange.visibility = View.GONE
            dataBinding.informationImg.alpha = 1.toFloat()
            dataBinding.informationImgIcChange.visibility = View.GONE
            dataBinding.saveInformationButton.visibility = View.GONE
            dataBinding.saveInformationBack.visibility = View.VISIBLE
        }

        dataBinding.saveInformationBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.backButtonInformation.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

}