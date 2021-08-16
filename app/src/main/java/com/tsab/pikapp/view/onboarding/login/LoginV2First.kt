package com.tsab.pikapp.view.onboarding.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentLoginV2FirstBinding
import com.tsab.pikapp.viewmodel.onboarding.login.LoginOnboardingViewModelV2
import kotlinx.android.synthetic.main.fragment_login_v2_first.*

var navController: NavController? = null

class LoginV2First : Fragment(){
    private lateinit var viewModel: LoginOnboardingViewModelV2
    private lateinit var dataBinding: FragmentLoginV2FirstBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_v2_first,
                container, false)

        return dataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginOnboardingViewModelV2::class.java)
        navController = Navigation.findNavController(view)

        dataBinding.privacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://pikapp.id/ketentuan-dan-kebijakan-privasi/"))
            startActivity(intent)
        }

        dataBinding.persyaratan.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://pikapp.id/ketentuan-dan-kebijakan-privasi/"))
            startActivity(intent)
        }

        observeViewModel()
        attachInputListeners()
    }

    private fun attachInputListeners() {
        dataBinding.nextBtn.setOnClickListener {
            viewModel.validateEmail(dataBinding.logPhone.text.toString())
            if(viewModel.isEmailValid == true){
                val bundle = bundleOf("email" to logPhone.text.toString())
                navController!!.navigate(R.id.action_loginV2First_to_loginV2Second, bundle)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.emailError.observe(viewLifecycleOwner, Observer { emailError ->
            dataBinding.emailerror.text = if (emailError.isEmpty()) "" else emailError
        })
    }
}