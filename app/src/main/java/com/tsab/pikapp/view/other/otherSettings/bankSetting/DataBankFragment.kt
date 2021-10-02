package com.tsab.pikapp.view.other.otherSettings.bankSetting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentDataBankBinding
import com.tsab.pikapp.viewmodel.other.OtherSettingViewModel

class DataBankFragment : Fragment() {

    private val viewModel: OtherSettingViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentDataBankBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data_bank, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProfileDetail()
        dataBinding.merchantProfileDetail = viewModel

        dataBinding.btnNext.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_dataBankFragment_to_pinBankFragment)
        }

        dataBinding.backButtonInformation.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_dataBankFragment_to_settingFragment)
        }
    }
}