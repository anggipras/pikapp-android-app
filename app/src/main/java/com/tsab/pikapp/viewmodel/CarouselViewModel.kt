package com.tsab.pikapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tsab.pikapp.util.SessionManager

class CarouselViewModel : ViewModel() {
    private val mutableCurrentPage = MutableLiveData(0)
    val currentPage: LiveData<Int> get() = mutableCurrentPage

//    private var sessionManager = SessionManager()

    fun setCurrentPage(currentPage: Int) {
        mutableCurrentPage.value = currentPage
    }

    fun nextPage() {
        mutableCurrentPage.value = mutableCurrentPage.value?.plus(1)
    }

    fun previousPage() {
        mutableCurrentPage.value = mutableCurrentPage.value?.minus(1)
    }

//    fun setFirstApp() {
//        val firstApp = sessionManager.setFirstApp(1)
//    }
}