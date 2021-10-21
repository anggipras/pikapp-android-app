package com.tsab.pikapp.viewmodel.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {

    private val mutableDateSelection = MutableLiveData("")
    val dateSelection: LiveData<String> get() = mutableDateSelection

    private val mutableStartDate = MutableLiveData("")
    val startDate: LiveData<String> get() = mutableStartDate

    private val mutableEndDate = MutableLiveData("")
    val endDate: LiveData<String> get() = mutableEndDate

    fun getDateSelection(Date: String) {
        mutableDateSelection.value = Date
    }

    fun getStartDate(startDate: String) {
        mutableStartDate.value = startDate
    }

    fun getEndDate(endDate: String) {
        mutableEndDate.value = endDate
    }
}