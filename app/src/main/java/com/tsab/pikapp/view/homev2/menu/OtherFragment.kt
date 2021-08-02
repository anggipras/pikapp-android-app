package com.tsab.pikapp.view.homev2.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.OtherFragmentBinding
import com.tsab.pikapp.view.other.OtherActivity
import com.tsab.pikapp.viewmodel.homev2.OtherViewModel
import kotlinx.android.synthetic.main.other_fragment.*

class OtherFragment : Fragment() {

    private lateinit var dataBinding: OtherFragmentBinding
    private lateinit var viewModel: OtherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.other_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(OtherViewModel::class.java)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMerchantProfile()
        viewModel.showMerchantProfile()
        dataBinding.merchantProfile = viewModel

        dataBinding.merchantSettingClick.setOnClickListener {
            activity?.let {
                val intent = Intent(it, OtherActivity::class.java)
                it.startActivity(intent)
            }
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.merchantResult.observe(this, Observer { merchantProfile ->
            merchantProfile?.let {
                dataBinding.merchantName.text = merchantProfile.merchantName.toString()
                Picasso.get().load(merchantProfile.merchantLogo).into(merchant_logo)
            }
        })
    }

}