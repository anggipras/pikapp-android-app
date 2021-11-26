package com.tsab.pikapp.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchItem
import com.tsab.pikapp.viewmodel.menu.MenuViewModel
import kotlinx.android.synthetic.main.activity_advance_menu.*

class AdvanceMenuActivity : AppCompatActivity() {
    companion object {
        // Intent extras identifier.
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_ADD = 0
        const val TYPE_EDIT = 1
        const val MENU_LIST = "menu_data"
    }

    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.bottom_up, R.anim.no_animation)
        setContentView(R.layout.activity_advance_menu)

        val navHostFragment = advancedMenuNavHost as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_advance_menu)
        navController = navHostFragment.navController

        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        if (type == TYPE_ADD) {
            viewModel.setAddOrEdit(false)
            navGraph.startDestination = R.id.updateMenuAddAdvFragment
        } else if (type == TYPE_EDIT) {
            val menuListData = intent.getSerializableExtra(MENU_LIST) as? SearchItem
            viewModel.setAddOrEdit(true)
            if (menuListData != null) {
                viewModel.setMenu(menuListData)
            }
            navGraph.startDestination = R.id.updateMenuEditAdvFragment
        }
        navController.graph = navGraph
    }
}