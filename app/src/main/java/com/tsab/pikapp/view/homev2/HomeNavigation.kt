package com.tsab.pikapp.view.homev2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.tsab.pikapp.R
import com.tsab.pikapp.databinding.ActivityHomeNavigationBinding
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.view.homev2.menu.MenuFragment
import com.tsab.pikapp.view.homev2.menu.OtherFragment
import com.tsab.pikapp.view.homev2.menu.PromoFragment
import com.tsab.pikapp.view.homev2.menu.TransactionFragment
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel
import kotlinx.android.synthetic.main.activity_home_navigation.*

class HomeNavigation : AppCompatActivity() {

    private val transactionFragment = TransactionFragment()
    private val menuFragment = MenuFragment()
    private val promoFragment = PromoFragment()
    private val otherFragment = OtherFragment()
    private val sessionManager = SessionManager()
    val model: CategoryViewModel by viewModels()

    private lateinit var dataBinding: ActivityHomeNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = ActivityHomeNavigationBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        when {
            sessionManager.getHomeNav() == 0 -> {
                replaceFragment(transactionFragment)
                bottom_navigation.setSelectedItemId(R.id.nav_transaction)
            }
            sessionManager.getHomeNav() == 1 -> {
                replaceFragment(menuFragment)
                bottom_navigation.setSelectedItemId(R.id.nav_menu)
            }
            sessionManager.getHomeNav() == 2 -> {
                replaceFragment(promoFragment)
                bottom_navigation.setSelectedItemId(R.id.nav_promo)
            }
            sessionManager.getHomeNav() == 3 -> {
                replaceFragment(otherFragment)
                bottom_navigation.setSelectedItemId(R.id.nav_other)
            }
        }


        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_transaction -> replaceFragment(transactionFragment)
                R.id.nav_menu -> replaceFragment(menuFragment)
                R.id.nav_promo -> replaceFragment(promoFragment)
                R.id.nav_other -> replaceFragment(otherFragment)
            }
            true
        }
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
            sessionManager.setHomeNav(0)
        }
    }
}