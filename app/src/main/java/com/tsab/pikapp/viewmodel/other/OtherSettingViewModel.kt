package com.tsab.pikapp.viewmodel.other

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtherSettingViewModel : ViewModel() {
    //Profile Setting
    val _genderConfirmation = MutableLiveData<Boolean>()
    val _genderSelection = MutableLiveData<String>()
    val _genderDialogAlert = MutableLiveData<Boolean>()
    val _birthdaySelection = MutableLiveData<String>()

    //Information Setting
    val _restaurantBanner = MutableLiveData(Uri.EMPTY)
    val _restaurantLogo = MutableLiveData(Uri.EMPTY)
    val _restaurantName = MutableLiveData<String>()
    val _restaurantAddress = MutableLiveData<String>()

    //Pin Setting
    val _newPin = MutableLiveData<String>()

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

    //Information Setting Method
    fun setBannerImg(banner: Uri?) {
        _restaurantBanner.value = banner
    }

    fun setLogoImg(logo: Uri?) {
        _restaurantLogo.value = logo
    }

    fun setNewMerchNameAndAddress(merchName: String?, merchAddress: String?) {
        _restaurantName.value = merchName
        _restaurantAddress.value = merchAddress
    }

    //Pin Setting Method
    fun setNewPin(pin: String?) {
        _newPin.value = pin
    }
}