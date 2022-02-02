package com.tsab.pikapp.view.other.otherSettings

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.SettingFragmentBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.loginv2.LoginRegisterActivity
import com.tsab.pikapp.viewmodel.other.otherSettings.SettingViewModel

class SettingFragment : Fragment() {

    private lateinit var dataBinding: SettingFragmentBinding
    private lateinit var viewModel: SettingViewModel
    private val sessionManager = SessionManager()

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

        sessionManager.setHomeNav(3)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                    activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
                }
            })

        dataBinding.informationSetting.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_informationFragment) }
        dataBinding.profilSetting.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_profileFragment) }
        dataBinding.pinSetting.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_currentPinFragment) }
        dataBinding.openHourSetting.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_shopManagementFragment) }
        dataBinding.accountNumber.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_dataBankFragment) }
        dataBinding.shippingSetting.setOnClickListener { Navigation.findNavController(view).navigate(R.id.navigateTo_merchantShippingFragment) }

        dataBinding.headerInsideSettings.headerTitle.text = "Pengaturan"
        dataBinding.headerInsideSettings.backImage.setOnClickListener {
            activity?.finish()
            activity?.overridePendingTransition(R.anim.no_animation, R.anim.slide_down)
        }

        dataBinding.logoutText.setOnClickListener {
            viewModel.logout()
        }

        observeViewModel()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {
        viewModel.logoutResponse.observe(this, { response ->
            response?.let {
                viewModel.clearSessionExclusive()
            }
        })

        viewModel.errorResponse.observe(this, { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })

        viewModel.loading.observe(this, { isLoading ->
            dataBinding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.activityToStart.observe(this, { response ->
            response?.let {
                activity?.let {
                    val intent = Intent(it, LoginRegisterActivity::class.java)
                    it.startActivity(intent)
                    activity?.finish()
                }
            }
        })
    }
}