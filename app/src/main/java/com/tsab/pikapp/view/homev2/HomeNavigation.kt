package com.tsab.pikapp.view.homev2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.tsab.pikapp.R
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tsab.pikapp.databinding.ActivityHomeNavigationBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.LoginV2Activity
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.view.homev2.menu.OtherFragment
import com.tsab.pikapp.view.homev2.menu.PromoFragment
import com.tsab.pikapp.view.homev2.menu.TransactionFragment
import com.tsab.pikapp.view.onboarding.login.LoginV2First
import com.tsab.pikapp.viewmodel.homev2.HomeNavigationViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*

class HomeNavigation : AppCompatActivity() {

    private val transactionFragment = TransactionFragment()
    private val menuFragment = MenuFragment()
    private val promoFragment = PromoFragment()
    private val otherFragment = OtherFragment()

    private lateinit var viewModel: HomeNavigationViewModel
    private lateinit var dataBinding: ActivityHomeNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeNavigationViewModel::class.java)
        dataBinding = ActivityHomeNavigationBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        replaceFragment(transactionFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_transaction -> {
                    logout_button.visibility = View.GONE
                    replaceFragment(transactionFragment)
                }
                R.id.nav_menu -> {
                    logout_button.visibility = View.GONE
                    replaceFragment(menuFragment)
                }
                R.id.nav_promo -> {
                    logout_button.visibility = View.GONE
                    replaceFragment(promoFragment)
                }
                R.id.nav_other -> {
                    replaceFragment(otherFragment)
                    logout_button.visibility = View.VISIBLE
                }
            }
            true
        }

        dataBinding.logoutButton.setOnClickListener {
            viewModel.logout()
        }

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
                val intent = Intent(this, LoginV2Activity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        if (bottom_navigation.getSelectedItemId()==R.id.nav_transaction) {
            finishAffinity()
        } else {
            bottom_navigation.setSelectedItemId(R.id.nav_transaction)
        }
    }
}