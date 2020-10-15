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
import com.bejohen.pikapp.view.LoginActivity
import com.bejohen.pikapp.view.OnboardingActivity
import com.bejohen.pikapp.viewmodel.onboarding.login.SignupOnboardingViewModel


class SignupOnboardingFragment : Fragment() {

    private lateinit var viewModel: SignupOnboardingViewModel
    private lateinit var dataBinding: FragmentSignupOnboardingBinding

    private var isEmailValid = false
    private var shortAnimationDuration: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup_onboarding, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shortAnimationDuration = resources.getInteger(android.R.integer.config_longAnimTime)

        viewModel = ViewModelProviders.of(this).get(SignupOnboardingViewModel::class.java)


        dataBinding.buttonLogin.setOnClickListener {
            viewModel.goToLogin(dataBinding.root)
        }
        dataBinding.buttonOnboardingSignupSignup.setOnClickListener {
            if (isEmailValid) {
                sendRegister()
            } else {
                val email = dataBinding.textFieldOnboardingSignupEmail.text.toString().trim()
                Log.d("Debug", "email : " + email)
                viewModel.emailValidation(email)
            }

        }
        observeViewModel()
    }

    private fun sendRegister() {

        val fullName = dataBinding.textFieldOnboardingSignupFullName.text.toString().trim()
        val phone = dataBinding.textFieldOnboardingSignupPhone.text.toString().trim()
        val password = dataBinding.textFieldOnboardingSignupPassword.text.toString().trim()

        viewModel.register(fullName, phone, password)
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun observeViewModel() {

        viewModel.emailValid.observe(this, Observer { isValid ->
            isValid?.let {
                if (it) {
                    (activity as OnboardingActivity).hideKeyboard()
                    animateGone()
                    Handler().postDelayed({
                        animateUI()
                    }, 500)
                    Log.d("Debug", "email harusnya : " + viewModel.email)
                    dataBinding.textOnboardingEmailValid.text = viewModel.email
                    isEmailValid = true
                } else {
                    dataBinding.textErrorOnboardingSignupEmail.visibility = View.VISIBLE
                }
            }

        })

        viewModel.fullNameError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorOnboardingFullName.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.phoneError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorOnboardingPhone.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.passwordError.observe(this, Observer { t ->
            t?.let {
                dataBinding.textErrorOnboardingSignupPassword.apply {
                    visibility = if (t.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                    text = t
                }
            }
        })

        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                dataBinding.loadingViewOnboardingSignup.visibility =
                    if (it) View.VISIBLE else View.GONE
                dataBinding.buttonOnboardingSignupSignup.isClickable = !it
                (activity as OnboardingActivity).hideKeyboard()
            }
        })

        viewModel.registerResponse.observe(this, Observer { response ->
            response?.let {
                viewModel.loginProcess()
            }
        })

        viewModel.registerErrorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })

        viewModel.loginResponse.observe(this, Observer {response ->
            if (response.newEvent!!) {
                viewModel.goToUserExclusive(activity as OnboardingActivity)
            } else {
                viewModel.goToHome(activity as OnboardingActivity)
            }
        })

        viewModel.loginErrorResponse.observe(this, Observer { errorResponse ->
            errorResponse?.let {
                errorResponse.errMessage?.let { err -> viewModel.createToast(err) }
            }
        })
    }

    private fun animateUI() {

        if (android.os.Build.VERSION_CODES.BASE >= android.os.Build.VERSION_CODES.O) {
            val transition = Fade()
            transition.duration = 800

            transition.addTarget(dataBinding.textOnboardingYourEmail)
            transition.addTarget(dataBinding.textOnboardingEmailValid)

            transition.addTarget(dataBinding.textOnboardingSignupFullName)
            transition.addTarget(dataBinding.textFieldOnboardingSignupFullName)
            transition.addTarget(dataBinding.textErrorOnboardingFullName)
            transition.addTarget(dataBinding.textOnboardingSignupPhone)
            transition.addTarget(dataBinding.textFieldOnboardingSignupPhone)
            transition.addTarget(dataBinding.textErrorOnboardingPhone)

            transition.addTarget(dataBinding.textOnboardingSignupPassword)
            transition.addTarget(dataBinding.textFieldOnboardingSignupPassword)
            transition.addTarget(dataBinding.textErrorOnboardingSignupPassword)
//            transition.addTarget(dataBinding.textOnboardingSignupConfirmPassword)
//            transition.addTarget(dataBinding.textFieldOnboardingSignupConfirmPassword)
//            transition.addTarget(dataBinding.textErrorOnboardingSignupConfirmPassword)
            transition.addTarget(dataBinding.buttonOnboardingSignupSignup)
            transition.addTarget(dataBinding.loginSection)
            transition.addTarget(dataBinding.buttonOnboardingSignupAnotherWay)

            TransitionManager.beginDelayedTransition(dataBinding.containerOnboarding, transition)

            //set visible
            dataBinding.textOnboardingYourEmail.setTransitionVisibility(View.VISIBLE)
            dataBinding.textOnboardingEmailValid.setTransitionVisibility(View.VISIBLE)
            dataBinding.textOnboardingSignupFullName.setTransitionVisibility(View.VISIBLE)
            dataBinding.textFieldOnboardingSignupFullName.setTransitionVisibility(View.VISIBLE)

            dataBinding.textOnboardingSignupPhone.setTransitionVisibility(View.VISIBLE)
            dataBinding.textFieldOnboardingSignupPhone.setTransitionVisibility(View.VISIBLE)

            dataBinding.textOnboardingSignupPassword.setTransitionVisibility(View.VISIBLE)
            dataBinding.textFieldOnboardingSignupPassword.setTransitionVisibility(View.VISIBLE)
//            dataBinding.textOnboardingSignupConfirmPassword.setTransitionVisibility(View.VISIBLE)
//            dataBinding.textFieldOnboardingSignupConfirmPassword.setTransitionVisibility(View.VISIBLE)
            dataBinding.buttonOnboardingSignupSignup.setTransitionVisibility(View.VISIBLE)

            //set invisible
            dataBinding.textErrorOnboardingFullName.setTransitionVisibility(View.INVISIBLE)
            dataBinding.textErrorOnboardingPhone.setTransitionVisibility(View.INVISIBLE)
            dataBinding.textErrorOnboardingSignupPassword.setTransitionVisibility(View.INVISIBLE)
//            dataBinding.textErrorOnboardingSignupConfirmPassword.setTransitionVisibility(View.INVISIBLE)
        } else {
            dataBinding.textOnboardingYourEmail.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textOnboardingEmailValid.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textOnboardingSignupFullName.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldOnboardingSignupFullName.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textOnboardingSignupPhone.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldOnboardingSignupPhone.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.textOnboardingSignupPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldOnboardingSignupPassword.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
//            dataBinding.textOnboardingSignupConfirmPassword.apply {
//                visibility = View.VISIBLE
//                animate()
//                    .setDuration(shortAnimationDuration.toLong())
//                    .setListener(null)
//            }
//            dataBinding.textFieldOnboardingSignupConfirmPassword.apply {
//                visibility = View.VISIBLE
//                animate()
//                    .setDuration(shortAnimationDuration.toLong())
//                    .setListener(null)
//            }
            dataBinding.buttonOnboardingSignupSignup.apply {
                visibility = View.VISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.textErrorOnboardingFullName.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorOnboardingPhone.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textErrorOnboardingSignupPassword.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
//            dataBinding.textErrorOnboardingSignupConfirmPassword.apply {
//                visibility = View.INVISIBLE
//                animate()
//                    .setDuration(shortAnimationDuration.toLong())
//                    .setListener(null)
//            }
        }
    }

    private fun animateGone() {

        dataBinding.containerOnboarding.transitionToEnd()

        if (android.os.Build.VERSION_CODES.BASE >= android.os.Build.VERSION_CODES.O) {

            val transition = Fade()
            transition.duration = 500

            transition.addTarget(dataBinding.textOnboardingSignupEmail)
            transition.addTarget(dataBinding.textFieldOnboardingSignupEmail)
            transition.addTarget(dataBinding.textErrorOnboardingSignupEmail)

            transition.addTarget(dataBinding.buttonOnboardingSignupSignup)

            TransitionManager.beginDelayedTransition(dataBinding.containerOnboarding, transition)

            //set gone
            dataBinding.textOnboardingSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.textFieldOnboardingSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.textErrorOnboardingSignupEmail.setTransitionVisibility(View.GONE)
            dataBinding.loginSection.setTransitionVisibility(View.GONE)
            dataBinding.buttonOnboardingSignupAnotherWay.setTransitionVisibility(View.GONE)
            dataBinding.buttonOnboardingSignupSignup.setTransitionVisibility(View.INVISIBLE)
        } else {
            dataBinding.textOnboardingSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.textFieldOnboardingSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.textErrorOnboardingSignupEmail.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.loginSection.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }
            dataBinding.buttonOnboardingSignupAnotherWay.apply {
                visibility = View.GONE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

            dataBinding.buttonOnboardingSignupSignup.apply {
                visibility = View.INVISIBLE
                animate()
                    .setDuration(shortAnimationDuration.toLong())
                    .setListener(null)
            }

        }
    }

}