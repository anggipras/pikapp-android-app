package com.tsab.pikapp.view.homev2.menu

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.OtherFragmentBinding
import com.tsab.pikapp.viewmodel.homev2.OtherViewModel

class OtherFragment : Fragment() {

    private lateinit var dataBinding: OtherFragmentBinding
    private lateinit var viewModel: OtherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(OtherViewModel::class.java)
        dataBinding = OtherFragmentBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMerchantProfile()

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.merchantResult.observe(this, Observer { merchantProfile ->
            merchantProfile?.let {
                dataBinding.merchantName.text = merchantProfile.merchantName.toString()
            }
        })
    }

}