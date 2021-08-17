package com.tsab.pikapp.view.menuCategory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tsab.pikapp.R
import com.tsab.pikapp.viewmodel.categoryMenu.CategoryViewModel

class CategoryNavigation : AppCompatActivity() {
    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        viewModel.fetchCategoryList()
    }
}