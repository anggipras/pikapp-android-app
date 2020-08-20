package com.bejohen.pikapp.viewmodel.categoryProduct

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bejohen.pikapp.models.model.ItemHomeCategory
import com.bejohen.pikapp.models.PikappDatabase
import com.bejohen.pikapp.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application): BaseViewModel(application) {

    val categoryData = MutableLiveData<ItemHomeCategory>()

    fun fetch(uuid: Int) {
        fetchFromDB(uuid)
    }

    private fun fetchFromDB(uuid: Int) {
        launch {
            val data = PikappDatabase(getApplication()).pikappDao().getHomeCategory(uuid)
            categoryRetrieved(data)
        }
    }

    private fun categoryRetrieved(data: ItemHomeCategory) {
        categoryData.value = data
    }
}