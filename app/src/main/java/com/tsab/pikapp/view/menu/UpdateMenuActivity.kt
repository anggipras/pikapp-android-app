package com.tsab.pikapp.view.menu

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.AdvanceMenu
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.activity_update_menu.*

class UpdateMenuActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_ADD = 0
        const val TYPE_EDIT = 1
    }

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_menu)

        val navHostFragment = updateMenuNavHost as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_update_menu)
        navController = navHostFragment.navController

        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        if (type == TYPE_ADD) {
            navGraph.startDestination = R.id.updateMenuAddFragment
        } else if (type == TYPE_EDIT) {
            navGraph.startDestination = R.id.updateMenuEditFragment
        }
        navController.graph = navGraph
    }
}