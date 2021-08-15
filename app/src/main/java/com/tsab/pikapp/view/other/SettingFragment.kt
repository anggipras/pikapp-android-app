package com.tsab.pikapp.view.other

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tsab.pikapp.databinding.SettingFragmentBinding
import com.tsab.pikapp.view.loginv2.LoginRegisterActivity
import com.tsab.pikapp.viewmodel.other.SettingViewModel

class SettingFragment : Fragment() {

    private lateinit var dataBinding: SettingFragmentBinding
    private lateinit var viewModel: SettingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        dataBinding = SettingFragmentBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBinding.backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        dataBinding.logoutText.setOnClickListener {
            viewModel.logout()
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.logoutResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.clearSessionExclusive()
            }
        })

        viewModel.errorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            dataBinding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.activityToStart.observe(this, Observer { response ->
            response?.let {
                activity?.let {
                    val intent = Intent(it, LoginRegisterActivity::class.java)
                    it.startActivity(intent)
                }
            }
        })
    }
}