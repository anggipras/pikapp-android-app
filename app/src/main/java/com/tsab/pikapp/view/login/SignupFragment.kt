package com.tsab.pikapp.view.login

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
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.FragmentSignupBinding
import com.tsab.pikapp.view.LoginActivity
import com.tsab.pikapp.view.OnboardingActivity
import com.tsab.pikapp.viewmodel.onboarding.signup.SignupOnboardingViewModel

class SignupFragment : Fragment() {

    private lateinit var viewModel: SignupOnboardingViewModel
    private lateinit var dataBinding: FragmentSignupBinding

    private var isEmailValid = false

    private var shortAnimationDuration: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SignupOnboardingViewModel::class.java)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)

        dataBinding.buttonSignupSignup.setOnClickListener {
            if (isEmailValid) {
//                sendRegister()
                registerSuccess()
            } else {
                val email = dataBinding.textFieldSignupEmail.text.toString().trim()
                Log.d("Debug", "email : " + email)
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

        viewModel.register(fullName, phone, password)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.emailValid.observe(this, Observer { isValid ->
            isValid?.let {
                if (it) {
                    if (viewModel.getOnboardingFinished()) {
                        (activity as LoginActivity).hideKeyboard()
                    } else {
                        (activity as OnboardingActivity).hideKeyboard()
                    }
                    animateGone()
                    Handler().postDelayed({
                        animateUI()
                    }, 500)
                    Log.d("Debug", "email harusnya : " + viewModel.email)
                    dataBinding.textEmailValid.text = viewModel.email
                    isEmailValid = true
                } else {
                    dataBinding.textErrorSignupEmail.visibility = View.VISIBLE
                }
            }

        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingView.visibility = if (it) View.VISIBLE else View.GONE
            }
        })

        viewModel.registerResponse.observe(this, Observer { response ->
            response?.let {
//                viewModel.goToSuccess(dataBinding.root)
            }
        })
    }

    private fun registerSuccess() {
//        viewModel.goToSuccess(dataBinding.root)
//        val action = SignupOnboardingFragmentDirections.actionToSignupOnboardingSuccessFragment()
//        Navigation.findNavController(dataBinding.root).navigate(action)
    }

    private fun animateUI() {

        if (android.os.Build.VERSION_CODES.BASE >= android.os.Build.VERSION_CODES.O) {
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

            TransitionManager.beginDelayedTransition(dataBinding.container, transition)

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
        } else {
            dataBinding.textYourEmail.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textEmailValid.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textSignupFullName.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldSignupFullName.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textSignupPhone.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldSignupPhone.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.textSignupPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldSignupPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textSignupConfirmPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldSignupConfirmPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.buttonSignupSignup.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.textErrorFullName.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorPhone.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorSignupPassword.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorSignupConfirmPassword.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }
    }

    private fun animateGone() {
        dataBinding.container.transitionToEnd()
        if (android.os.Build.VERSION_CODES.BASE >= android.os.Build.VERSION_CODES.O) {
            val transition = Fade()
            transition.duration = 500

            transition.addTarget(dataBinding.textSignupEmail)
            transition.addTarget(dataBinding.textFieldSignupEmail)
            transition.addTarget(dataBinding.textErrorSignupEmail)

            transition.addTarget(dataBinding.buttonSignupSignup)

            TransitionManager.beginDelayedTransition(dataBinding.container, transition)

            //set gone
            dataBinding.textSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.textFieldSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.textErrorSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.textOr.setTransitionVisibility(View.GONE)
            dataBinding.buttonSignupAnotherWay.setTransitionVisibility(View.GONE)
            dataBinding.buttonSignupSignup.setTransitionVisibility(View.INVISIBLE)
        } else {
            dataBinding.textSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textOr.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.buttonSignupAnotherWay.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.buttonSignupSignup.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
        }


    }
}