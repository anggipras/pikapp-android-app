package com.bejohen.pikapp.view.onboarding.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bejohen.pikapp.R
import com.bejohen.pikapp.databinding.FragmentSignupOnboardingBinding
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.viewmodel.onboarding.login.SignupOnboardingViewModel


class SignupOnboardingFragment : Fragment() {

    private lateinit var viewModel: SignupOnboardingViewModel
    private lateinit var dataBinding: FragmentSignupOnboardingBinding

    private var isEmailValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_onboarding, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = ViewModelProviders.of(this).get(SignupOnboardingViewModel::class.java)

        dataBinding.buttonSignupSignup.setOnClickListener {
            if (isEmailValid) {
//                sendRegister()
                registerSuccess()
            } else {
                val email = dataBinding.textFieldSignupEmail.text.toString().trim()
                Log.d("Debug","email : " + email)
                viewModel.emailValidation(email)

            }

        }
        observeViewModel()
    }

    private fun sendRegister() {
        val fullName = dataBinding.textFieldSignupFullName.text.toString().trim()
        val phone = dataBinding.textFieldSignupPhone.text.toString().trim()
        val password = dataBinding.textFieldSignupPassword.text.toString().trim()
        val confPassword = dataBinding.textErrorSignupConfirmPassword.text.toString().trim()

        viewModel.register(fullName, phone, password, confPassword)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.emailValid.observe(this, Observer {isValid ->
            isValid?.let {
                if(it) {
                    (activity as OnboardingActivity).hideKeyboard()
                    animateGone()
                    Handler().postDelayed({
                        animateUI()
                    }, 500)
                    Log.d("Debug","email harusnya : " + viewModel.email)
                    dataBinding.textEmailValid.text = viewModel.email
                    isEmailValid = true
                } else {
                    dataBinding.textErrorSignupEmail.visibility = View.VISIBLE
                }
            }

        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.registerResponse.observe(this, Observer {response ->
            response?.let {
                val action = SignupOnboardingFragmentDirections.actionToSignupOnboardingSuccessFragment()
                Navigation.findNavController(dataBinding.root).navigate(action)
            }
        })
    }

    private fun registerSuccess() {
        val action = SignupOnboardingFragmentDirections.actionToSignupOnboardingSuccessFragment()
        Navigation.findNavController(dataBinding.root).navigate(action)
    }

    private fun animateUI() {

        val transition = Fade()
        transition.duration = 800

        transition.addTarget(dataBinding.textYourEmail)
        transition.addTarget(dataBinding.textEmailValid)

        transition.addTarget(dataBinding.textSignupFullName)
        transition.addTarget(dataBinding.textFieldSignupFullName)
        transition.addTarget(dataBinding.textErrorFullName)
        transition.addTarget(dataBinding.textSignupPhone)
        transition.addTarget(dataBinding.textFieldSignupPhone)
        transition.addTarget(dataBinding.textErrorPhone)

        transition.addTarget(dataBinding.textSignupPassword)
        transition.addTarget(dataBinding.textFieldSignupPassword)
        transition.addTarget(dataBinding.textErrorSignupPassword)
        transition.addTarget(dataBinding.textSignupConfirmPassword)
        transition.addTarget(dataBinding.textFieldSignupConfirmPassword)
        transition.addTarget(dataBinding.textErrorSignupConfirmPassword)
        transition.addTarget(dataBinding.buttonSignupSignup)
        transition.addTarget(dataBinding.textOr)
        transition.addTarget(dataBinding.buttonSignupAnotherWay)

        TransitionManager.beginDelayedTransition(dataBinding.container,transition)

        //set visible
        dataBinding.textYourEmail.setTransitionVisibility(View.VISIBLE)
        dataBinding.textEmailValid.setTransitionVisibility(View.VISIBLE)
        dataBinding.textSignupFullName.setTransitionVisibility(View.VISIBLE)
        dataBinding.textFieldSignupFullName.setTransitionVisibility(View.VISIBLE)

        dataBinding.textSignupPhone.setTransitionVisibility(View.VISIBLE)
        dataBinding.textFieldSignupPhone.setTransitionVisibility(View.VISIBLE)

        dataBinding.textSignupPassword.setTransitionVisibility(View.VISIBLE)
        dataBinding.textFieldSignupPassword.setTransitionVisibility(View.VISIBLE)
        dataBinding.textSignupConfirmPassword.setTransitionVisibility(View.VISIBLE)
        dataBinding.textFieldSignupConfirmPassword.setTransitionVisibility(View.VISIBLE)
        dataBinding.buttonSignupSignup.setTransitionVisibility(View.VISIBLE)

        //set invisible
        dataBinding.textErrorFullName.setTransitionVisibility(View.INVISIBLE)
        dataBinding.textErrorPhone.setTransitionVisibility(View.INVISIBLE)
        dataBinding.textErrorSignupPassword.setTransitionVisibility(View.INVISIBLE)
        dataBinding.textErrorSignupConfirmPassword.setTransitionVisibility(View.INVISIBLE)
    }

    private fun animateGone() {
        dataBinding.container.transitionToEnd()
        val transition = Fade()
        transition.duration = 500

        transition.addTarget(dataBinding.textSignupEmail)
        transition.addTarget(dataBinding.textFieldSignupEmail)
        transition.addTarget(dataBinding.textErrorSignupEmail)

        transition.addTarget(dataBinding.buttonSignupSignup)

        TransitionManager.beginDelayedTransition(dataBinding.container,transition)

        //set gone
        dataBinding.textSignupEmail.setTransitionVisibility(View.GONE)
        dataBinding.textFieldSignupEmail.setTransitionVisibility(View.GONE)
        dataBinding.textErrorSignupEmail.setTransitionVisibility(View.GONE)
        dataBinding.textOr.setTransitionVisibility(View.GONE)
        dataBinding.buttonSignupAnotherWay.setTransitionVisibility(View.GONE)
        dataBinding.buttonSignupSignup.setTransitionVisibility(View.INVISIBLE)

    }


}