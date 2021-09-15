package com.tsab.pikapp.viewmodel.menu

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import com.tsab.pikapp.R
import com.tsab.pikapp.models.model.SearchList
import com.tsab.pikapp.models.network.PikappApiService
import com.tsab.pikapp.util.SessionManager
import com.tsab.pikapp.util.SharedPreferencesUtil
import com.tsab.pikapp.viewmodel.BaseViewModel
import io.reactivex.disposables.CompositeDisposable

class EditMenuViewModel(application: Application) : BaseViewModel(application) {
    private val mutableMenuList = MutableLiveData<SearchList>()
    val menuList : LiveData<SearchList> get() = mutableMenuList
    fun setMenu(menu: SearchList) {
        mutableMenuList.value = menu
    }
}