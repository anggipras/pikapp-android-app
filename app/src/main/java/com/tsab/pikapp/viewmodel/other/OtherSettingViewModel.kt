package com.tsab.pikapp.viewmodel.other

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtherSettingViewModel : ViewModel() {
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    fun setGender(bool: Boolean, gender: String) {
        _genderConfirmation.value = bool
        _genderSelection.value = gender
    }

    fun setBirthday(date: String?) {
        _birthdaySelection.value = date
    }

    fun confirmGender(bool: Boolean) {
        _genderDialogAlert.value = bool
    }

    fun restartFragment() {
        _genderConfirmation.value = false
        _genderDialogAlert.value = false
    }
}