package com.bejohen.pikapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.bejohen.pikapp.util.SessionManager
import com.bejohen.pikapp.view.HomeActivity
import com.bejohen.pikapp.view.OnboardingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application): AndroidViewModel(application), CoroutineScope {

    private var sessionManager = SessionManager(getApplication())
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun createToastShort(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_SHORT).show()
    }

    fun createToastLong(m: String) {
        Toast.makeText(getApplication(), m, Toast.LENGTH_LONG).show()
    }
    fun clearSession(context: Context) {
        sessionManager.logout()
        goToOnboardingFromHome(context)
    }

    private fun goToOnboardingFromHome(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context.startActivity(onboardingActivity)
        (context as HomeActivity).finish()
    }

}