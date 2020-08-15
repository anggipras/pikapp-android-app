package com.bejohen.pikapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.bejohen.pikapp.R
import com.bejohen.pikapp.viewmodel.SplashViewModel
import com.bejohen.pikapp.viewmodel.userExclusive.UserExclusiveViewModel
import kotlinx.android.synthetic.main.activity_user_exclusive.*

class UserExclusiveActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var viewModel: UserExclusiveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_exclusive)

        viewModel = ViewModelProviders.of(this).get(UserExclusiveViewModel::class.java)

        val navHostFragment = navHostUserExclusive as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_user_exclusive)
        navController = navHostFragment.navController

        val destination = if (viewModel.getUserExclusiveFormFinished()) R.id.userExclusiveHomeFragment else R.id.userExclusiveFormFragment
        navGraph.startDestination = destination
        navController.graph = navGraph
    }
}