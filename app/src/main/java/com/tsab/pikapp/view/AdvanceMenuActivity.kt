package com.tsab.pikapp.view

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.view.menu.EditMenuFragment
import com.tsab.pikapp.viewmodel.menu.EditMenuViewModel
import kotlinx.android.synthetic.main.activity_advance_menu.*
import kotlinx.android.synthetic.main.activity_update_menu.*
import kotlinx.android.synthetic.main.category_menu_items_sort.*

class AdvanceMenuActivity : AppCompatActivity() {
    companion object {
        // Intent extras identifier.
        const val EXTRA_TYPE = "extra_type"
        const val TYPE_ADD = 0
        const val TYPE_EDIT = 1
        const val MENU_LIST = "menu_data"
    }

//    private val viewModel: AdvanceMenuViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private val viewModel: EditMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_menu)

        val navHostFragment = advancedMenuNavHost as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.nav_advance_menu)
        navController = navHostFragment.navController

        val type = intent.getIntExtra(EXTRA_TYPE, 0)
        if (type == TYPE_ADD) {
            navGraph.startDestination = R.id.updateMenuAddAdvFragment
        } else if (type == TYPE_EDIT) {
            val menuListData = intent.getSerializableExtra(MENU_LIST) as? SearchList
            if (menuListData != null) {
                viewModel.setMenu(menuListData)
            }
            navGraph.startDestination = R.id.updateMenuEditAdvFragment
        }
        navController.graph = navGraph

//        // Set product ID in view model.
//        val productId = intent.getStringExtra(PRODUCT_ID)
//        viewModel.setProductId(productId)
//        viewModel.fetchAdvanceMenuData()
//
//        // Send result when push success.
//        viewModel.isPushSuccess.observe(this, Observer { isPushSuccess ->
//            if (isPushSuccess) {
//                Intent(this, UpdateMenuActivity::class.java).apply {
//                    putExtra(RESULT_DATA, viewModel.advanceMenuList.value.toString())
//                    setResult(RESULT_OK, this)
//                    startActivity(this)
//                    finish()
//                }
//            }
//        })
    }
}