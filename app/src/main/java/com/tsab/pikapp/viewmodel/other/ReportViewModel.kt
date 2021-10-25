package com.tsab.pikapp.viewmodel.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {

    //Showing Date on Mobile
    private val mutableDateSelection = MutableLiveData("")
    val dateSelection: LiveData<String> get() = mutableDateSelection

    private val mutableStartDate = MutableLiveData("")
    val startDate: LiveData<String> get() = mutableStartDate

    private val mutableEndDate = MutableLiveData("")
    val endDate: LiveData<String> get() = mutableEndDate

    //Sending ISO String 8601 to webview
    private val mutableStartISO = MutableLiveData("")
    val startISO: LiveData<String> get() = mutableStartISO

    private val mutableEndISO = MutableLiveData("")
    val endISO: LiveData<String> get() = mutableEndISO

    fun getDateSelection(date: String) {
        mutableDateSelection.value = date
    }

    fun getStartDate(startDate: String) {
        mutableStartDate.value = startDate
    }

    fun getEndDate(endDate: String) {
        mutableEndDate.value = endDate
    }

    fun getStartISO(startDate: String) {
        mutableStartISO.value = startDate
    }

    fun getEndISO(endDate: String) {
        mutableEndISO.value = endDate
    }
}