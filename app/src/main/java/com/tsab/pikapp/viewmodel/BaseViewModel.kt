package com.tsab.pikapp.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope {

    private var sessionManager = SessionManager(getApplication())
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun createToastShort(application: Application, m: String) {
        Toast.makeText(application, m, Toast.LENGTH_SHORT).show()
    }

    fun createToastShortFragment(activity: Activity, m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    fun createToastLong(application: Application, m: String) {
        Toast.makeText(application, m, Toast.LENGTH_LONG).show()
    }

    fun createToastLongFragment(activity: Activity, m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_LONG).show()
    }

    fun clearSession(context: Context) {
        sessionManager.logout()
    }

    fun goToOnboardingFromTransaction(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context.startActivity(onboardingActivity)
        (context as TransactionActivity).finish()
    }

    fun goToOnboardingFromHome(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context.startActivity(onboardingActivity)
        (context as HomeActivity).finish()
    }

    fun goToOnboardingFromOrderList(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context.startActivity(onboardingActivity)
        (context as OrderListActivity).finish()
    }

    fun goToOnboardingFromStoreOrderList(context: Context) {
        val onboardingActivity = Intent(context, OnboardingActivity::class.java)
        context.startActivity(onboardingActivity)
        (context as StoreOrderListActivity).finish()
    }
}